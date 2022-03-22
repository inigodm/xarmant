package xarmant

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xarmant.mainwindow.infraestructure.ConsoleMonitor
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.model.GitContext
import xarmanta.mainwindow.shared.git.XGit
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XGitTest {

    @BeforeAll
    fun setup() {
        /*Files.walk(Path.of("./test"))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
        Files.deleteIfExists(Path.of("./test"))
        val jgit = Git.init().setDirectory(File("./test")).call()
        newCommitNewFile(jgit, "A")
        newCommitNewFile(jgit, "B")
        newCommitNewFile(jgit, "C")
        jgit.checkout().setCreateBranch(true).setName("brancha").call();
        newCommitNewFile(jgit, "G")
        newCommitNewFile(jgit, "H")
        newCommitNewFile(jgit, "I")
        newCommitNewFile(jgit, "J")
        newCommitNewFile(jgit, "K")
        newCommitNewFile(jgit, "L")
        newCommitNewFile(jgit, "M")
        newCommitNewFile(jgit, "N")
        newCommitNewFile(jgit, "O")
        newCommitNewFile(jgit, "P")
        newCommitNewFile(jgit, "Q")
        jgit.checkout().setCreateBranch(true).setName("branchb").call();
        newCommitNewFile(jgit, "R")
        newCommitNewFile(jgit, "S")
        newCommitNewFile(jgit, "T")
        newCommitNewFile(jgit, "U")
        newCommitNewFile(jgit, "V")
        jgit.checkout().setCreateBranch(false).setName("master").call();
        newCommitNewFile(jgit, "E")
        newCommitNewFile(jgit, "F")
        jgit.checkout().setCreateBranch(true).setName("branchc").call();
        newCommitNewFile(jgit, "W")
        newCommitNewFile(jgit, "X")
        newCommitNewFile(jgit, "Y")
        newCommitNewFile(jgit, "Z")
        jgit.close()*/
    }

    fun `newCommitNewFile`(jgit: Git, filename: String) {
        Thread.sleep(1000L)
        Files.createFile(Path.of("./test/$filename"))
        jgit.add().addFilepattern(filename).call();
        jgit.commit().setMessage("Commit $filename").call();
    }

    @Test
    fun `commits are listed in desired order`() {
        val xgit = XGit(GitContext(null, File("./test")), ConsoleMonitor()).open()
        val commits = xgit.reverseWalkTimed()
        commits.forEach{
            println("${it.description} ${it.branches}")
        }
    }
}

fun XGit.reverseWalkTimed(): MutableList<Commit> {
    val begin = System.nanoTime()
    val commits = this.reverseWalk()
    val end = System.nanoTime()
    println("Elapsed: ${(end - begin)/1000000}ms")
    return commits
}

fun XGit.reverseWalk(): MutableList<Commit> {
    val mutableList : MutableList<Commit> = ArrayList()
    return mutableList
}
