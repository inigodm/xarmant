package xarmanta.mainwindow.model

import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revwalk.RevCommit
import xarmanta.mainwindow.infraestructure.jgit.JavaFxLane

enum class CommitType {
    COMMIT,
    UNCOMMITED,
    STASH
}
data class Commit(val description: String,
                  val username: String,
                  val sha: String,
                  var branch: String,
                  val commitTime: Int,
                  val branches: MutableSet<String>,
                  val commit: RevCommit?=null,
                  val plotCommit: PlotCommit<JavaFxLane>? = commit as PlotCommit<JavaFxLane>,
                  val type : CommitType = CommitType.COMMIT
)
