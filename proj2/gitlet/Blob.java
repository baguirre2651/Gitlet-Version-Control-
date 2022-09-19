package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.readContents;
import static gitlet.Utils.readContentsAsString;

/**
 * blobs: The saved contents of files.
 * Gitlet saves many versions of files,
 * a single file might correspond to multiple blobs: each being tracked in a different commit.
 **/


public class Blob implements Serializable {

    /**
     * String containing the file name of the "blob".
     **/
    public String stringOfBlobName;
    /** Unique byte containing of the blob, when read with the Utils.readContents. **/
    /**
     * @source: GeeksforGeeks on using byte
     **/
    public byte[] blobContents;
    /**
     * Unique String byte containing of the blob, when read with the Utils.readContentAsString
     **/
    public String blobFileContents;

    public String blobEncryption;

    public String blobFile;

    /**
     * An object that stores the value of filename.
     **/
    public Blob(String filename) {
        stringOfBlobName = filename;
    }

    /**
     * A function that is reading in the files and storing their unique Values.
     **/
    public void readBlobFiles(File file) {
        blobContents = readContents(file);
        blobFileContents = readContentsAsString(file);
    }

    public String getBlobEncryption() {
        String cts = new String(blobContents);
        blobEncryption = Utils.sha1(cts + stringOfBlobName);
        return blobEncryption;
    }
}





