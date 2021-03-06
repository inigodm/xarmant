package xarmanta.mainwindow.model

data class FileChanges(val oldFilename: String, val filename: String, val changeType: String, val oldCommit: Commit, val newCommit: Commit)
