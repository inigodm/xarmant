package xarmanta.mainwindow.model.modifiedFiles

import xarmanta.mainwindow.model.commit.Commit

data class FileModified(val oldFilename: String, val filename: String, val changeType: String, val oldCommit: Commit, val newCommit: Commit, val entry: Entry)
