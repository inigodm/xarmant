package xarmanta.mainwindow.shared

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.DepthWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.model.Commit


class XGit(val config: GitContext, val monitor: XarmantProgressMonitor) {
    lateinit var git: Git

    fun clone(): XGit {
        git = Git.cloneRepository()
            .setURI(config.url)
            .setDirectory(config.directory)
            .setProgressMonitor(this.monitor)
            .call()
        return this
    }

    fun open() : XGit{
        git = Git.open(config.directory)
        return this
    }

    fun push() {
        git.push()
            .setProgressMonitor(monitor)
            .call()
    }

    fun pull() {
        git.pull()
            .setProgressMonitor(monitor)
            .call()
    }

    fun reverseWalk(): MutableList<Commit> {
        val walk = RevWalk(git.repository)
        walk.markStart(walk.parseCommit(git.repository.resolve(Constants.HEAD)))
        walk.sort(RevSort.TOPO) // chronological order
        val history = mutableListOf<Commit>()
        walk.forEach {
            val name = git.nameRev()
            .addPrefix("refs/heads")
            .add(it)
            .call();
            history.add(Commit(it.fullMessage, it.name, it.authorIdent.name, name.values.firstOrNull()?.toString()?: ""))
        }
        walk.close()
        return history
    }
}
