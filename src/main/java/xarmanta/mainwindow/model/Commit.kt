package xarmanta.mainwindow.model

data class Commit(val description: String, val username: String, val sha: String, val branches: MutableSet<String>, val commitTime: Int)
