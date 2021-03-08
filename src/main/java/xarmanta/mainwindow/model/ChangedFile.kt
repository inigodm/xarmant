package xarmanta.mainwindow.model

import org.eclipse.jgit.diff.EditList

data class ChangedFile(val oldFile: Array<String>, val newFile: Array<String>, val editList: EditList)
