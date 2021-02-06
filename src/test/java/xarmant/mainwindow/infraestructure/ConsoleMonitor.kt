package xarmant.mainwindow.infraestructure

import xarmanta.mainwindow.infraestructure.XarmantProgressMonitor

class ConsoleMonitor : XarmantProgressMonitor() {
    override fun onUpdate(taskName: String?, workCurr: Int) {
        print("$workCurr")
    }

    override fun onUpdate(taskName: String?, workCurr: Int, workTotal: Int, percentDone: Int) {
        print("$workCurr/$workTotal $percentDone%")
    }

    /**
     * Finish the progress monitor when the total wasn't known in advance.
     *
     * @param taskName
     * name of the task.
     * @param workCurr
     * total number of units processed.
     */
    override fun onEndTask(taskName: String?, workCurr: Int) {
        println("$workCurr")
    }

    /**
     * Finish the progress monitor when the total is known in advance.
     *
     * @param taskName
     * name of the task.
     * @param workCurr
     * total number of units processed.
     * @param workTotal
     * estimated number of units to process.
     * @param percentDone
     * `workCurr * 100 / workTotal`.
     */
    override fun onEndTask(taskName: String?, workCurr: Int, workTotal: Int, percentDone: Int) {
        println("$workCurr/$workTotal $percentDone%")
    }
}
