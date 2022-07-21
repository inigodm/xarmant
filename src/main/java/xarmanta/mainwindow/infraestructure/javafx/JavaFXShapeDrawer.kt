package xarmanta.mainwindow.infraestructure.javafx

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import xarmanta.mainwindow.model.commit.Commit
import xarmanta.mainwindow.model.commit.Type
import xarmanta.mainwindow.application.graph.ShapeDrawer

class JavaFXShapeDrawer(var gc : GraphicsContext) : ShapeDrawer {
    val LINE_GAP = 10.0
    val H_COMMIT = 5.0
    val W_COMMIT = 5.0
    val H_MAX = 20.0
    override fun drawCell(commit: Commit) {
        commit.graphic.forEach {
            when(it.type) {
                Type.LINE -> {
                    drawForkingLine(commit, it.fromX, it.toX, it.fromY, it.toY, it.color)
                }
                Type.COMMIT -> {
                    drawCommit(commit, it.fromX, it.fromY, it.color)
                }
                Type.STASH -> {
                    drawStash(commit, it.fromX, it.fromY, it.color)
                }
                Type.WIP -> {
                    drawCommit(commit, it.fromX, it.fromY, it.color)
                }
            }
        }
    }

    override fun drawStraightLine(commit: Commit, x: Double, yo: Double, yf: Double, color: Color) {
        gc.stroke = color
        gc.strokeLine(x * LINE_GAP, yo * H_MAX, x * LINE_GAP, yf * H_MAX)
    }

    override fun drawForkingLine(commit: Commit, fromX: Double, toX: Double, yo: Double, yf: Double, color: Color) {
        gc.stroke = color
        gc.strokeLine(fromX * LINE_GAP, yo * H_MAX, toX * LINE_GAP, yf * H_MAX)
    }

    override fun drawMergingLine(commit: Commit, fromX: Double, toX: Double, yo: Double, yf: Double, color: Color) {
        gc.stroke = color
        gc.strokeLine(fromX * LINE_GAP, yo * H_MAX, toX * LINE_GAP, yf * H_MAX)
    }

    override fun drawCommit(commit: Commit, x: Double, y: Double, color: Color) {
        gc.stroke = color
        gc.fillOval(x * LINE_GAP - W_COMMIT / 2, H_MAX / 2 - H_COMMIT / 2, W_COMMIT, H_COMMIT)
    }

    override fun drawStash(commit: Commit, x: Double, y: Double, color: Color) {
        gc.stroke = color
        gc.fillRect(x * LINE_GAP - W_COMMIT / 2, H_MAX / 2 - H_COMMIT / 2, W_COMMIT, H_COMMIT)
    }

    override fun next() {
        //Nothing to do here
    }
}
