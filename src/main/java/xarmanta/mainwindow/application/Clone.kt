package xarmanta.mainwindow.application

import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit

class Clone {
    fun execute(context: GitContext, monitor: XarmantProgressMonitor): XGit {
        return XGit(context, monitor).clone()
    }
}
