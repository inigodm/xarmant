package xarmanta.mainwindow.model

import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane
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
                  val type : CommitType = CommitType.COMMIT,
                  val lines : PlotLane = (commit as PlotCommit<JavaFxLane>).lane
)
