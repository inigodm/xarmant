package xarmanta.mainwindow.shared.git

import xarmanta.mainwindow.model.Commit

class XGitDrawer {
    // commits que han de ser dibujados y que no sabemos cuandolo seran (parents de dibujados)
    val commitsMap: MutableMap<String, Commit> = mutableMapOf()
    val activeCommits: MutableList<Commit> = mutableListOf()

    fun drawGraph(commits: List<Commit>) {
        initCommitsMap(commits)
        commits.forEach {
            drawCommit(it)
        }
    }

    fun drawCommit(commit: Commit) {
        val commitX = drawTopLines(commit)
        drawCommitInGraph(commit, commitX)
        setCommitAsTreated(commit)
        drawBottomLines(commit)
    }

    private fun drawTopLines(commit: Commit): Double {
        var x = 1.0
        var commitX = 0.0
        var finalX = 0.0
        activeCommits.forEach {
            finalX = x
            // si es el comit que estamos dibujando
            if (it == commit) {
                // el primer commit tiene una linea recta al commit desde arriba
                if (commitX == 0.0) {
                    commitX = x
                } else {
                    // el resto de lineas que vayan al commit tienen que juntarse en el commit
                    finalX = commitX
                }
            }
            //sino es el commit que estamos dibujando solo se pinta la linea que segfuira para abajo
            commit.addLine(x, finalX, 0.0, 0.5) //it
            //movemos a la derecha
            x += 1.0
        }
        return if (commitX == 0.0) x else commitX
    }

    private fun drawBottomLines(commit: Commit) {
        var x = 1.0
        var commitX = 0.0
        var sonCommit : Commit? = null
        activeCommits.forEach {
            var mergingX = x
            // si es el comit que estamos dibujando
            if (it.sons.contains(commit)) {
                if (sonCommit == null) {
                    commitX = x
                    sonCommit = it //it
                } else //it
                {
                    if (sonCommit != it){
                        mergingX = commitX
                    }
                }
            }
            commit.addLine(mergingX, x, 0.5, 1.0) //it
            x += 1.0
        }
    }

    private fun setCommitAsTreated(commit: Commit) {
        val indexOfCommit = activeCommits.indexOf(commit)
        commit.parents.forEach {
            if (indexOfCommit == -1) {
                activeCommits.add(it)
            } else {
                activeCommits.add(indexOfCommit, it)
            }
        }
        // remove current commit from actives because we will not use it anymore
        //it could be more than once
        while (activeCommits.contains(commit)) {
            activeCommits.remove(commit)
        }
    }

    fun initCommitsMap(commits: List<Commit>) {
        commitsMap.clear()
        commits.forEach {
            commitsMap.put(it.sha, it)
        }
    }

    private fun drawCommitInGraph(commit: Commit, commitX: Double) {
        commit.addCommit(commitX, 1.0)
    }
}
