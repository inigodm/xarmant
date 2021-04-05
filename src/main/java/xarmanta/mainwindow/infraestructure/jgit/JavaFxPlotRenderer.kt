package xarmanta.mainwindow.infraestructure.jgit

import javafx.scene.paint.Color
import javafx.scene.paint.Color.BLACK
import javafx.scene.text.Text
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.AbstractPlotRenderer
import org.eclipse.jgit.revplot.PlotCommit
import xarmanta.mainwindow.infraestructure.CommitGraphCell
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color.RED
import javafx.scene.text.Font
import org.eclipse.jgit.revplot2.XarmantAbstractPlotRenderer
import xarmanta.mainwindow.model.CommitType


class JavaFxPlotRenderer() : XarmantAbstractPlotRenderer<JavaFxLane>() {
    var cell: CommitGraphCell? = null

    var gc : GraphicsContext? = null
    var color : Color = BLACK

    fun paint(cell: CommitGraphCell, commit: PlotCommit<JavaFxLane>?, type: CommitType) {
        this.cell = cell
        gc = cell.canvas!!.graphicsContext2D
        gc!!.setLineWidth(5.0);
        paintCommit(commit, 20)
    }
    /**
     * Draw a decoration for the Ref ref at x,y
     *
     * @param x
     * left
     * @param y
     * top
     * @param ref
     * A peeled ref
     * @return width of label in pixels
     */
    override fun drawLabel(x: Int, y: Int, ref: Ref?): Int {
        gc!!.setLineWidth(1.0);
        val font = Font.font("Arial", 12.0);
        val texth = 20.0
        val name = ref?.name ?: "label"
        var txt = ""
        if (name.startsWith(Constants.R_HEADS)) {
            txt = name.substringAfter(Constants.R_HEADS);
        } else if (name.startsWith(Constants.R_REMOTES)) {
            txt = name.substringAfter(Constants.R_REMOTES);
        } else if (name.startsWith(Constants.R_TAGS)) {
            txt = name.substringAfter(Constants.R_TAGS);
        } else {
            // Whatever this would be
            txt = if (name.startsWith(Constants.R_REFS))
                name.substring(Constants.R_REFS.length)
            else name // HEAD and such
        }
        val innerColor = Color((color.red * 0.9f), (color.green * 0.9f), (color.blue * 0.9f), 0.5)
        if (ref!!.peeledObjectId != null) {
            //color = Color((color.red * 0.9f), (color.green * 0.9f), (color.blue * 0.9f), 0.9)
        }
        val arcSize: Double = texth / 4
        //if (txt.length > 12) txt = txt.substring(0, 11) + "\u2026"
        val text = Text(txt)
        text.font = font
        gc!!.font = font
        val x0 = x - arcSize
        val y0 = y - text.layoutBounds.height/2
        gc!!.fill = innerColor
        gc!!.fillRoundRect(x0, y0, text.layoutBounds.width + arcSize * 2, text.layoutBounds.height , 5.0, 5.0)
        gc!!.stroke = color
        gc!!.strokeRoundRect(x0, y0, text.layoutBounds.width + arcSize * 2, text.layoutBounds.height , 5.0, 5.0)
        gc!!.stroke = BLACK
        gc!!.strokeText(txt,  x.toDouble(), y.toDouble() + arcSize);
        cell!!.graphic = cell!!.group
        gc!!.setLineWidth(5.0);
        return (text.layoutBounds.width + arcSize * 2).toInt()
    }

    /**
     * Obtain the color reference used to paint this lane.
     *
     *
     * Colors returned by this method will be passed to the other drawing
     * primitives, so the color returned should be application specific.
     *
     *
     * If a null lane is supplied the return value must still be acceptable to a
     * drawing method. Usually this means the implementation should return a
     * default color.
     *
     * @param myLane
     * the current lane. May be null.
     * @return graphics specific color reference. Must be a valid color.
     */
    override fun laneColor(myLane: JavaFxLane?): Color {
        return myLane?.color ?: BLACK
    }

    /**
     * Draw a single line within this cell.
     *
     * @param color
     * the color to use while drawing the line.
     * @param x1
     * starting X coordinate, 0 based.
     * @param y1
     * starting Y coordinate, 0 based.
     * @param x2
     * ending X coordinate, 0 based.
     * @param y2
     * ending Y coordinate, 0 based.
     * @param width
     * number of pixels wide for the line. Always at least 1.
     */
    override fun drawLine(col: Color?, x1: Int, y1: Int, x2: Int, y2: Int, width: Int) {
        color = col ?: BLACK
        gc!!.stroke = color
        gc!!.strokeLine(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
        cell!!.graphic = cell!!.group
    }

    /**
     * Draw a single commit dot.
     *
     *
     * Usually the commit dot is a filled oval in blue, then a drawn oval in
     * black, using the same coordinates for both operations.
     *
     * @param x
     * upper left of the oval's bounding box.
     * @param y
     * upper left of the oval's bounding box.
     * @param w
     * width of the oval's bounding box.
     * @param h
     * height of the oval's bounding box.
     */
    override fun drawCommitDot(x: Int, y: Int, w: Int, h: Int) {
        gc!!.stroke = color
        gc!!.fillOval(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
        cell!!.graphic = cell!!.group
    }

    /**
     * Draw a single boundary commit (aka uninteresting commit) dot.
     *
     *
     * Usually a boundary commit dot is a light gray oval with a white center.
     *
     * @param x
     * upper left of the oval's bounding box.
     * @param y
     * upper left of the oval's bounding box.
     * @param w
     * width of the oval's bounding box.
     * @param h
     * height of the oval's bounding box.
     */
    override fun drawBoundaryDot(x: Int, y: Int, w: Int, h: Int) {
        gc!!.stroke = Color.BLANCHEDALMOND
        gc!!.fillOval(x.toDouble(), y.toDouble(), h.toDouble(), h.toDouble())
        cell!!.graphic = cell!!.group
    }

    /**
     * Draw a single line of text.
     *
     *
     * The font and colors used to render the text are left up to the
     * implementation.
     *
     * @param msg
     * the text to draw. Does not contain LFs.
     * @param x
     * first pixel from the left that the text can be drawn at.
     * Character data must not appear before this position.
     * @param y
     * pixel coordinate of the baseline of the text. Implementations
     * must adjust this coordinate to account for the way their
     * implementation handles font rendering.
     */
    override fun drawText(msg: String?, x: Int, y: Int) {
        /*val y0: Double = (y - 20) / 2 + (cell!!.height - 20) / 2
        val t = Text(x.toDouble(), y0 + 20, msg)
        t.setFont(Font(20.0))
        t.fill = BLACK
        cell!!.group!!.children.add(t)
        cell!!.graphic = cell!!.group*/
    }
}

