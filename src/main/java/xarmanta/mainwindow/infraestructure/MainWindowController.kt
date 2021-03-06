package xarmanta.mainwindow.infraestructure

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.StackPane
import javafx.scene.text.TextFlow
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import xarmanta.shared.KotlinAsyncRunner
import javafx.scene.control.ButtonType
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Alert
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.DirectoryChooser
import javafx.util.Callback
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.fxmisc.richtext.CodeArea
import xarmanta.mainwindow.application.Clone
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit
import java.net.URL

import xarmanta.mainwindow.infraestructure.jgit.JavaFxPlotRenderer
import xarmanta.mainwindow.model.FileChanges
import java.io.*


class MainWindowController(val configManager: ConfigManager = ConfigManager(), val plotRenderer: JavaFxPlotRenderer = JavaFxPlotRenderer()) {
    // Unop de estos dos, o el XGit o su contexto, sobran, no tengo claro aun cual
    var git : XGit? = null
    var context: GitContext? = null
    //FXML bindings
    lateinit var console: TextFlow
    lateinit var root: StackPane
    lateinit var vBox: VBox
    lateinit var btnPush: Button
    lateinit var btnPull: Button
    lateinit var column2: TableColumn<Commit, Commit>
    lateinit var column3: TableColumn<Commit, String>
    lateinit var files: TableColumn<Commit, String>
    lateinit var filesChanges: TableColumn<FileChanges, String>
    lateinit var table: TableView<Commit>
    lateinit var filesInObjectId: TableView<FileChanges>
    lateinit var recentRepos: Menu
    lateinit var fileContent: CodeArea
    // Observable para saber si hay, o no, algun repo de git abierto en la app
    var isAnyRepoOpen = SimpleBooleanProperty(false)
    var isAnyRecentRepo = SimpleBooleanProperty(false)
    // Cosas que se levantan en el progressiondicator de las tareas largas
    val pi = ProgressIndicator(-1.0)
    val blockingLabel = Label("")
    private var monitor = LabelProgressMonitor(blockingLabel)
    val box = HBox(pi, blockingLabel)
    //TA-DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA!!!

    @FXML
    fun initialize() {
        monitor = LabelProgressMonitor(blockingLabel)
        blockingLabel.text = "Wait while process ends..."
        box.alignment = Pos.CENTER
        table.setFixedCellSize(25.0);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
        table.setOnMouseClicked {
            getChangesBetween(table.selectionModel.selectedItems)
        }
        column2.cellFactory = Callback<TableColumn<Commit, Commit>, TableCell<Commit, Commit>> { CommitGraphCell(plotRenderer) }
        column2.cellValueFactory = Callback { ObservableCommit(it.value) }
        column3.cellValueFactory = PropertyValueFactory("description")
        filesChanges.cellValueFactory = PropertyValueFactory("changeType")
        files.cellValueFactory = PropertyValueFactory("filename")
        recentRepos.items.addAll(getRecentOpened())
        btnPull.disableProperty().bind( isAnyRepoOpen.not() )
        btnPush.disableProperty().bind( isAnyRepoOpen.not() )
        recentRepos.disableProperty().bind( isAnyRecentRepo.not() )
        loadHabitualRepos()
    }

    fun getChangesBetween(selectedItems: ObservableList<Commit>?) {
        runLongOperation{
            val fileChanges = when (selectedItems!!.size){
                0 -> emptyList<FileChanges>()
                1 -> git!!.getChangesInCommit(selectedItems!!.get(0))
                else -> git!!.getChangesBetween(selectedItems!!.get(1), selectedItems!!.get(0))
            }
            Platform.runLater{
                filesInObjectId.items.clear()
                fileChanges.forEach { filesInObjectId.items.add(it) }
            }
        }
    }

    private fun getRecentOpened(): List<MenuItem> {
        return emptyList()
    }

    fun openRepository(actionEvent: ActionEvent?) {
        var dir: File? = null
        try {
            dir = chooseDirectory("Choose root of your local git repository")
            context = GitContext(null, dir)
            openRepo(context!!)
        } catch (e: RepositoryNotFoundException) {
            Alert(AlertType.ERROR, "$dir does not contain a valid git repository").showAndWait()
            openRepository(actionEvent)
        }
    }

