package xarmanta.mainwindow.application.config

import xarmanta.mainwindow.infraestructure.git.GitContext
import java.io.Serializable

data class ConfigFile(val repos: MutableList<GitContext>,
                      var lastOpened: GitContext? = null): Serializable
