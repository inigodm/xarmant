package xarmanta.mainwindow.infraestructure

import javafx.fxml.FXML
import javafx.scene.Group
import javafx.fxml.FXMLLoader
import javafx.scene.control.TableCell
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import xarmanta.mainwindow.model.Commit
import java.io.IOException


class CommitGraphCell : TableCell<Commit, Commit>() {
    @FXML
    var group: Group? = null

    private var mLLoader = FXMLLoader(javaClass.getResource("/ListCell.fxml"))

    override fun updateItem(commit: Commit?, empty: Boolean) {
        super.updateItem(commit, empty);
        if (commit == null) {
            setGraphic(null);
            return;
        }

        mLLoader = FXMLLoader(javaClass.getResource("/ListCell.fxml"))
        mLLoader.setController(this)
        try {
            mLLoader.load<Any>()
            val line = Line(0.0, 0.0, 0.0, 30.0)
            line.stroke = Color.RED;
            group!!.children.add(line)
            setGraphic(group)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
