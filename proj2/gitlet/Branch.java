package gitlet;

import java.io.Serializable;

/**
 * This command does NOT immediately switch to the newly created branch (just as in real Git).
 * /**Before you ever call branch, your code should be running with a default branch called “main”.
 **/


public class Branch implements Serializable {
    /**Name of commit within the branch as a string**/
    String name;
    /**the commit pointer**/
    Commit branchCommit;

    /**Gives the name of commits within a branch as a string and the commit itself**/
    public Branch(String stringBranchName, Commit commit) {
        name = stringBranchName;
        branchCommit = commit;
    }

    /**Sequences of commits being added**/
    public void addCommitToBranch(Commit commit) {
        branchCommit = commit;
    }
}

