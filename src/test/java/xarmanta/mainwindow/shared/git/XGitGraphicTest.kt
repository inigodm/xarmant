package xarmanta.mainwindow.shared.git

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xarmanta.mainwindow.application.graph.XGitGraphCalculator
import xarmanta.mainwindow.model.Type
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XGitGraphicTest {

    lateinit var jgit: Git

    @BeforeEach
    fun setUp() {
        setUpRepo()
    }

    @Test
    fun `A commit should be an only point`() {
        val commit = newCommitNewFile("A")

        val commits = XGitGraphCalculator().buildCommits(jgit)

        assertThat(commits.size).isEqualTo(1)
        assertThat(commits[0].commit).isEqualTo(commit)
        assertThat(commits[0].lines[0].from.commit).isEqualTo(commit)
        assertThat(commits[0].lines[0].to.commit).isEqualTo(commit)
    }

    @Test
    fun `Two commits should be in the same line`() {
        val commitA = newCommitNewFile("A")
        val commitB = newCommitNewFile("B")

        val commits = XGitGraphCalculator().buildCommits(jgit)

        assertThat(commits.size).isEqualTo(2)
        assertThat(commits[0].commit).isEqualTo(commitB)
        assertThat(commits[1].commit).isEqualTo(commitA)
        assertThat(commits[0].lines[0].from.commit).isEqualTo(commitB)
        assertThat(commits[0].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].from.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[0].lines[0].type).isEqualTo(Type.COMMIT)
        assertThat(commits[1].lines[0].type).isEqualTo(Type.COMMIT)
    }

    @Test
    fun `When 2 different branches comes from same commit should be a forking off line`() {
        val commitA = newCommitNewFile("A")
        val commitB = newCommitNewFile("B")

        jgit.checkout().setName(commitA.name).call()

        val commitC = newCommitInNewBranch("aux", "C")

        val xGitGraphic = XGitGraphCalculator()
        val commits = xGitGraphic.buildCommits(jgit)

        assertThat(commits.size).isEqualTo(3)
        assertThat(commits[0].commit).isEqualTo(commitC)
        assertThat(commits[1].commit).isEqualTo(commitB)
        assertThat(commits[2].commit).isEqualTo(commitA)
        assertThat(commits[0].lines[0].from.commit).isEqualTo(commitC)
        assertThat(commits[0].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].from.commit).isEqualTo(commitB)
        assertThat(commits[1].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[2].lines[0].from.commit).isEqualTo(commitA)
        assertThat(commits[2].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[0].lines[0].color).isEqualTo(commits[1].lines[0].color)
        assertThat(commits[0].lines[0].type).isEqualTo(Type.COMMIT)
        assertThat(commits[1].lines[0].type).isEqualTo(Type.COMMIT)
    }

    @Test
    fun `A stash and a commits should be in the same line`() {
        val commitA = newCommitNewFile("A")
        val stash = stashNewFile("B")

        val commits = XGitGraphCalculator().buildCommits(jgit)

        assertThat(commits.size).isEqualTo(2)
        assertThat(commits[0].commit).isEqualTo(stash)
        assertThat(commits[1].commit).isEqualTo(commitA)
        assertThat(commits[0].lines[0].from.commit).isEqualTo(stash)
        assertThat(commits[0].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].from.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[0].type).isEqualTo(Type.STASH)
        assertThat(commits[1].type).isEqualTo(Type.COMMIT)
    }

    @Test
    fun `A WIP and a commits should be in the same line`() {
        val commitA = newCommitNewFile("A")
        wipNewFile("B")

        val commits = XGitGraphCalculator().buildCommits(jgit)

        val wipCommit = commits[0]

        assertThat(commits.size).isEqualTo(2)
        assertThat(commits[1].commit!!.name).isEqualTo(commitA.name)
        assertThat(commits[0].lines[0].from).isEqualTo(wipCommit)
        assertThat(commits[0].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].from.commit).isEqualTo(commitA)
        assertThat(commits[1].lines[0].to.commit).isEqualTo(commitA)
        assertThat(commits[0].type).isEqualTo(Type.WIP)
        assertThat(commits[1].type).isEqualTo(Type.COMMIT)
    }

    private fun setUpRepo() {
        val testDir = Path.of("./test")
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete)
            Files.deleteIfExists(testDir)
        }
        jgit = Git.init().setDirectory(File("./test")).call()

    }

    fun newCommitNewFile(filename: String) : RevCommit {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./test/$filename"))
        jgit.add().addFilepattern(filename).call();
        return jgit.commit().setMessage("Commit $filename").call();
    }

    fun newCommitInNewBranch(branch: String, filename: String) : RevCommit {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./test/$filename"))
        jgit.branchCreate().setName(branch).call()
        jgit.add().addFilepattern(filename).call();
        return jgit.commit().setMessage("Commit $filename").call();
    }
    fun stashNewFile(filename: String): RevCommit {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./test/$filename"))
        jgit.add().addFilepattern(filename).call();
        return jgit.stashCreate().call();
    }

    fun wipNewFile(filename: String) {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./test/$filename"))
    }

}

