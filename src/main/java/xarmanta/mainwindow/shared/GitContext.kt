package xarmanta.mainwindow.shared

import xarmanta.mainwindow.shared.exceptions.InvalidUrl
import java.io.File
import java.net.URL

// Basicamente sera un objeto que podre serializar a archivo de configuracion
class GitContext {
    var url: String? = null
    var directory: File? = null
    var user: String? = null
    var pass: String? = null

    constructor(url: String?, directory: File?, user: String? = null, pass: String? = null) {
        if (!isValidUrl(url)) {
            throw InvalidUrl(url)
        }
        this.url = url
        this.directory = directory
        this.user = user
        this.pass = pass
    }

    private fun isValidUrl(url: String?): Boolean {
        if (url == null) {
            return true
        }
        try{
            URL(url)
            return true
        } catch (t: Throwable) {
            return false
        }
    }

}
