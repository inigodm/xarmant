package xarmanta.mainwindow.application

import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit
// Esto va a desaoarecer:
/**
 * Era un intento de arquitectura hexagonal, per, sabes que?
 *
 * No voy a cambiar de JavaFX, lo unico que podria cambiar el JGit poor correr comandos y parsear respuestas y, para esp, esta lo de XGit
 */
class Clone {
    fun execute(context: GitContext, monitor: XarmantProgressMonitor): XGit {
        return XGit(context, monitor).clone()
    }
}
