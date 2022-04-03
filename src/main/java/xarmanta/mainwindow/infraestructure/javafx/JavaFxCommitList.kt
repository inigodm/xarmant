package xarmanta.mainwindow.infraestructure.jgit

import javafx.scene.paint.Color
import javafx.scene.paint.Color.*
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane



class JavaFxCommitList : PlotCommitList<JavaFxLane>() {
    val colors: List<Color> = mutableListOf(GREENYELLOW,
        GREEN,
        DARKGREEN,
        ALICEBLUE,
        BLUE,
        BLUEVIOLET,
        CADETBLUE,
        DARKBLUE,
        RED,
        MAGENTA,
        DARKGRAY,
        YELLOW,
        ORANGE)
    var i = 0

    override fun createLane(): JavaFxLane {
        val lane = JavaFxLane()
        lane.color = colors[i]
        i++
        if (i > colors.size - 1) {
            i = 0
        }
        return lane
    }

    override fun recycleLane(lane: JavaFxLane) {
        //Nothing to do here
    }
}

class JavaFxLane : PlotLane() {
    var color: Color? = null

    companion object {
        private const val serialVersionUID = 1L
    }
}
