package xarmanta.mainwindow.infraestructure

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
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
import javafx.stage.DirectoryChooser
import org.eclipse.jgit.errors.RepositoryNotFoundException
import xarmanta.mainwindow.application.Clone
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit
import java.net.URL


class MainWindowController {

    lateinit var console: TextFlow
    lateinit var root: StackPane
    lateinit var vBox: VBox
    lateinit var btnPush: Button
    lateinit var btnPull: Button
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
        } catch (e: RepositoryNotFoundException) {
            Alert(AlertType.ERROR, "$dir does not contain a valid git repository").showAndWait()
            openRepository(actionEvent)
        }
    }

    fun cloneRepository(actionEvent: ActionEvent?) {
        val directory = chooseDirectory("Choose destination directory")
        val url = URL(askForAText("Insert repository's URL"))
        runLongOperation {
            context = GitContext(url.toString(), directory)
            git = Clone().execute(context!!, monitor)
            isAnyRepoOpen.set(true)
        }
    }

    fun push(actionEvent: ActionEvent?) {
        runLongOperation { git?.push() }
    }

    fun pull(actionEvent: ActionEvent?) {
        runLongOperation { git?.pull() }
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
