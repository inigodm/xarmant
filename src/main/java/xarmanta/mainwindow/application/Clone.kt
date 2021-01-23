package xarmanta.mainwindow.application

import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit

class Clone {
    fun execute(config: GitContext, monitor: XarmantProgressMonitor): XGit {
        return XGit(config).clone(monitor)
    }
}
