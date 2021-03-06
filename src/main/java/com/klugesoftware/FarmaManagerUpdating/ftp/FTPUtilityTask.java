package com.klugesoftware.FarmaManagerUpdating.ftp;

import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FTPUtilityTask extends Task {

    boolean singleFile = false;
    boolean wholeDirectory = false;
    private String remoteFilePath;
    private String savePath;
    private String parentDir;
    private String currentDir;
    private String saveDir;

    /**
     * Download a single file from FTP server
     * @param remoteFilePath path of the file on the server
     * @param savePath path of directory where the file will be stored
     */
    public FTPUtilityTask(String remoteFilePath, String savePath ){
        this.remoteFilePath = remoteFilePath;
        this.savePath = savePath;
        singleFile = true;
    }

    /**
     * Download a whole directrory from a FTP server.
     * @param parentDir Path of parent directory of the current directory being downloaded.
     * @param currentDir Path of the current directory being downloaded.
     * @param saveDir Path of directory where the whole remote directory will be downloaded and saved.
     */
    public FTPUtilityTask(String parentDir,String currentDir, String saveDir){
        this.parentDir = parentDir;
        this.currentDir = currentDir;
        this.saveDir = saveDir;
        wholeDirectory = true;
    }

    @Override
    protected Object call() throws Exception {
        FTPConnector ftpConnector = new FTPConnector();
        updateMessage("Sto scaricando l'aggiornamento...");
        File toSaveDir = new File(saveDir);
        if(toSaveDir.exists() && toSaveDir.isDirectory())
            FileUtils.deleteDirectory(toSaveDir);
        if(singleFile)
            ftpConnector.downLoadSingleFile(remoteFilePath,savePath);

        if(wholeDirectory)
            ftpConnector.downLoadDirectory(parentDir,currentDir,saveDir);

        updateMessage("Operazione terminata");

        if(isCancelled()){
            updateMessage("Operazione annullata!");
            return null;
        }

        return null;
    }
}
