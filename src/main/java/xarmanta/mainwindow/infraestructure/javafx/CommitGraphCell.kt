package xarmanta.mainwindow.infraestructure.javafx

import javafx.beans.InvalidationListener
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.fxml.FXMLLoader
import javafx.scene.canvas.Canvas
import javafx.scene.control.TableCell
import org.eclipse.jgit.revplot.PlotCommit
import xarmanta.mainwindow.infraestructure.jgit.JavaFxLane
import xarmanta.mainwindow.model.Commit
import java.io.IOException

/**
 * Celdas de la grafica en el tableview
 */
class CommitGraphCell(val renderer : JavaFxPlotRenderer) : TableCell<Commit, Commit>() {
    @FXML
    var group: Group? = null
    @FXML
    var canvas: Canvas? = null

    private var mLLoader = FXMLLoader(javaClass.getResource("/ListCell.fxml"))

    override fun updateItem(commit: Commit?, empty: Boolean) {
        super.updateItem(commit, empty);
        if (commit == null) {
            graphic = null;
            return;
        }

        mLLoader = FXMLLoader(javaClass.getResource("/ListCell.fxml"))
        mLLoader.setController(this)
        try {
            mLLoader.load<Any>()
            renderer.paint(this, commit.commit as PlotCommit<JavaFxLane>, commit.type)
            setGraphic(canvas)
            val listener = InvalidationListener{
                if (item != null) {
                    updateItem(item, true)
                }
            }
            canvas!!.widthProperty().addListener(listener);
            canvas!!.heightProperty().addListener(listener);
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
