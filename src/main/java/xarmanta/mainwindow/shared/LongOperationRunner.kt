package xarmanta.mainwindow.shared

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class LongOperationRunner(val root: StackPane, val vBox: VBox, val box : HBox) {
    // Corre una tarea asincronamente en background y levanta una pantalla de 'cargando' mientras
    fun runLongOperation(operation: Runnable) {
        val toExecute = {
            Platform.runLater {
                showLoading()
            }
            try {
                operation.run()
            } catch (e: Throwable) {
                e.printStackTrace()
                Platform.runLater {
                    Alert(
                        Alert.AlertType.ERROR,
                        e.message,
                        ButtonType.OK
                    ).show()
                }
            } finally {
                Platform.runLater {
                    hideLoading()
                }
            }
        }
        val onFailure = {
            val alert = Alert(
                Alert.AlertType.WARNING,
                "Timeout doing the last command, would you like to retry it duplicating the timeout?",
                ButtonType.OK,
                ButtonType.CANCEL
            )
            alert.title = "Warning"
            var toRedo = false
            Platform.runLater {
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
