package xarmanta.mainwindow.infraestructure

import javafx.fxml.FXML
import javafx.scene.Group
import javafx.fxml.FXMLLoader
import javafx.scene.control.TableCell
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import xarmanta.mainwindow.infraestructure.jgit.JavaFxPlotRenderer
import xarmanta.mainwindow.model.Commit
import java.io.IOException

/**
 * Celdas de la grafica en el tableview
 */
class CommitGraphCell(val renderer : JavaFxPlotRenderer) : TableCell<Commit, Commit>() {
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
            renderer.paint(this, commit.plotCommit!!)
            setGraphic(group)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
