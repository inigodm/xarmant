package xarmanta.mainwindow.model.commit

import javafx.scene.paint.Color
import javafx.scene.paint.Color.*
import org.eclipse.jgit.revwalk.RevCommit
import xarmanta.mainwindow.model.DrawableItem

val COLORS = listOf<Color>(BEIGE, BLACK, BLUE, CYAN,
    DARKBLUE, DARKCYAN, DARKGRAY, DARKGOLDENROD, DARKGREEN, DARKMAGENTA, DARKSALMON, DARKRED, FIREBRICK,
    FUCHSIA, GOLD, HOTPINK, INDIGO, INDIANRED, KHAKI, LIGHTBLUE, MAGENTA, MEDIUMBLUE,
    OLIVE, ORANGE, PALEGREEN, PERU, RED, ROYALBLUE, SALMON, SILVER, SIENNA, TOMATO, VIOLET, YELLOW)

val COLORSMAP = mutableMapOf<String, Color>()
var index = 0
val localPrefix = "refs/heads/"
val remoteLength = "refs/remotes/".length
val length = localPrefix.length


fun getColor(commit: Commit?): Color {
    if (commit == null) {
        return BLACK
    }
    if (commit.localBranches.isNotEmpty()) {
        return COLORSMAP[commit.localBranches.last()]!!
    }
    if (commit.remoteBranches.isNotEmpty()) {
        return COLORSMAP[commit.remoteBranches.last()]!!
    }
    return BLACK
}

class Commit {
    val commit: RevCommit?
    val description: String
    val username: String
    val sha: String
    var localBranches: MutableList<String> = mutableListOf()
    var remoteBranches: MutableList<String> = mutableListOf()
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

    fun addLine(fromX: Double, toX: Double, fromY: Double, toY: Double, size : Double = 1.0, color : Color = getColor()) {
        graphic.add(DrawableItem(Type.LINE, fromX, toX, fromY, toY, color, size))
    }

    fun addCommit(x: Double, size: Double, color : Color = getColor()) {
        graphic.add(DrawableItem(Type.COMMIT, x, x, 0.0, 0.0, color, size))
    }

    fun addWIP(x: Double, size: Double, color : Color = getColor()) {
        graphic.add(DrawableItem(Type.WIP, x, x, 0.0, 0.0, color, size))
    }

    fun addStash(x: Double, size: Double, color : Color = getColor()) {
        graphic.add(DrawableItem(Type.STASH, x, x, 0.0, 0.0, color, size))
    }

    fun getColor(): Color {
        if (localBranches.isNotEmpty()) {
            return COLORSMAP[localBranches.last()]!!
        }
        if (remoteBranches.isNotEmpty()) {
            return COLORSMAP[remoteBranches.last()]!!
        }
        return BLACK
    }

    fun addBranches(localAndRemoteBraches: List<String>) {
        println(description)
        localAndRemoteBraches.forEach {
            if (it.startsWith(localPrefix)) {
                val branch = it.substring(length)
                if (!COLORSMAP.containsKey(branch)) {
                    COLORSMAP[branch] = COLORS[index++]
                }
                localBranches.add(branch)
            } else {
                val branch = it.substring(remoteLength)
                if (!COLORSMAP.containsKey(branch)) {
                    COLORSMAP[branch] = COLORS[index++]
                }
                remoteBranches.add(branch)
            }
        }
        println("local: $localBranches")
        println("remote: $remoteBranches")
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
