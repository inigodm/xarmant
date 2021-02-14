package xarmanta.mainwindow.shared

import java.io.Serializable

data class ConfigFile(val repos: MutableList<GitContext>): Serializable
