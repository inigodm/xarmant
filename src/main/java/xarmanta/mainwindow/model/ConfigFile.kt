package xarmanta.mainwindow.model

import java.io.Serializable

data class ConfigFile(val repos: MutableList<GitContext>,
                      var lastOpened: GitContext? = null): Serializable
