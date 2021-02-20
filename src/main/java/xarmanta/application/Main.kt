package xarmanta.application

import javafx.application.Application
import kotlin.Throws
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.lang.Exception

class Main : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader()
        fxmlLoader.location = Thread.currentThread().contextClassLoader.getResource("./main.fxml")
        val root = fxmlLoader.load<Parent>()
        primaryStage.title = "Xarmanta"
        primaryStage.scene = Scene(root, 800.0, 600.0)
        primaryStage.scene.getStylesheets().add("stylesheet.css");
        primaryStage.show()
    }
}
