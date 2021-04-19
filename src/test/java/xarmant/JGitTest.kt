package xarmant

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xarmant.mainwindow.infraestructure.ConsoleMonitor
import xarmanta.mainwindow.model.Commit
import xarmanta.mainwindow.shared.GitContext
import xarmanta.mainwindow.shared.XGit
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.lang.NoSuchMethodException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JGitTest {

	var jgit: Git? = null


	@BeforeAll
	fun init() {
		//static InitCommand init() Create an empty Git repository or reinitialize an existing one 
		Files.deleteIfExists(Path.of("/tmp/test"))
		jgit = Git.init().setDirectory(File("/tmp/test")).call()

		// static Git open(File dir)
		// static Git open(File dir, FS fs)		
		//  dir - the repository to open. May be either the GIT_DIR, or the working tree directory that contains .git.
		//  fs - filesystem abstraction to use when accessing the repository. 
		// jgit.open()
		/*Files.walk(Path.of("/tmp/test"))
  .sorted(Comparator.reverseOrder())
  .map(Path::toFile)
  .forEach(File::delete)
    Files.deleteIfExists(Path.of("/tmp/test"))
    val jgit = Git.init().setDirectory(File("/tmp/test")).call()
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
		 */
	}

	@AfterAll
	fun finalize() {
		assertNotNull(jgit)
		jgit!!.close()
	}

	// AddCommand add() Returns a command object to execute a Add command
	@Test
	fun add_command() {
		assertNotNull(jgit)
		val filename: String = "test.txt"
		Files.createFile(Path.of("/tmp/test/$filename"))
		jgit!!.add().addFilepattern(filename).call();
	}

	// ApplyCommand apply() Returns a command object to execute a apply command
	@Test
	fun aply_command() {
		assertNotNull(jgit)
	}

	// BlameCommand blame() Returns a command object to execute a blame command
	@Test
	fun blamne_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// CreateBranchCommand branchCreate() Returns a command object used to create branches
	@Test
	fun branchCreate_command() {
		assertNotNull(jgit)
	}

	// DeleteBranchCommand branchDelete() Returns a command object used to delete branches
	@Test
	fun branchDelete_command() {
		assertNotNull(jgit)
	}

	// ListBranchCommand branchList() Returns a command object used to list branches
	@Test
	fun branchList_command() {
		assertNotNull(jgit)
	}

	// RenameBranchCommand branchRename() Returns a command object used to rename branches
	@Test
	fun branchRename_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// CheckoutCommand checkout() Returns a command object to execute a checkout command
	@Test
	fun checkout_command() {
		assertNotNull(jgit)
	}

	// CherryPickCommand cherryPick() Returns a command object to execute a cherry-pick command
	@Test
	fun cherryPick_command() {
		assertNotNull(jgit)
	}


	// CleanCommand clean() Returns a command object to execute a clean command
	@Test
	fun clean_command() {
		assertNotNull(jgit)
	}

	// static CloneCommand cloneRepository() Returns a command object to execute a clone command
	@Test
	fun cloneRepository_command() {
		assertNotNull(jgit)
	}

	// CommitCommand commit() Returns a command object to execute a Commit command
	@Test
	fun commit_command() {
		assertNotNull(jgit)
		val filename: String = "test.txt"
		jgit!!.commit().setMessage("Commit $filename").call();
	}

	// DiffCommand diff() Returns a command object to execute a diff command
	@Test
	fun diff_command() {
		assertNotNull(jgit)
	}

	// FetchCommand fetch() Returns a command object to execute a Fetch command
	@Test
	fun fetch_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// StatusCommand status() Returns a command object to execute a status command
	@Test
	fun status_command() {
		assertNotNull(jgit)
	}

	// Repository getRepository()  
	@Test
	fun getRepository_command() {
		assertNotNull(jgit)
	}

	// LogCommand log() Returns a command object to execute a Log command
	@Test
	fun logcommand() {
		assertNotNull(jgit)
	}

	// LsRemoteCommand lsRemote() Returns a command object to execute a ls-remote command
	@Test
	fun lsRemote_command() {
		assertNotNull(jgit)
	}

	// MergeCommand merge() Returns a command object to execute a Merge command
	@Test
	fun merge_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// AddNoteCommand notesAdd() Returns a command to add notes to an object
	@Test
	fun notesAdd_command() {
		assertNotNull(jgit)
	}

	// ListNotesCommand notesList() Returns a command to list all notes
	@Test
	fun notesList_command() {
		assertNotNull(jgit)
	}

	// RemoveNoteCommand notesRemove() Returns a command to remove notes on an object
	@Test
	fun notesRemove_command() {
		assertNotNull(jgit)
	}

	// ShowNoteCommand notesShow() Returns a command to show notes on an object
	@Test
	fun notesShow_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// PullCommand pull() Returns a command object to execute a Pull command
	@Test
	fun pull_command() {
		assertNotNull(jgit)
	}

	// PushCommand push() Returns a command object to execute a Push command
	@Test
	fun push_command() {
		assertNotNull(jgit)
	}

	// RebaseCommand rebase() Returns a command object to execute a Rebase command
	@Test
	fun rebase_command() {
		assertNotNull(jgit)
	}

	// ReflogCommand reflog() Returns a command object to execute a reflog command
	@Test
	fun reflog_command() {
		assertNotNull(jgit)
	}

	// ResetCommand reset() Returns a command object to execute a reset command
	@Test
	fun reset_command() {
		assertNotNull(jgit)
	}

	// RevertCommand revert() Returns a command object to execute a revert command
	@Test
	fun revert_command() {
		assertNotNull(jgit)
	}

	// RmCommand rm() Returns a command object to execute a rm command
	@Test
	fun rm_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// StashApplyCommand stashApply() Returns a command object used to apply a stashed commit
	@Test
	fun stashApply_command() {
		assertNotNull(jgit)
	}

	// StashCreateCommand stashCreate() Returns a command object used to create a stashed commit
	@Test
	fun stashCreate_command() {
		assertNotNull(jgit)
	}

	// StashDropCommand stashDrop() Returns a command object used to drop a stashed commit
	@Test
	fun stashDrop_command() {
		assertNotNull(jgit)
	}

	// StashListCommand stashList() Returns a command object used to list stashed commits
	@Test
	fun stashList_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// SubmoduleAddCommand submoduleAdd() Returns a command object to execute a submodule add command
	@Test
	fun submoduleAdd_command() {
		assertNotNull(jgit)
	}


	// SubmoduleInitCommand submoduleInit( Returns a command object to execute a submodule init command
	@Test
	fun submoduleInit_command() {
		assertNotNull(jgit)
	}

	// SubmoduleStatusCommand submoduleStatus() Returns a command object to execute a submodule status command
	@Test
	fun submoduleStatus_command() {
		assertNotNull(jgit)
	}

	// SubmoduleSyncCommand submoduleSync() Returns a command object to execute a submodule sync command
	@Test
	fun submoduleSync_command() {
		assertNotNull(jgit)
	}

	// SubmoduleUpdateCommand submoduleUpdate() Returns a command object to execute a submodule update command
	@Test
	fun submoduleUpdate_command() {
		assertNotNull(jgit)
	}

	//////////////////////////////////////////////////////////////////////
	// TagCommand tag() Returns a command object to execute a Tag command
	@Test
	fun tag_command() {
		assertNotNull(jgit)
	}

	// DeleteTagCommand tagDelete() Returns a command object used to delete tags
	@Test
	fun tagDelete_command() {
		assertNotNull(jgit)
	}

	// ListTagCommand tagList() Returns a command object used to list tags
	@Test
	fun tagList_command() {
		assertNotNull(jgit)
	}

	// static Git wrap(Repository repo)
	@Test
	fun wrap_command() {
		assertNotNull(jgit)
	}
}
