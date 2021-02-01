package xarmanta.mainwindow.shared

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.DepthWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.RemoteConfig
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.model.Commit
import java.io.File

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
        val walk = RevWalk(git.repository)
        walk.markStart(walk.parseCommit(git.repository.resolve(Constants.HEAD)))
        walk.sort(RevSort.TOPO) // chronological order
        val history = mutableListOf<Commit>()
        walk.forEach {
            val name = git.branchList().setContains(it.name).setListMode(ListBranchCommand.ListMode.ALL).call()
            history.add(Commit(it.fullMessage, it.name, it.authorIdent.name, name[0].name))
            println(getBrancNames(name))
        }
        walk.close()
        return history
    }

    fun getBrancNames(appearances: List<Ref>) : List<String>{
        val branch = appearances[0].name.substringAfterLast(File.separator)
        return appearances.filter { it.name.contains(branch) }.map{ it.name.substringAfter("${File.separator}ref/")}
    }
}
