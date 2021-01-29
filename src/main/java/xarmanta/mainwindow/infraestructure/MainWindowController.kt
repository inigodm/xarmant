package xarmanta.mainwindow.infraestructure

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableObjectValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.StackPane
import javafx.scene.text.TextFlow
import java.io.File
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
import xarmanta.mainwindow.application.Clone
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit
import java.net.URL


class MainWindowController {

    lateinit var console: TextFlow
    lateinit var root: StackPane
    lateinit var vBox: VBox
    lateinit var btnPush: Button
    lateinit var btnPull: Button
    lateinit var column1: TableColumn<Commit, String>
    lateinit var column2: TableColumn<Commit, Commit>
    lateinit var column3: TableColumn<Commit, String>
    lateinit var table: TableView<Commit>
    var git : XGit? = null
    var context: GitContext? = null
    var isAnyRepoOpen = SimpleBooleanProperty(false)
    val pi = ProgressIndicator(-1.0)
    val blockingLabel = Label("")
    private var monitor = LabelProgressMonitor(blockingLabel)
    val box = HBox(pi, blockingLabel)

    @FXML
    fun initialize() {
        monitor = LabelProgressMonitor(blockingLabel)
        blockingLabel.text = "Wait while process ends..."
        box.alignment = Pos.CENTER
        column2.cellFactory = Callback<TableColumn<Commit, Commit>, TableCell<Commit, Commit>> { CommitGraphCell() }
        column2.cellValueFactory = Callback { ObservableCommit(it.value) }
        column1.cellValueFactory = PropertyValueFactory("branch")
        column3.cellValueFactory = PropertyValueFactory("description")
        btnPull.disableProperty().bind( isAnyRepoOpen.not() )
        btnPush.disableProperty().bind( isAnyRepoOpen.not() )
    }

    fun openRepository(actionEvent: ActionEvent?) {
        var dir: File? = null
        try {
            dir = chooseDirectory("Choose root of your local git repository")
            context = GitContext(null, dir)
            git = XGit(context!!, monitor).open()
            isAnyRepoOpen.set(true)
            loadGraph()
        } catch (e: RepositoryNotFoundException) {
            Alert(AlertType.ERROR, "$dir does not contain a valid git repository").showAndWait()
            openRepository(actionEvent)
        }
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
        return directoryChooser.showDialog(console.scene.window)
    }

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
            alert.showAndWait().get() === ButtonType.OK
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
