package xarmanta.mainwindow.infraestructure.javafx

import javafx.application.Platform
import javafx.scene.control.Label
import org.eclipse.jgit.lib.BatchingProgressMonitor

/**
 * Label actualizable que muestra el progreso de las tareas largas
 */
class XarmantProgressMonitor(val label: Label) : BatchingProgressMonitor() {

    override fun onUpdate(taskName: String, workCurr: Int) {
        val s = StringBuilder()
        format(s, taskName, workCurr)
        send(s)
    }

    override fun onEndTask(taskName: String, workCurr: Int) {
        val s = StringBuilder()
        format(s, taskName, workCurr)
        s.append("\n")
        send(s)
    }

    private fun format(s: StringBuilder, taskName: String, workCurr: Int) {
        s.append("\r$taskName: ")
        while (s.length < 25) s.append(' ')
        s.append(workCurr)
    }

    override fun onUpdate(taskName: String, cmp: Int, totalWork: Int, pcnt: Int) {
        val s = StringBuilder()
        format(s, taskName, cmp, totalWork, pcnt)
        send(s)
    }

    /** {@inheritDoc}  */
    override fun onEndTask(taskName: String, cmp: Int, totalWork: Int, pcnt: Int) {
        val s = StringBuilder()
        format(s, taskName, cmp, totalWork, pcnt)
        s.append("\n")
        send(s)
    }

    private fun format(s: StringBuilder, taskName: String, cmp: Int, totalWork: Int, pcnt: Int) {
        s.append("\r$taskName: ")
        while (s.length < 25) s.append(' ')
        val endStr = totalWork.toString()
        var curStr = cmp.toString()
        while (curStr.length < endStr.length) curStr = " $curStr" //$NON-NLS-1$
        if (pcnt < 100) s.append(' ')
        if (pcnt < 10) s.append(' ')
        s.append("$pcnt% ($curStr/$endStr)")
    }

    private fun send(s: StringBuilder) {
        Platform.runLater{
            label.text = s.toString()
        }
    }
}
