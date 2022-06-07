package xarmanta.mainwindow.application.graph

import javafx.scene.paint.Color
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.model.GitLine
import xarmanta.mainwindow.model.Type
import java.util.*

class XGitGraphCalculator {
    private var aux = 0
    private lateinit var stashes : MutableList<RevCommit>
    private var newestStash : RevCommit? = null
    private val commits = mutableMapOf<String, Commit>()

    fun buildCommits(git: Git, depth: Int = 100, fromDepth: Int = 0) : List<Commit> {
        initStashList(git)
        var response = mutableListOf<Commit>()
        git.log().all().call().take(depth).forEach { buildCommitAndStashes(it, response) }
        val xGitRenderer = XGitRenderer()
        xGitRenderer.initCommitsMap(response)
        response.forEach { commit ->
            commit.lines.addAll(createLinesForCommit(commit))
            commit.parents.addAll(findCommitsForAllParents(commit.commit!!, response))
            commit.parents.forEach { it.sons.add(commit) }
            xGitRenderer.renderCommit(commit)
        }
        response = response.sortedByDescending { it.commitTime }.toMutableList()
        addWIP(git, response)
        return response
    }

    private fun addWIP(git: Git, response: MutableList<Commit>) {
        if (!git.status().call().isClean) {
            val head = getHead(git)
            val commit = Commit(Date().time.toString(), findCommitFor(head, response)!!)
            commit.lines.add(GitLine(Color.RED, commit, commits[head.name] ?: commit))
            response.add(0, commit)
        }
    }

    private fun getHead(git: Git): RevCommit {
        return RevWalk(git.repository).use { revWalk -> revWalk.parseCommit(git.repository.resolve(Constants.HEAD)) }
    }

    private fun findCommitFor(revCommit: RevCommit, response: MutableList<Commit>) : Commit? {
        return response.find {
            it.sha == revCommit.name
        }
    }

    private fun buildCommitAndStashes(it: RevCommit,  response: MutableList<Commit>) {
        //There comes 2 commits for each stash because JGit sucks
        if (newestStash != null) {
            if (it.commitTime == newestStash!!.commitTime) {
                if (aux == 0) {
                    response.add(createCommit(newestStash!!, commitType = Type.STASH))
                } else {
                    newestStash = stashes.removeFirstOrNull()
                }
                aux++
                return
            }
        }
        aux = 0
        response.add(createCommit(it))
    }

    private fun initStashList(git: Git) {
        stashes = git.stashList().call().toMutableList()
        newestStash = stashes.removeFirstOrNull()
    }

    private fun createCommit(revCommit: RevCommit, commitType: Type = Type.COMMIT): Commit {
        val commit = Commit(revCommit, commitType)
        commits[commit.sha] = commit
        return commit
    }

    private fun findCommitsForAllParents(revCommit: RevCommit, response: MutableList<Commit> ) : List<Commit>{
        return revCommit.parents.mapNotNull {
            findCommitFor(it, response)
        }.reversed()
    }

    private fun createLinesForCommit(commit: Commit) : List<GitLine>{
        if (commit.commit!!.parents.size == 0) return listOf(GitLine(Color.RED, from = commit, to = commit))
        val response = mutableListOf<GitLine>()
        commit.commit.parents.forEach {
            //There is an extra commit that jgit creates for each stash... crazy
            if (commits.containsKey(it.name)) {
                response.add(GitLine(Color.RED, from = commit, to = commits[it.name]!!) )
            }
        }
        return response
    }
}
