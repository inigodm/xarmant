package xarmanta.mainwindow.shared.exceptions

class InvalidUrl: Exception {
    constructor(url: String?) : super("$url is not a valid URL")
}
