package xarmanta.mainwindow.model

import org.eclipse.jgit.revplot.PlotCommit
import xarmanta.mainwindow.infraestructure.jgit.JavaFxLane

data class Commit(val description: String, val username: String, val sha: String, var branch: String, val commitTime: Int, val branches: MutableSet<String>,
val plotCommit: PlotCommit<JavaFxLane>?=null
)
