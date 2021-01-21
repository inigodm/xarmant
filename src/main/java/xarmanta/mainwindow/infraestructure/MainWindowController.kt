package xarmanta.mainwindow.infraestructure

import javafx.application.Platform
import javafx.event.ActionEvent
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
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.errors.RepositoryNotFoundException


class MainWindowController {

    lateinit var console: TextFlow
    lateinit var root: StackPane
    lateinit var vBox: VBox
    private var blockingLabel: Label? = null
    var git : Git? = null

    fun openRepository(actionEvent: ActionEvent?) {
        try {
            git = Git.open(chooseDirectory("Choose root of your local git repository"))
        } catch (e: RepositoryNotFoundException) {
            println("Not a git repository")
        }
    }

    fun cloneRepository(actionEvent: ActionEvent?) {
        val directory = chooseDirectory("Choose destination directory")
        val url = askForATest("Insert repository's URL")
        runLongOperation {
            git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(directory)
                .setProgressMonitor(blockingLabel?.let { LabelProgressMonitor(it) })
                .call()
        }
    }

    private fun askForATest(title: String, headerText: String = "", contenText: String = ""): String? {
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
            } catch (e: Exception) {
                e.printStackTrace()
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
        val pi = ProgressIndicator(-1.0)
        blockingLabel = Label("Wait while process ends...")
        val box = HBox(pi, blockingLabel)
        box.alignment = Pos.CENTER
        vBox.setDisable(true)
        root.children.add(box)
    }

    fun hideLoading() {
        vBox.setDisable(false)
        blockingLabel = null
        root.children.removeLast()
    }

}
