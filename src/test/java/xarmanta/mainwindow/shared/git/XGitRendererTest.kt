package xarmanta.mainwindow.shared.git

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.spyk
import javafx.scene.paint.Color
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xarmanta.mainwindow.application.graph.ShapeDrawer
import xarmanta.mainwindow.application.graph.XGitGraphCalculator
import xarmanta.mainwindow.application.graph.XGitRenderer
import xarmanta.mainwindow.model.commit.Commit
import xarmanta.mainwindow.model.DrawableItem
import xarmanta.mainwindow.model.commit.Type
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XGitRendererTest {
    lateinit var jgit: Git
    lateinit var shapeDrawer: ShapeDrawer
    lateinit var printer: Printer
    lateinit var drawer: XGitRenderer

    @BeforeEach
    fun setUp() {
        printer = spyk(Printer())
        shapeDrawer = spyk(TestShapeDrawer(printer))
        drawer = XGitRenderer()
    }

    @Test
    fun `A commit should be drawn as a single point`() {
        createRepoOne()
        val graph = XGitGraphCalculator().buildCommits(jgit)

        drawer.renderGraph(graph)

        assertThat(graph[0].graphic).isEqualTo(listOf(DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0)))
    }

    @Test
    fun `Two commits should be in the same line`() {
        createRepoTwo()
        val graph = XGitGraphCalculator().buildCommits(jgit)

        drawer.renderGraph(graph)

        assertThat(graph[0].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
            )
        )
        assertThat(graph[1].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
            )
        )
    }

    @Test
    fun `Should draw forking lines`() {
        createRepoForking()
        val graph = XGitGraphCalculator().buildCommits(jgit)

        drawer.renderGraph(graph)

        assertThat(graph[0].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
            )
        )
        assertThat(graph[1].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 2.0, 2.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[2].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
            )
        )
    }

    @Test
    fun `Should draw forking and merging lines`() {
        createRepoForkingMerking()
        val graph = XGitGraphCalculator().buildCommits(jgit)

        drawer.renderGraph(graph)

        assertThat(graph[0].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 1.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[1].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 2.0, 2.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0)
            )
        )
        assertThat(graph[2].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[3].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
            )
        )
    }

    @Test
    fun `Should draw many forking lines`() {

        createRepoForManyForking()
        val graph = XGitGraphCalculator().buildCommits(jgit)

        drawer.renderGraph(graph)

        assertThat(graph[0].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
            )
        )
        assertThat(graph[1].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 2.0, 2.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[2].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 2.0, 2.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[3].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 2.0, 2.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[4].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[5].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
            )
        )
        assertThat(graph[6].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 3.0, 3.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 3.0, 3.0, 0.5, 1.0),
            )
        )
        assertThat(graph[7].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 3.0, 3.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 3.0, 3.0, 0.0, 0.0),
                DrawableItem(Type.LINE, 1.0, 1.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 2.0, 2.0, 0.5, 1.0),
                DrawableItem(Type.LINE, 3.0, 3.0, 0.5, 1.0),
            )
        )
        assertThat(graph[8].graphic).isEqualTo(
            listOf(
                DrawableItem(Type.LINE, 1.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 2.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.LINE, 3.0, 1.0, 0.0, 0.5),
                DrawableItem(Type.COMMIT, 1.0, 1.0, 0.0, 0.0),
            )
        )
    }

    private fun createRepoOne() {
        val testDir = Path.of("./one")
        if (Files.exists(testDir)) {
            jgit = Git.init().setDirectory(File("./one")).call()
            return
        }
        setUpRepo("one")
        newCommitNewFile("A", "one")
    }

    private fun createRepoTwo() {
        val testDir = Path.of("./two")
        if (Files.exists(testDir)) {
            jgit = Git.init().setDirectory(File("./two")).call()
            return
        }
        setUpRepo("two")
        newCommitNewFile("A", "two")
        newCommitNewFile("B", "two")
    }

    private fun createRepoForking() {
        val testDir = Path.of("./forking")
        if (Files.exists(testDir)) {
            jgit = Git.init().setDirectory(File("./forking")).call()
            return
        }
        setUpRepo("forking")
        val commitA = newCommitNewFile("A", "forking")
        newCommitNewFile("B", "forking")
        checkoutCommit(commitA)
        newCommitInNewBranch("aux", "C", "forking")
    }

    private fun createRepoForkingMerking() {
        val testDir = Path.of("./forkingMerking")
        if (Files.exists(testDir)) {
            jgit = Git.init().setDirectory(File("./forkingMerking")).call()
            return
        }
        setUpRepo("forkingMerking")
        val commitA = newCommitNewFile("A", "forkingMerking")
        val commitB = newCommitNewFile("B", "forkingMerking")
        checkoutCommit(commitA)
        newCommitInNewBranch("aux", "C", "forkingMerking")
        mergeCurrentBranchWith(commitB)
    }
    private fun createRepoForManyForking() {
        val testDir = Path.of("./manyForking")
        if (Files.exists(testDir)) {
            jgit = Git.init().setDirectory(File("./manyForking")).call()
            return
        }
        setUpRepo("manyForking")
        val commitA = newCommitNewFile("A", "manyForking")
        newCommitNewFile("B", "manyForking")
        stashNewFile("S", "manyForking")
        checkoutCommit(commitA)
        newCommitInNewBranch("aux", "C", "manyForking")
        val commitG = newCommitNewFile("G", "manyForking")
        checkoutCommit(commitA)
        newCommitInNewBranch("aux2", "D", "manyForking")
        newCommitNewFile("E", "manyForking")
        newCommitNewFile("F", "manyForking")
        checkoutCommit(commitG)
        wipNewFile("WIP", "manyForking")
        jgit = Git.init().setDirectory(File("./manyForking")).call()
    }

    private fun checkoutCommit(commitA: RevCommit) {
        jgit.checkout().setName(commitA.name).call()
    }

    private fun mergeCurrentBranchWith(commitB: RevCommit) {
        jgit.merge().include(commitB).setCommit(true).setMessage("M").call()
    }

    private fun setUpRepo(repoName: String) {
        val testDir = Path.of("./$repoName")
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete)
            Files.deleteIfExists(testDir)
        }
        jgit = Git.init().setDirectory(File("./$repoName")).call()

    }

    fun newCommitNewFile(filename: String, repoName: String): RevCommit {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./$repoName/$filename"))
        jgit.add().addFilepattern(filename).call();
        return jgit.commit().setMessage("$filename").call();
    }

    fun newCommitInNewBranch(branch: String, filename: String, repoName: String): RevCommit {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./$repoName/$filename"))
        jgit.branchCreate().setName(branch).call()
        jgit.checkout().setName(branch).call()
        jgit.add().addFilepattern(filename).call();
        return jgit.commit().setMessage("$filename").call();
    }

    fun stashNewFile(filename: String, repoName: String): RevCommit {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./$repoName/$filename"))
        jgit.add().addFilepattern(filename).call();
        return jgit.stashCreate().setIndexMessage("S").call();
    }

    fun wipNewFile(filename: String, repoName: String) {
        TimeUnit.MILLISECONDS.sleep(1000L)
        Files.createFile(Path.of("./$repoName/$filename"))
    }
}

