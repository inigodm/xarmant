package xarmanta.mainwindow.infraestructure.javafx

import javafx.beans.InvalidationListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.control.TableCell
import xarmanta.mainwindow.model.Commit
import java.io.IOException

class CommitCell :  TableCell<Commit, Commit>() {
    @FXML
    var group: Group? = null
    @FXML
    var canvas: Canvas? = null

    private var mLLoader = FXMLLoader(javaClass.getResource("/ListCell.fxml"))

    override fun updateItem(commit: Commit?, empty: Boolean) {
        super.updateItem(commit, empty)
        if (commit == null) {
            graphic = null;
            return;
        }
        mLLoader = FXMLLoader(javaClass.getResource("/ListCell.fxml"))
        mLLoader.setController(this)
        try {
            mLLoader.load<Any>()
            JavaFXShapeDrawer(canvas!!.graphicsContext2D).drawCell(commit)
            graphic = canvas
            refreshInvalidationListener()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun refreshInvalidationListener() {
        val listener = InvalidationListener {
            if (item != null) {
                updateItem(item, true)
            }
        }
        canvas!!.widthProperty().addListener(listener);
        canvas!!.heightProperty().addListener(listener);
    }
}
