package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Directory structure mapping names to references to blobs and other subdirectories
 **/

public class Tree implements Serializable {

    /**
     * This contains the Branch name into BranchValue.
     **/
    Branch branchValue;
    /**
     * HashMap containing the Added Files.
     **/
    HashMap<String, Blob> addedFileMap;
    /**
     * HashMap containing the removed Files.
     **/
    HashMap<String, Blob> removedFileMap;
    /**
     * HashMap containing the commits(BranchValues).
     **/
    HashMap<String, Branch> commitBranchesMap;

    String currBranch;

    /**
     * An Object containing, updating the different HashMaps.
     **/
    public Tree() {
        addedFileMap = new HashMap<>();
        removedFileMap = new HashMap<>();
        commitBranchesMap = new HashMap<>();
        currBranch = null;
    }

    /**
     * Adding the currentBranch Value to BranchValue variable.
     **/
    public void addCurrentBranch(Branch branch) {
        this.branchValue = branch;
        commitBranchesMap.put(branch.name, branch); //key, val
    }

    public void cleanTree() {
        addedFileMap = new HashMap<>();
        removedFileMap = new HashMap<>();
    }

    public void addBranchToTree(Branch newBranch) {
        commitBranchesMap.put(newBranch.name, newBranch);
    }

    public void removeBranchFromTree(String branchName) {
        commitBranchesMap.remove(branchName);
    }


}