class TestShapeDrawer(val printer: Printer) : ShapeDrawer {
    var buffer: StringBuilder = java.lang.StringBuilder()

    override fun drawCell(commit: Commit) {
        commit.graphic.forEach {
            when(it.type) {
                Type.LINE -> {
                    drawForkingLine(commit, it.fromX, it.toX, it.fromY, it.toY, Color.BLACK)
                }
                Type.COMMIT -> {
                    drawCommit(commit, it.fromX, it.fromY, Color.BLACK)
                }
                Type.STASH -> {
                    drawStash(commit, it.fromX, it.fromY, Color.BLACK)
                }
                Type.WIP -> {
                    drawCommit(commit, it.fromX, it.fromY, Color.BLACK)
                }
            }
        }
    }
    override fun drawStraightLine(commit: Commit, x: Double, yo: Double, yf: Double, color: Color) {
        buffer.append("|$x-" + commit.description.lowercase().substring(0, 1) + ", ${yo}->${yf}|")
        //buffer.append("|")
    }

    override fun drawForkingLine(commit: Commit, fromX: Double, toX: Double, yo: Double, yf: Double, color: Color) {
        buffer.append("/$fromX-$toX-${commit.description.lowercase()}, ${yo}->${yf}/")
    }

    override fun drawMergingLine(commit: Commit, fromX: Double, toX: Double, yo: Double, yf: Double, color: Color) {
        buffer.append("\\$fromX-$toX-${commit.description.lowercase()}, ${yo}->${yf}\\")
    }

    override fun drawCommit(commit: Commit, x: Double, y: Double, color: Color) {
        buffer.append("($x-" + commit.description + ")")
    }

    override fun drawStash(commit: Commit, x: Double, y: Double, color: Color) {
        buffer.append("[$x-S]")
    }

    override fun next() {
        printer.printline(buffer.toString())
        buffer = java.lang.StringBuilder()
    }
}

class Printer {
    fun printline(s: String) {
        println(s)
    }
}
