package com.klugesoftware.FarmaManagerUpdating.ftp;


import javafx.scene.control.Alert;
import org.apache.commons.net.ftp.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class FTPConnector {

    private final String FTP_HOST_NAME="ftp.klugesoftware.com";
    private final String FTP_USER_NAME="farmamanager@klugesoftware.com";
    private final String FTP_PASSWORD="builder163";
    private Logger logger = LogManager.getLogger(FTPConnector.class.getName());
    private FTPClient ftpClient;
    private FTPClientConfig ftpclientConfig;

    public FTPConnector(){
        try {
            int reply;
            ftpClient = new FTPClient();
            ftpclientConfig = new FTPClientConfig();
            ftpClient.configure(ftpclientConfig);
            ftpClient.connect(FTP_HOST_NAME);
            ftpClient.login(FTP_USER_NAME,FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)){
                ftpClient.disconnect();
                logger.error("Ci sono problemi di connessione ftp.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore di Connessione");
                alert.setHeaderText("Errore di Connessione");
                alert.setContentText("Verificare la connessione!");
                alert.showAndWait();
                ftpClient = null;
            }
        }catch(Exception ex){
            logger.error("Host name: "+FTP_HOST_NAME+"\n");
            logger.error(ex.getMessage());
        }
    }

    public FTPClient getFtpClient(){
        return ftpClient;
    }

    public void closeFtpClient(){
        try {
            if(ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }catch(IOException ex){
            logger.error(ex.getStackTrace());
        }
    }

    /**
     * Download a single file from FTP server
     * @param remoteFilePath path of the file on the server
     * @param savePath path of directory where the file will be stored
     * @return true if the files was downloaded successfully, false otherwise
     */
    public boolean downloadSingleFile(String remoteFilePath, String savePath) throws IOException{
        OutputStream outputStream = null;
        try{
            File downloadFile = new File(savePath);
            File parentDir = downloadFile.getParentFile();
            if(!parentDir.exists()){
                parentDir.mkdir();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath,outputStream);
        }catch(IOException ex){
            logger.error(ex);
        }finally {
            if(outputStream != null)
                outputStream.close();
        }
        return false;
    }

    /**
     * Download a whole directrory from a FTP server.
     * @param parentDir Path of parent directory of the current directory being downloaded.
     * @param currentDir Path of the current directory being downloaded.
     * @param saveDir Path of directory where the whole remote directory will be downloaded and saved.
     * @throws IOException If any network or IO error occured.
     */
    public void downloadDirectory(String parentDir,String currentDir, String saveDir) throws IOException{
        String dirToList = parentDir;
        if(!currentDir.equals("")){
            dirToList += "/"+currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if(subFiles != null && subFiles.length > 0){
            for(FTPFile aFile : subFiles){
                String currentFileName = aFile.getName();
                if(currentFileName.equals(".") || currentFileName.equals("..")){
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/" +currentFileName;
                if(currentDir.equals("")){
                    filePath = parentDir + "/" + currentFileName;
                }
                String newDirPath = saveDir + parentDir + File.separator + currentDir + File.separator+ currentFileName;
                if (currentDir.equals("")) {
                    newDirPath = saveDir + parentDir + File.separator
                            + currentFileName;
                }
                if(aFile.isDirectory()){
                    //create the directory in saveDir
                    File newDir = new File(newDirPath);
                    boolean created = newDir.mkdirs();
                    if(created)
                        logger.info("Created the director:"+newDirPath);
                    else
                        logger.info("Could not create the directory: "+newDirPath);

                    //download the sub directory
                    downloadDirectory(dirToList,currentFileName,saveDir);
                }else{
                    //download the file
                    boolean success = downloadSingleFile(filePath,newDirPath);
                    if(success){
                        logger.info("Downloaded the file: "+filePath);
                    }else
                        logger.info("Could not download the file: "+filePath);
                }
            }
        }
    }
}
