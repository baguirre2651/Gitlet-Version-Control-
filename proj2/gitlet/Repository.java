package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author Bryan Aguirre and Natalie Rivas
 */
public class Repository implements Serializable {

    /**
     * The current working directory.
     **/
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     **/
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The Commit Directory
     **/
    public static final File COMMIT_DIR = join(CWD, ".gitlet/commits");
    /**
     * The Branch Directory
     **/
    public static final File BRANCH_DIR = join(CWD, ".gitlet/branches");


    /**
     * Creates an Object from the Tree Class, for Repository Class to use its variable to attain values.
     **/
    Tree getTree;
    /**
     * commit, the snapshots
     **/
    Commit commit;
    /**
     * Name for a reference
     **/
    Branch branch;
    /**
     * untracked Blob files in the HashMap
     **/
    HashMap<String, Blob> untrackedMap;

    /**
     * Creates Object for Stage
     **/
    public Repository() {
        try {
            getTree = Utils.readObject(join(GITLET_DIR, "Staging Area"), Tree.class);
            branch = getTree.branchValue;
        } catch (IllegalArgumentException e) {
            getTree = new Tree();
        }
        untrackedMap = new HashMap<>();
    }

    /**
     * Initalizes and creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit:
     **/

    public void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system"
                    + " already exists in the current directory.");
        } else {
            GITLET_DIR.mkdirs();
            Files.createDirectory(Paths.get(".gitlet/commits"));
            Files.createDirectory(Paths.get(".gitlet/branches"));
            Commit getCommit = new Commit("initial commit", new HashMap<>(), null);
            String getSha = getCommit.makeShaKey();
            commit = getCommit;
            Utils.writeObject(join(COMMIT_DIR, getSha), getCommit);
            Branch initHeadBranch = new Branch("main", getCommit);
            Utils.writeObject(join(BRANCH_DIR, initHeadBranch.name), initHeadBranch);
            getTree.currBranch = initHeadBranch.name;
            getTree.addCurrentBranch(initHeadBranch);
            branch = initHeadBranch;
            untrackedMap = new HashMap<>();
            Utils.writeObject(join(GITLET_DIR, "Staging Area"), getTree);
        }


    }

    /**
     * Adds a copy of the file to the staging area.
     **/
    public void add(String filename) {
        File file = join(CWD, filename);


        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Blob getBlobFile = new Blob(filename);
        getBlobFile.readBlobFiles(file);
        String getBlobSha = getBlobFile.getBlobEncryption();
        getTree.removedFileMap.remove(filename);
        getTree.addedFileMap.put(filename, getBlobFile);
        for (Blob getBlob2 : getTree.branchValue.branchCommit.commitOfAddedBlobs.values()) {
            if (getBlob2.blobEncryption.equals(getBlobSha)) {
                getTree.addedFileMap.remove(filename, getBlobFile);
            }
        }
        Utils.writeObject(join(GITLET_DIR, "Staging Area"), getTree);

    }

    /**
     * Saves a snapshot of tracked files in the current commit and staging area
     * so they can be stored at a later time.
     **/
    public void commit(String commitMessage) {
        if (getTree.addedFileMap.isEmpty() && getTree.removedFileMap.isEmpty()) { //checks if commit is empty
            System.out.println("No changes added to the commit.");
        }
        if (commitMessage.equals("") || commitMessage.equals(" ")) {
            System.out.println("Please enter a commit message.");
            return;
        } else {
            HashMap<String, Blob> BlobMap = new HashMap<>(getTree.addedFileMap);
            Commit getCommit = new Commit(commitMessage, BlobMap, branch.branchCommit.shaOneKey);
            String commitKey = getCommit.makeShaKey();
            getCommit.commitOfRemovedBlobs.putAll(getTree.removedFileMap);
            commit = getCommit;
            getTree.branchValue.addCommitToBranch(getCommit);
            getTree.cleanTree();
            Utils.writeObject(join(GITLET_DIR, "Staging Area"), getTree);
            Utils.writeObject(join(COMMIT_DIR, commitKey), getCommit);
            Utils.writeObject(join(BRANCH_DIR, getTree.branchValue.name), getTree.branchValue);
        }
    }


    /**
     * Remove Command*
     * Unstages the file and removes the file from the working directory.
     **/
    public void rm(String blobName) {
        Commit getCommit = branch.branchCommit;
        boolean reasonToRemove = false;// maybe branch maybe not
        if (getTree.addedFileMap.containsKey(blobName)) { //get tree maybe
            getTree.addedFileMap.remove(blobName);
            reasonToRemove = true;
        } else {
            while (getCommit != null) {
                if (getCommit.commitOfAddedBlobs.containsKey(blobName)) {
                    Blob blob = getCommit.commitOfAddedBlobs.get(blobName);
                    getTree.removedFileMap.put(blobName, blob);
                    Utils.restrictedDelete(join(CWD, blobName));
                    reasonToRemove = true;
                    break;
                }
                if (getCommit.prevShaOneKey == null) {
                    break;
                }
                String linked = getCommit.prevShaOneKey;
                getCommit = Utils.readObject(join(COMMIT_DIR, linked), Commit.class);
            }
        }
        if (!reasonToRemove) {
            System.out.println("No reason to remove the file.");
        }
        Utils.writeObject(join(GITLET_DIR, "Staging Area"), getTree);
    }


    /*** Displays information about each commit's history.**/
    public void log() {
        Commit getCommit = branch.branchCommit;
        while (getCommit != null) {
            System.out.println("===");
            System.out.println("commit " + getCommit.shaOneKey);
            System.out.println("Date: " + getCommit.commitDate);
            System.out.println(getCommit.commitMessage);
            System.out.println();
            String linked = getCommit.prevShaOneKey; //pointer to the prev key
            if (linked == null) {
                break;
            }
            getCommit = Utils.readObject(join(COMMIT_DIR, linked), Commit.class);
        }

    }

    public void logMessage(Commit getCommit) {
        System.out.println("===");
        System.out.println("commit " + getCommit.shaOneKey);
        System.out.println("Date: " + getCommit.commitDate);
        System.out.println(getCommit.commitMessage);
        System.out.println();
    }

    /**
     * Displays information about all commits ever made
     **/
    public void globalLog() {
        List<String> names = plainFilenamesIn(COMMIT_DIR);
        assert names != null;
        for (String commitUniqueVal : names) {
            Commit currentCommit = Commit.getCommitFromFile(commitUniqueVal);
            logMessage(currentCommit); //log message from above gives the commit and date format
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message, one per line.
     * If there are multiple such commits, it prints the ids out on separate lines.
     **/
    public void find(String commitMessage) {
        boolean doError = false;
        Utils.plainFilenamesIn(COMMIT_DIR);
        for (String name : Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR))) {
            Commit commit1 = Utils.readObject(join(COMMIT_DIR, name), Commit.class);
            if (commit1.commitMessage.equals(commitMessage)) {
                System.out.println(commit1.shaOneKey);
                doError = true;
            }
        }
        if (!doError) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     **/
    public void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            System.out.println("=== Branches ===");
            System.out.println("*" + getTree.branchValue.name);
            for (Branch branch : getTree.commitBranchesMap.values()) {
                if (!branch.name.equals(getTree.branchValue.name)) {
                    System.out.println(branch.name);
                }
            }
            System.out.println();
            System.out.println("=== Staged Files ===");
            ArrayList<String> stagedFile = new ArrayList<>(getTree.addedFileMap.keySet());
            Collections.sort(stagedFile);
            for (String sta : stagedFile) {
                System.out.println(getTree.addedFileMap.get(sta).stringOfBlobName);
            }
            System.out.println();
            System.out.println("=== Removed Files ===");
            ArrayList<String> removed = new ArrayList<>(getTree.removedFileMap.keySet());
            Collections.sort(removed);
            for (String sta : removed) {
                System.out.println(getTree.removedFileMap.get(sta).stringOfBlobName);
            }
            System.out.println();
            System.out.println("=== Modifications Not Staged For Commit ===");
            System.out.println();
            System.out.println("=== Untracked Files ===");
            System.out.println();
        }
    }


    /**
     * Takes the version of the existing file and puts it in the working directory.
     * overwrites the version of the file that's already there.
     * (new file of the version is not staged)
     **/

    public void checkout(String filename) {
        Blob temp1;
        Commit commit1 = branch.branchCommit;
        HashMap<String, Blob> blobFiles = commit1.commitOfAddedBlobs;
        boolean fileExist = false;
        for (String string : blobFiles.keySet()) {
            if (filename.equals(string)) {
                File file = new File(filename);
                String contents = blobFiles.get(string).blobFileContents;
                Utils.writeContents(file, contents);
                temp1 = new Blob(file.getName());
                temp1.readBlobFiles(file);
                fileExist = true;
                break;
            }
        }
        if (!fileExist) {
            System.out.println("File does not exist in that commit."); //checkout error
        }
        Utils.writeObject(join(GITLET_DIR, "Staging Area"), getTree);
    }

    /**
     * Takes the version of the file in the commit with the given commitID and puts it in the working directory.
     **/
    public void checkout(String commitId, String filename) {
        Blob temp1 = null;
        String fullname = "";
        List<String> names = Utils.plainFilenamesIn(COMMIT_DIR);
        boolean commitIdExist = false;
        if (names == null) {
            System.out.println("No commit with that id exists.");
            return;
        } else {
            for (String name : names) {
                if (name.startsWith(commitId)) {
                    commitIdExist = true;
                    fullname = name;
                    break;
                }
            }
        }
        if (!commitIdExist) {
            System.out.println("No commit with that id exists.");
            return;

        } else {
            Commit commit2 = readObject(join(COMMIT_DIR, fullname), Commit.class);
            HashMap<String, Blob> blobFiles = commit2.commitOfAddedBlobs;
            boolean fileExist = false;
            for (String string : blobFiles.keySet()) {
                if (filename.equals(string)) {
                    File file = new File(filename);
                    String contents = blobFiles.get(string).blobFileContents;
                    Utils.writeContents(file, contents);
                    temp1 = new Blob(file.getName());
                    temp1.readBlobFiles(file);
                    fileExist = true;
                    break;
                }
            }
            if (!fileExist) {
                System.out.println("File does not exist in that commit.");
                return;
            }
        }
        Utils.writeObject(join(GITLET_DIR, "Staging Area"), getTree);
    }

    /**
     * checkout branch
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     **/ //help
    public void checkoutbranch(String branchName) {
        if (!getTree.commitBranchesMap.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        Branch targetBranch = readObject(join(BRANCH_DIR, branchName), Branch.class);
        Commit commitObj = targetBranch.branchCommit;
        if (getTree.currBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
    }


    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch
     **/
    public void branch(String branchName) {
        if (getTree.commitBranchesMap.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            Commit commitVal = getTree.branchValue.branchCommit;
            Branch newBranch = new Branch(branchName, commitVal);
            getTree.addBranchToTree(newBranch);
            writeObject(join(BRANCH_DIR, newBranch.name), newBranch);
            writeObject(join(GITLET_DIR, "Staging Area"), getTree);
        }
    }

    /**
     * Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     **/
    public void rm_branchCommand(String branchName) {
        if (branchName.equals(getTree.branchValue.name)) {
            System.out.println("No need to checkout the current branch.");
        }
        if (!getTree.commitBranchesMap.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else {
            Branch branch = getTree.commitBranchesMap.get(branchName);
            branch.addCommitToBranch(null);
            getTree.removeBranchFromTree(branchName);
            System.out.println("Cannot remove the current branch.");
            writeObject(join(GITLET_DIR, "Staging Area"), getTree);
        }
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     **/
    public void reset(String commitId) {
    }

    public void merge() {
    }

}

