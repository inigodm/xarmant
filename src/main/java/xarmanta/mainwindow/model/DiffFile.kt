package xarmanta.mainwindow.model

import org.eclipse.jgit.diff.EditList

data class DiffFile(val oldFile: List<String>, val newFile: List<String>, val editList: EditList)
