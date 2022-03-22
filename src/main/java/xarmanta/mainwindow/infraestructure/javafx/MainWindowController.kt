package xarmanta.mainwindow.infraestructure.javafx

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.StackPane
import javafx.scene.text.TextFlow
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Alert
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.util.Callback
import org.eclipse.jgit.diff.Edit
import org.eclipse.jgit.diff.EditList
import org.eclipse.jgit.errors.RepositoryNotFoundException
import xarmanta.mainwindow.shared.ConfigManager
import xarmanta.mainwindow.infraestructure.LabelProgressMonitor
//import org.fxmisc.richtext.CodeArea
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.model.ConfigFile
import java.net.URL

import xarmanta.mainwindow.model.FileChanges
import xarmanta.mainwindow.model.GitContext
import xarmanta.mainwindow.shared.*
import xarmanta.mainwindow.shared.git.XGit
import java.io.*


class MainWindowController(val configManager: ConfigManager = ConfigManager(), val plotRenderer: JavaFxPlotRenderer = JavaFxPlotRenderer()) {
    // Unop de estos dos, o el XGit o su contexto, sobran, no tengo claro aun cual
    var git : XGit? = null
    var context: GitContext? = null
    //FXML bindings
    //lateinit var console: TextFlow
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
    lateinit var fileContent: TextFlow
    // Observable para saber si hay, o no, algun repo de git abierto en la app
    var isAnyRepoOpen = SimpleBooleanProperty(false)
    var isAnyRecentRepo = SimpleBooleanProperty(false)
    // Cosas que se levantan en el progressiondicator de las tareas largas
    val pi = ProgressIndicator(-1.0)
    val blockingLabel = Label("")
    private var monitor = LabelProgressMonitor(blockingLabel)
    val box = HBox(pi, blockingLabel)
    lateinit var runner: LongOperationRunner

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
        filesInObjectId.setOnMouseClicked {
            drawDiff(filesInObjectId.selectionModel.selectedItem)
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
        runner = LongOperationRunner(root, vBox, box)
        val config = configManager.openConfigFile()
        if (config.lastOpened != null) {
            openRepository(config.lastOpened!!)
            loadHabitualRepos(config, config.lastOpened!!)
        } else {
            loadHabitualRepos(config, null)
        }
    }

    fun drawDiff(selectedItem: FileChanges?) {
        if (selectedItem != null) {
            runner.runLongOperation {
                val drawable = git!!.buildDiff(selectedItem)
                Platform.runLater {
                    fileContent.children.clear()
                    drawable.oldFile.forEachIndexed { index, it ->  drawLine(it, drawable.editList, index, drawable.newFile) }
                }
            }
        }
    }

    fun drawLine(text: String, editList: EditList, lineIndex: Int, newFile: List<String>) {
        val edit = editList.firstOrNull { it.beginA <= lineIndex && it.endA >= lineIndex }
        val textToAdd = Text(text + "\n")
        if (edit != null) {
            println(edit)
            if (edit.endA != lineIndex && (edit.type == Edit.Type.DELETE  || edit.type == Edit.Type.REPLACE)) {
                textToAdd.fill = Color.RED
                fileContent.children.add(textToAdd)
            }
            if (edit.endA == lineIndex) {
                if (edit.type == Edit.Type.INSERT || edit.type == Edit.Type.REPLACE) {
                    newFile.subList(edit.beginB, edit.endB).forEach {
                        val newText = Text(it + "\n")
                        newText.fill = Color.GREEN
                        fileContent.children.add(newText)
                    }
                }
                fileContent.children.add(textToAdd)
            }
        } else {
            fileContent.children.add(textToAdd)
        }
    }

    fun getChangesBetween(selectedItems: ObservableList<Commit>?) {
        runner.runLongOperation{
            val fileChanges = when (selectedItems!!.size){
                0 -> emptyList<FileChanges>()
                1 -> git!!.getChangesInCommit(selectedItems[0])
                else -> git!!.getChangesBetween(selectedItems.takeLast(1)[0], selectedItems[0])
            }
            Platform.runLater{
                fileContent.children.clear()
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
            openRepository(GitContext(null, dir)!!)
        } catch (e: RepositoryNotFoundException) {
            Alert(AlertType.ERROR, "$dir does not contain a valid git repository").showAndWait()
            openRepository(actionEvent)
        }
    }

    private fun openRepository(ctxt: GitContext) {
        git = XGit(ctxt, monitor).open()
        isAnyRepoOpen.set(true)
        loadGraph()
        configManager.saveContext(ctxt)
        val config = configManager.openConfigFile()
        Platform.runLater {
            loadHabitualRepos(config, ctxt)
        }
    }

    private fun loadHabitualRepos(config: ConfigFile, context: GitContext?) {
        recentRepos.items.clear()
        config.repos.filter { it.directory != context?.directory }.forEach {
                recentRepos.items.add(createRecentRepoMenuItem(it))
        }
        isAnyRecentRepo.set(recentRepos.items.isNotEmpty())
    }

    private fun createRecentRepoMenuItem(ctxt: GitContext): MenuItem {
        val mnu = MenuItem(ctxt.directory!!.path)
        mnu.setOnAction { openRepository(ctxt) }
        return mnu
    }

    fun cloneRepository(actionEvent: ActionEvent?) {
        val directory = chooseDirectory("Choose destination directory")
        if (directory != null) {
            val url = URL(askForAText("Insert repository's URL"))
            runner.runLongOperation {
                context = GitContext(url.toString(), directory)
                git = XGit(context!!, monitor).clone()
                isAnyRepoOpen.set(true)
                loadGraph()
            }
        }
    }

    fun push(actionEvent: ActionEvent?) = runner.runLongOperation { git?.push() }

    fun pull(actionEvent: ActionEvent?) = runner.runLongOperation { git?.pull() }

    fun loadGraph() = runner.runLongOperation {
        val commits = git?.getGraph()
        Platform.runLater {
            table.items.clear()
            commits?.forEach { table.items.add(it) }
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
}

