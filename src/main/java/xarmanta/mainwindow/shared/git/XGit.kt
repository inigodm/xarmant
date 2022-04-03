package xarmanta.mainwindow.shared.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.RemoteConfig
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.infraestructure.jgit.JavaFxCommitList
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.lib.ObjectLoader

import org.eclipse.jgit.treewalk.TreeWalk

import java.io.IOException
import java.nio.charset.StandardCharsets

import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.EditList
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.treewalk.FileTreeIterator
import xarmanta.mainwindow.model.*
import xarmanta.mainwindow.model.GitContext


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

    fun status(): Status? {
        return git.status().call()
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

    fun open() : XGit {
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

    fun buildListOfCommits(): MutableList<Commit> {
        val walk = PlotWalk(git.repository)
        git.repository.refDatabase.refs.forEach {
            println(it.name)
            walk.markStart(walk.parseCommit(it.objectId))
        }
        println("*******************************************************************")
        git.branchList().call().forEach{
            if (it.name.contains("17908")) {
                print("----->>>>")
            }
            if (it.name.contains("17972")) {
                print("----->>>>")
            }
            println(it.name)
        }
        val list = JavaFxCommitList()
        list.source(walk)
        list.fillTo(Int.MAX_VALUE)
        val history = mutableListOf<Commit>()
        list.forEach { history.add(Commit(it.fullMessage, it.name, it.authorIdent.name,"Not Supported", it.commitTime, mutableSetOf(), it))}
        val status = status()
        status?.changed
        val head = git.repository.resolve(Constants.HEAD)
        val headCommit  = list.filter { it.name.equals(head.name) }.firstOrNull()
        //TODO Add working which father is HEAD:
        // is as it but withouth mergin / forking lanes
        // Add, too, the new commit as passing lane to every commits newer than HEAD
        println(headCommit?.fullMessage)
        history.add(0, Commit("Uncommited",
                "",
                "",
                "",
                0,
                status!!.changed,
                headCommit,
                CommitType.UNCOMMITED))
        return history
    }

    fun getChangesBetween(oldCommit: Commit, newCommit: Commit): List<FileChanges> {
        val res = changesBetweenCommits(oldCommit.commit!!, newCommit.commit!!)
        return res!!.map { FileChanges(it.oldPath, it.newPath, it.changeType.name, oldCommit, newCommit, Entry(it)) }
    }

    fun getChangesInCommit(commit: Commit): List<FileChanges> {
        val parent: RevCommit? = commit.commit?.parents?.getOrElse(0) { commit.commit!! }
        val res = if (parent == commit.commit ){
            git.diff().call()
        } else {
            changesBetweenCommits(commit.commit!!.parents[0], commit.commit)
        }
        return res!!.map { FileChanges(it.oldPath, it.newPath, it.changeType.name,
                Commit(parent!!.fullMessage, parent.name, parent.authorIdent.name,
                        "Not Cupported", parent.commitTime, mutableSetOf(), parent),
                commit, Entry(it)) }
    }

    private fun changesInFirstCommit(commit: Commit): List<DiffEntry>? {
        return git.diff()
            .setOldTree(EmptyTreeIterator())
            .setNewTree(getCanonicalTree(commit.commit!!))
            .call()
    }

    private fun changesInWorkingCopy(oldCommit: RevCommit) : List<DiffEntry>? {
        return git.diff()
                .setNewTree(FileTreeIterator(git.repository))
                .setOldTree(getCanonicalTree(oldCommit))
                .call()
    }

    private fun changesInIndex(oldCommit: RevCommit) : List<DiffEntry>? {
        return git.diff()
                .setNewTree(DirCacheIterator(git.repository.readDirCache()))
                .setOldTree(getCanonicalTree(oldCommit))
                .call()
    }

    private fun changesBetweenCommits(oldCommit: RevCommit, newCommit: RevCommit): List<DiffEntry>? {
        return git.diff()
            .setNewTree(getCanonicalTree(newCommit))
            .setOldTree(getCanonicalTree(oldCommit))
            .call()
    }

    private fun getCanonicalTree(fromCommit: RevCommit): CanonicalTreeParser {
        RevWalk(git.repository).use { walk ->
            val commit = walk.parseCommit(fromCommit)
            val tree = commit.tree.id
            git.repository.newObjectReader()
                .use { reader -> return CanonicalTreeParser(null, reader, tree) }
        }
    }

    fun buildDiff(selectedItem: FileChanges): ChangedFile {
        val old = getFileContentAtCommit(selectedItem.oldCommit.commit!!, selectedItem.oldFilename)
        val new = getFileContentAtCommit(selectedItem.newCommit.commit!!, selectedItem.filename)
        val editList = obtainEditList(selectedItem)
        return ChangedFile(old, new, editList)
    }

    private fun obtainEditList(selectedItem: FileChanges): EditList {
        DiffFormatter(null).use { formatter ->
            formatter.setRepository(git.repository)
            val fileHeader = formatter.toFileHeader(selectedItem.entry.entry)
            return fileHeader.toEditList()
        }
    }

    @Throws(IOException::class)
    private fun getFileContentAtCommit(commit: RevCommit, path: String): List<String> {
        TreeWalk.forPath(git.repository, path, commit.tree).use { treeWalk ->
            if (treeWalk == null) {
                return emptyList()
            }
            val blobId = treeWalk.getObjectId(0)
            git.repository.newObjectReader().use { objectReader ->
                val objectLoader: ObjectLoader = objectReader.open(blobId)
                val bytes = objectLoader.bytes
                return String(bytes, StandardCharsets.UTF_8).lines()
            }
        }
    }
}
