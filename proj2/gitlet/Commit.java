package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;


/**
 * Represents a gitlet commit object.
 * Combinations of log messages, other metadata (commit date, author, etc.),
 * a reference to a tree, and references to parent commits.
 * <p>
 * does at a high level.
 *
 * @author Bryan Aguirre and Natalie Rivas
 */

public class Commit implements Serializable {

    /**
     * String of the Sha1 --> 1242v2v2452g2v2522
     **/
    String shaOneKey;
    /**
     * String of the commit Date --> SDF formatted.
     **/
    String commitDate;
    /**
     * String of the commit message when committing.
     **/
    String commitMessage;
    /**
     * A pointer towards the previous Sha1 Value.
     **/
    String prevShaOneKey;
    /**
     * HashMap of commits that are being added with Blobs.
     **/
    HashMap<String, Blob> commitOfAddedBlobs;
    /**
     * HashMap of commits that are being removed with Blobs.
     **/
    HashMap<String, Blob> commitOfRemovedBlobs;


    /**
     * The Commit Class Object.
     * It makes the initial commit within this function.
     * It gets the value of the new commits and makes the new commits
     **/

    public Commit(String stringCommitMessage, HashMap<String, Blob> blobMap, String shaOneVal) {
        commitValue(stringCommitMessage, blobMap, shaOneVal);
        Date newDate = new Date();
        SimpleDateFormat SDF = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        commitDate = SDF.format(newDate);
        if (stringCommitMessage.equals("initial commit")) {
            commitDate = "Wed Dec 31 16:00:00 1969 -0800";
        }
        commitOfRemovedBlobs = new HashMap<>();

    }

    public static Commit getCommitFromFile(String hashID) {
        File matchingFile = join(Repository.COMMIT_DIR, hashID);
        if (!matchingFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return Utils.readObject(matchingFile, Commit.class);
    }

    /**
     * Updating the commit Value into the variables.
     **/
    public void commitValue(String stringCommitMessage, HashMap<String, Blob> addedBlob, String sha1) {
        commitMessage = stringCommitMessage;
        commitOfAddedBlobs = addedBlob;
        prevShaOneKey = sha1;
    }

    /**
     * Making the Sha Value string using commitMessage, CommitDate, and the uniq message string.
     **/
    public String makeShaKey() {
        StringBuilder string = new StringBuilder();
        for (Blob blobVal : commitOfAddedBlobs.values()) {
            string.append(blobVal.toString());
        }
        shaOneKey = sha1(commitMessage, commitDate, string.toString());
        return shaOneKey;
    }
}