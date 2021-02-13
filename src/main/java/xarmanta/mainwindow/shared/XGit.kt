package xarmanta.mainwindow.shared

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.RemoteConfig
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.model.Commit
import org.eclipse.jgit.revplot.PlotCommitList

import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.lib.ObjectLoader

import org.eclipse.jgit.treewalk.TreeWalk

import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevWalk
import xarmanta.mainwindow.infraestructure.jgit.JavaFxCommitList
import xarmanta.mainwindow.infraestructure.jgit.JavaFxLane


// Clase para wrapear JGit
class XGit(val config: GitContext, val monitor: XarmantProgressMonitor) {
    lateinit var git: Git
    lateinit var branches: List<Ref>
    lateinit var tags: List<Ref>
    lateinit var origins: List<RemoteConfig>
    lateinit var stashes: MutableCollection<RevCommit>

    fun updateData() {
        branches = git.branchList().call()
        tags = git.tagList().call()
        origins = git.remoteList().call()
        stashes = git.stashList().call()
    }

    fun clone(): XGit {
        git = Git.cloneRepository()
            .setURI(config.url)
            .setDirectory(config.directory)
            .setProgressMonitor(this.monitor)
            .call()
        updateData()
        return this
    }

    fun open() : XGit{
        git = Git.open(config.directory)
        updateData()
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
        val walk = PlotWalk(git.repository)
        val allRefs: Collection<Ref> = git.repository.refDatabase.getRefs()
        for (ref in allRefs) {
            walk.markStart(walk.parseCommit(ref.objectId))
        }
        val list = JavaFxCommitList()
        list.source(walk)
        list.fillTo(Int.MAX_VALUE)
        val history = mutableListOf<Commit>()
        list.forEach { history.add(Commit(it.fullMessage, it.name, it.authorIdent.name,
            "Not Cupported", it.commitTime, mutableSetOf(), it))}
        return history
    }

}
