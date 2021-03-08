package xarmanta.mainwindow.model

import org.eclipse.jgit.diff.EditList

data class ChangedFile(val oldFile: List<String>, val newFile: List<String>, val editList: EditList)