    private fun openRepo(context: GitContext) {
        git = XGit(context!!, monitor).open()
        isAnyRepoOpen.set(true)
        loadGraph()
        configManager.saveContext(context!!)
        Platform.runLater {
            loadHabitualRepos()
        }
    }

    private fun loadHabitualRepos() {
        recentRepos.items.clear()
        configManager.openConfigFile()?.repos.forEach {
                ctxt -> recentRepos.items.add(createRecentRepoMenuItem(ctxt))
        }
        isAnyRecentRepo.set(recentRepos.items.isNotEmpty())
    }

    private fun createRecentRepoMenuItem(ctxt: GitContext): MenuItem {
        val mnu = MenuItem(ctxt.directory!!.path)
        mnu.setOnAction { openRepo(ctxt) }
        return mnu
    }

    fun cloneRepository(actionEvent: ActionEvent?) {
        val directory = chooseDirectory("Choose destination directory")
        if (directory != null) {
            val url = URL(askForAText("Insert repository's URL"))
            runLongOperation {
                context = GitContext(url.toString(), directory)
                git = Clone().execute(context!!, monitor)
                isAnyRepoOpen.set(true)
                loadGraph()
            }
        }
    }

    fun push(actionEvent: ActionEvent?) {
        runLongOperation { git?.push() }
    }

    fun pull(actionEvent: ActionEvent?) {
        runLongOperation { git?.pull() }
    }

    fun loadGraph() {
        runLongOperation {
            val commits = git?.reverseWalk()
            Platform.runLater{
                table.items.clear()
                commits?.forEach { table.items.add(it) }
            }
        }
    }

    private fun askForAText(title: String, headerText: String = "", contenText: String = ""): String? {
        val dialog = TextInputDialog();
        dialog.headerText = headerText;
        dialog.title = title;
        dialog.contentText = contenText;
        return dialog.showAndWait().orElse(null)
    }

    private fun chooseDirectory(title: String): File? {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File(System.getProperty("user.home"))
        directoryChooser.title = title
        return directoryChooser.showDialog(table.scene.window)
    }

    // Corre una tarea asincronamente en background y levanta una pantalla de 'cargando' mientras
    fun runLongOperation(operation: Runnable) {
        val toExecute = {
            Platform.runLater{
                showLoading()
            }
            try {
                operation.run()
            } catch (e: Throwable) {
                e.printStackTrace()
                Platform.runLater{
                    Alert(
                        AlertType.ERROR,
                        e.message,
                        ButtonType.OK
                    ).show()
                }
            } finally {
                Platform.runLater{
                    hideLoading()
                }
            }
        }
        val onFailure = {
            val alert = Alert(
                AlertType.WARNING,
                "Timeout doing the last command, would you like to retry it duplicating the timeout?",
                ButtonType.OK,
                ButtonType.CANCEL
            )
            alert.title = "Date format warning"
            var toRedo = false
            Platform.runLater{
                toRedo = alert.showAndWait().get() === ButtonType.OK
            }
            toRedo
        }
        KotlinAsyncRunner().runAsyncReThrowable(toExecute, onFailure)
    }

    fun showLoading() {
        vBox.setDisable(true)
        root.children.add(box)
    }

    fun hideLoading() {
        vBox.setDisable(false)
        root.children.removeLast()
    }

}

// Se encesitaba para el tableview... realmente no necesito que sean observables, pero yo que se....
class ObservableCommit(val commit: Commit): ObservableObjectValue<Commit> {
    override fun addListener(listener: ChangeListener<in Commit>?) {
        //no op
    }

    override fun addListener(listener: InvalidationListener?) {
        //no op
    }

    override fun removeListener(listener: InvalidationListener?) {
        //no op
    }

    override fun removeListener(listener: ChangeListener<in Commit>?) {
        //no op
    }

    override fun getValue(): Commit {
        return commit
    }

    override fun get(): Commit {
        return commit
    }

}
