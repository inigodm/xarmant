package xarmanta.mainwindow.model

import org.eclipse.jgit.diff.DiffEntry

data class FileChanges(val oldFilename: String, val filename: String, val changeType: String, val oldCommit: Commit, val newCommit: Commit, val entry: Entry)
data class Entry(val entry: DiffEntry)
