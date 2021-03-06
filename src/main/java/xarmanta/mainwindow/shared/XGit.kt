package xarmanta.mainwindow.shared

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.RemoteConfig
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.infraestructure.jgit.JavaFxCommitList
import xarmanta.mainwindow.model.FileChanges

import org.eclipse.jgit.revwalk.RevWalk

import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator

import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.EditList
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import java.io.ByteArrayOutputStream
import org.eclipse.jgit.revwalk.FollowFilter

import org.eclipse.jgit.diff.DiffConfig

import org.eclipse.jgit.api.errors.GitAPIException

import java.io.IOException

import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.revplot.PlotCommit

import org.eclipse.jgit.treewalk.AbstractTreeIterator
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

    fun getChangesBetween(oldCommit: Commit, newCommit: Commit): List<FileChanges> {
        val res = changesBetweenCommits(oldCommit.plotCommit!!, newCommit.plotCommit!!)
        return res!!.map { FileChanges(it.newPath, it.changeType.name) }
    }

    fun getChangesInCommit(commit: Commit): List<FileChanges> {
        val parent = commit.plotCommit?.parents?.elementAtOrElse(0) { commit }
        val res = if (parent == commit ){
            changesInASingleCommit(commit)
        } else {
            changesBetweenCommits(commit.plotCommit!!.parents[0], commit.plotCommit)
        }
        return res!!.map { FileChanges(it.newPath, it.changeType.name) }
    }

    fun dunno(selectedItems: List<Commit>?) {
        val reader: ObjectReader = git.repository.newObjectReader()
        val oldTreeIter = FileTreeIterator(git.repository)
        oldTreeIter.reset()
        val newTreeIter = CanonicalTreeParser()
        newTreeIter.reset(reader, selectedItems?.get(0)?.plotCommit?.getTree())
        val out = ByteArrayOutputStream()
        val diffFormatter = DiffFormatter(out)
        diffFormatter.setRepository(git.repository)
        val workTreeIterator = FileTreeIterator(git.repository)
        val entries: List<DiffEntry> = diffFormatter.scan(newTreeIter, workTreeIterator)
        for (entry in entries) {
            println("*******************")
            println("Entry: " + entry + ", from: " + entry.oldId + ", to: " + entry.newId)
            println("oldpath: " + entry.oldPath + ", newpath: " + entry.newPath )
            println("oldId: " + entry.oldId + ", NEWID: " + entry.newId )
            println("+++++++++++++++++++++++")
            diffFormatter.format(entry)
            println("-----------------------")
            runDiff(git.repository, selectedItems?.get(0)?.plotCommit!!.toObjectId().name,
                selectedItems.get(1).plotCommit!!.toObjectId().name, entry.newPath)
        }
        diffFormatter.format( entries );
        diffFormatter.close()
        //res!!.forEach { getChangesInFile(it) }
        println(out)
    }

    fun runDiff(repo: Repository, oldCommit: String, newCommit: String, path: String) {
        // Diff README.md between two commits. The file is named README.md in
        // the new commit (5a10bd6e), but was named "jgit-cookbook README.md" in
        // the old commit (2e1d65e4).
        // Diff README.md between two commits. The file is named README.md in
        // the new commit (5a10bd6e), but was named "jgit-cookbook README.md" in
        // the old commit (2e1d65e4).
        val diff: DiffEntry? = diffFile(
            repo,
            oldCommit,
            newCommit,
            path
        )

        // Display the diff

        // Display the diff
        System.out.println("Showing diff of $path")
        DiffFormatter(System.out).use { formatter ->
            formatter.setRepository(repo)
            formatter.format(diff)
        }
    }

    @Throws(IOException::class, GitAPIException::class)
    private fun diffFile(
        repo: Repository, oldCommit: String,
        newCommit: String, path: String
    ): DiffEntry? {
        val config = Config()
        config.setBoolean("diff", null, "renames", true)
        val diffConfig: DiffConfig = config.get(DiffConfig.KEY)
        Git(repo).use { git ->
            val diffList =
                git.diff().setOldTree(prepareTreeParser(repo, oldCommit)).setNewTree(prepareTreeParser(repo, newCommit))
                    .setPathFilter(FollowFilter.create(path, diffConfig)).call()
            if (diffList.size == 0) return null
            if (diffList.size > 1) throw RuntimeException("invalid diff")
            return diffList[0]
        }
    }

    @Throws(IOException::class)
    private fun prepareTreeParser(repository: Repository, objectId: String): AbstractTreeIterator? {
        // from the commit we can build the tree which allows us to construct the TreeParser
        RevWalk(repository).use { walk ->
            val commit = walk.parseCommit(repository.resolve(objectId))
            val tree = walk.parseTree(commit.tree.id)
            val treeParser = CanonicalTreeParser()
            repository.newObjectReader().use { reader -> treeParser.reset(reader, tree.id) }
            walk.dispose()
            return treeParser
        }
    }

    fun getChangesInFile(entry: DiffEntry): EditList? {
        var editList: EditList? = null
        var s = ""
        println("${entry.newPath}")
        try {
            DiffFormatter(System.out).use { diffFormatter ->
                diffFormatter.setRepository(git.repository)
                val fileHeader = diffFormatter.toFileHeader(entry)
                println("${fileHeader.scriptText}")
                val editList =  fileHeader.toEditList()
                fileHeader.hunks.forEach { println(" ${it}") }
                editList.forEach { println(" ${it}") }
            }
        } catch (e: Exception) {
            println(e.message)
            return null
        }

        return editList
    }

    fun changesInASingleCommit(commit: Commit): List<DiffEntry>? {
        return git.diff()
            .setNewTree(FileTreeIterator(git.repository))
            .setOldTree(getCanonicalTree(commit.plotCommit!!))
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
}
