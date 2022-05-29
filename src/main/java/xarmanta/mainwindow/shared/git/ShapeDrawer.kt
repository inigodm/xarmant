package xarmanta.mainwindow.shared.git

import javafx.scene.paint.Color
import xarmanta.mainwindow.model.Commit

interface ShapeDrawer {
    fun drawCell(commit: Commit)
    fun drawStraightLine(commit: Commit, x: Double = 0.0, yo: Double = 25.0, yf: Double = 0.0, color: Color = Color.BLACK)
    fun drawForkingLine(commit: Commit, fromX: Double = 0.0, toX: Double = 0.0, yo: Double = 25.0, yf: Double = 0.0, color: Color = Color.BLACK)
    fun drawMergingLine(commit: Commit, fromX: Double = 0.0, toX: Double = 0.0, yo: Double = 25.0, yf: Double = 0.0, color: Color = Color.BLACK)
    fun drawCommit(commit: Commit, x: Double = 0.0, y: Double = 0.0, color: Color = Color.BLACK)
    fun drawStash(commit: Commit, x: Double = 0.0, y: Double = 0.0, color: Color = Color.BLACK)
    fun next()
}
