package xarmanta.mainwindow.infraestructure.jgit

import javafx.scene.paint.Color
import javafx.scene.paint.Color.BLACK
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.text.Font
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.AbstractPlotRenderer
import org.eclipse.jgit.revplot.PlotCommit
import xarmanta.mainwindow.infraestructure.CommitGraphCell


class JavaFxPlotRenderer() : AbstractPlotRenderer<JavaFxLane, Color>() {
    var cell: CommitGraphCell? = null

    fun paint(cell: CommitGraphCell, commit: PlotCommit<JavaFxLane>) {
        this.cell = cell
        if (commit != null) {
            paintCommit(commit, 20)
        }
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
        val texth = 20.0
        val name = ref?.name ?: "label"
        var txt = name
        if (name.startsWith(Constants.R_HEADS)) {
            txt = name.substringAfter(Constants.R_HEADS);
        } else if (name.startsWith(Constants.R_REMOTES)) {
            txt = name.substringAfter(Constants.R_REMOTES);
        } else if (name.startsWith(Constants.R_TAGS)) {
            txt = name.substringAfter(Constants.R_TAGS);
        }
        val arcHeight: Double = texth / 4
        val y0: Double = y - texth / 2 + (cell!!.height - texth) / 2
        val text = Text(txt)
        val rect = Rectangle(x.toDouble(), y0, 100.0, texth + 2)
        rect.fill = Color.LIGHTCYAN
        text.x = x + 1.0
        text.y = y + 1.0
        text.fill = Color.BLACK
        text.stroke = Color.BLACK
        cell!!.group!!.children.add(text)
        //cell!!.group!!.children.add(rect)
        cell!!.graphic = cell!!.group
        return rect.width.toInt()
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
        return myLane?.color ?: Color.BLACK
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
    override fun drawLine(color: Color?, x1: Int, y1: Int, x2: Int, y2: Int, width: Int) {
        lateinit var line : Line
        if (y1 == y2) {
            line = Line((x1 - width / 2).toDouble(), y1.toDouble(), (x2 - width / 2).toDouble(), y2.toDouble())
        } else if (x1 == x2) {
            line = Line(x1.toDouble(), (y1 - width / 2).toDouble(), x2.toDouble(), (y2 - width / 2).toDouble())
        } else {
            line = Line(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
        }
        line.stroke = Color.RED;
        cell!!.group!!.children.add(line)
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
        val rect = Rectangle(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
        rect.stroke = Color.RED
        cell!!.group!!.children.add(rect)
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
        val rect = Rectangle(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
        rect.stroke = Color.RED
        cell!!.group!!.children.add(rect)
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
        /*
        val t = Text(x.toDouble(), y.toDouble(), msg)
        t.setFont(Font(20.0))
        t.fill = BLACK
        cell!!.group!!.children.add(t)
        cell!!.graphic = cell!!.group
         */
    }
}

