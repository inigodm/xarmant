package xarmanta.mainwindow.application.config

import xarmanta.mainwindow.infraestructure.git.GitContext
import xarmanta.mainwindow.shared.KotlinAsyncRunner
import java.io.*
import java.nio.file.Files
import java.nio.file.Path

class ConfigManager {

    fun saveContext(contxt: GitContext) {
        KotlinAsyncRunner().runAsyncIO {
            val config = openConfigFile()
            updateRepository(config, contxt)
            saveConfigFile(config)
        }
    }

    private fun updateRepository(config: ConfigFile, contxt: GitContext) {
        val repo = config.repos.filter { it.directory!!.path == contxt.directory!!.path }.firstOrNull()
        if (repo != null) {
            config.repos.remove(repo)
        }
        config.repos.add(contxt)
        config.lastOpened = contxt
    }

    private fun saveConfigFile(config: ConfigFile) {
        ObjectOutputStream(FileOutputStream(File("config.xar"))).use {
            it.writeObject(config)
        }
    }

    fun openConfigFile(): ConfigFile {
        var configFile : ConfigFile
        if (!Files.exists(Path.of("./config.xar"))) {
            Files.createFile(Path.of("./config.xar"))
            saveConfigFile(ConfigFile(mutableListOf()))
            return ConfigFile(mutableListOf())
        }
        ObjectInputStream(FileInputStream(File("./config.xar"))).use {
            configFile = it.readObject() as ConfigFile
        }
        return configFile
    }


}
