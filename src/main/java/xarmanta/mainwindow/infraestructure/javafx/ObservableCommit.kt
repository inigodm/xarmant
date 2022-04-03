package xarmanta.mainwindow.infraestructure.javafx

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableObjectValue
import xarmanta.mainwindow.model.Commit

// Se encesitaba para el tableview... realmente no necesito que sean observables, pero yo que se....
class ObservableCommit(val commit: Commit): ObservableObjectValue<Commit> {
    override fun addListener(listener: ChangeListener<in Commit>?) {
        //no op
    }

    override fun addListener(listener: InvalidationListener?) {
        //no op
    }

    override fun removeListener(listener: InvalidationListener?) {
        //no op
    }

    override fun removeListener(listener: ChangeListener<in Commit>?) {
        //no op
    }

    override fun getValue(): Commit {
        return commit
    }

    override fun get(): Commit {
        return commit
    }

}
