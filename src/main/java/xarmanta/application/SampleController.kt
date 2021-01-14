package xarmanta.application

import javafx.event.ActionEvent
import javafx.scene.control.Alert
import javafx.scene.control.TextArea

class SampleController {
    lateinit var console: TextArea

    fun sayHelloWorld(actionEvent: ActionEvent?) {
        console.appendText("zurixe\n")
        val alert = Alert(Alert.AlertType.INFORMATION, "Hello World!?")
        alert.showAndWait()
    }
}
