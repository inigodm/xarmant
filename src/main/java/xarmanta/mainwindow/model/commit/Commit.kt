package xarmanta.mainwindow.model.commit

import javafx.scene.paint.Color
import org.eclipse.jgit.revwalk.RevCommit
import xarmanta.mainwindow.model.DrawableItem

class Commit {
    val commit: RevCommit?
    val description: String
    val username: String
    val sha: String
    var branches: MutableList<String> = mutableListOf()
    val commitTime: Int
    val type : Type
    val lines: MutableList<GitLine> = mutableListOf()
    var parents: MutableList<Commit>
    var sons: MutableSet<Commit> = mutableSetOf()
    var graphic : MutableList<DrawableItem> = mutableListOf()
    constructor(revCommit: RevCommit,
                typeOfCommit: Type,
                parent: List<Commit> = listOf()
    ) {
        commit = revCommit
        description = commit.fullMessage
        username = commit.authorIdent.name
        sha = commit.name
        commitTime = commit.commitTime
        type = typeOfCommit
        parents = parent.toMutableList()
    }

    constructor(name: String, parent: Commit?) {
        commit = null
        description = "WIP"
        username = ""
        sha = name
        commitTime = Int.MAX_VALUE
        type = Type.WIP
        parents = listOf(parent).mapNotNull { it }.toMutableList()
        parents.reverse()
    }

    fun addLine(fromX: Double, toX: Double, fromY: Double, toY: Double, color: Color = Color.RED, size : Double = 1.0) {
        graphic.add(DrawableItem(Type.LINE, fromX, toX, fromY, toY, color, size))
    }

    fun addCommit(x: Double, size: Double, color: Color = Color.RED) {
        graphic.add(DrawableItem(Type.COMMIT, x, x, 0.0, 0.0, color, size))
    }

    fun addWIP(x: Double, size: Double, color: Color = Color.RED) {
        graphic.add(DrawableItem(Type.WIP, x, x, 0.0, 0.0, color, size))
    }

    fun addStash(x: Double, size: Double, color: Color) {
        graphic.add(DrawableItem(Type.STASH, x, x, 0.0, 0.0, color, size))
    }
}
data class GitLine(val color: Color,
                   val from: Commit,
                   val to: Commit,
                   val type: Type = Type.COMMIT
)

enum class Type {
    COMMIT, STASH, WIP, LINE
}
