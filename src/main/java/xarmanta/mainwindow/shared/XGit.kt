package xarmanta.mainwindow.shared

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.RemoteConfig
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.model.Commit


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
        val initialCommit = git.repository.resolve(Constants.HEAD)
        val history = mutableListOf<Commit>()
        val commitCache = mutableListOf(initialCommit)
        val mapCommits = mutableMapOf<String, Commit>()
        while(commitCache.isNotEmpty()) {
            val actual = commitCache.removeAt(0)
            val name = git.branchList().setContains(actual.name).setListMode(ListBranchCommand.ListMode.ALL).call()
            name.forEach { branchName ->
                run {
                    val commits = git.log().add(branchName.objectId).call()
                    commits.forEach { commit ->
                        run {
                            if (!mapCommits.contains(commit.toObjectId().name)) {
                                val newCommit = Commit(commit.fullMessage, commit.name, commit.authorIdent.name,
                                    mutableSetOf(branchName.name), commit.commitTime)
                                mapCommits.put(commit.toObjectId().name, newCommit)
                                history.add(newCommit)
                                commitCache.add(commit)
                            } else {
                                mapCommits.get(commit.toObjectId().name)!!.branches.add(branchName.name)
                            }
                        }
                    }
                }
            }
        }
        history.sortByDescending { it.commitTime }
        //HeapDumper.dumpHeap("headdump.hprof", true)
        return history
    }
}
