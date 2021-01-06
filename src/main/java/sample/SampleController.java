package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class SampleController {
  public Label helloWorld;

  public void sayHelloWorld(final ActionEvent actionEvent) {
    helloWorld.setText("Hello World!");
    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hello World!?");
    alert.showAndWait();
  }
}
