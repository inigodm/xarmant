package xarmanta.mainwindow.shared

import org.eclipse.jgit.api.Git
import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor


class XGit(val config: GitContext, val monitor: XarmantProgressMonitor) {
    lateinit var git: Git

    fun clone(): XGit {
        git = Git.cloneRepository()
            .setURI(config.url)
            .setDirectory(config.directory)
            .setProgressMonitor(monitor)
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


}
