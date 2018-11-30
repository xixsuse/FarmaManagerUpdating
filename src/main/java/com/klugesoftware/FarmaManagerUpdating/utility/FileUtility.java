package com.klugesoftware.FarmaManagerUpdating.utility;

import com.klugesoftware.FarmaManagerUpdating.ftp.FTPConnector;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The purpose of this class is:
 * - copy the updated file jar main folder
 * - delete the updated file and folder
 * - upload the file that denotes the installed version
 */
public class FileUtility extends Task {

    private Logger logger = LogManager.getLogger(FileUtility.class.getName());
    private final String PROPERTIES_FILE = "./resources/config/config.properties";
    private final String VERSION_PROPERTIES_FILE = "./resources/config/version.properties";
    private Properties properties;
    private Properties versioneProperties;

    public FileUtility() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream(PROPERTIES_FILE));
            versioneProperties = new Properties();
            versioneProperties.load(new FileInputStream(VERSION_PROPERTIES_FILE));
        } catch (IOException ex) {
            logger.error(ex.getStackTrace());
        }
    }

    @Override
    protected Object call() throws Exception {

        logger.info("Aggiornamento versione");
        logger.info("Copia di backup...");
        updateMessage("Sto aggiornando i files...");
        updateMessage("Copia di backup...");

        File backupLocationMain = new File(properties.getProperty("backupLocationMain"));
        if(backupLocationMain.exists() && backupLocationMain.isDirectory())
            FileUtils.deleteDirectory(backupLocationMain);
        backupLocationMain.mkdirs();

        File backupLocationLib = new File(properties.getProperty("backupLocationLib"));
        backupLocationLib.mkdirs();
        File backupLocationResources = new File(properties.getProperty("backupLocationResources"));
        backupLocationResources.mkdirs();

        FileUtils.copyDirectory(new File(properties.getProperty("targetLocationMain")),backupLocationMain);
        FileUtils.copyDirectory(new File(properties.getProperty("targetLocationLib")), backupLocationLib);
        FileUtils.copyDirectory(new File(properties.getProperty("targetLocationResources")), backupLocationResources);

        logger.info("Aggiorno l'applicazione...");
        updateMessage("Aggiorno l'applicazione...");

        File targetLocationMain = new File((properties.getProperty("targetLocationMain")));
        if (targetLocationMain.exists() && targetLocationMain.isDirectory()) {
            File[] listFiles = targetLocationMain.listFiles();
            for (int k = 0; k < listFiles.length; k++) {
                if (listFiles[k].isFile())
                    listFiles[k].delete();
            }
        }else
            targetLocationMain.mkdir();

        File targetLocationLib = new File(properties.getProperty("targetLocationLib"));
        if(targetLocationLib.exists() && targetLocationLib.isDirectory())
            FileUtils.deleteDirectory(targetLocationLib);
        targetLocationLib.mkdir();

        File targetLocationResources = new File(properties.getProperty("targetLocationResources"));
        if(targetLocationResources.exists() && targetLocationResources.isDirectory())
            FileUtils.deleteDirectory(targetLocationResources);
        targetLocationResources.mkdir();

        FileUtils.copyDirectory(new File(properties.getProperty("sourceLocationMain")), targetLocationMain);
        FileUtils.copyDirectory(new File(properties.getProperty("sourceLocationLib")), targetLocationLib);
        FileUtils.copyDirectory(new File(properties.getProperty("sourceLocationResources")), targetLocationResources);

        File sourceLocationRoot = new File(properties.getProperty("sourceLocationRoot"));
        if(sourceLocationRoot.exists() && sourceLocationRoot.isDirectory())
            FileUtils.deleteDirectory(sourceLocationRoot);

        //aggiorno versione e rinomino jar FarmaManager
        File mainDir = new File(properties.getProperty("targetLocationMain"));
        if(mainDir.exists() && mainDir.isDirectory()){
            File[] files = mainDir.listFiles();
            String nameFile = "";
            for (int i=0;i<files.length;i++){
                if(files[i].isFile()) {
                    nameFile = files[i].getName();
                    if (nameFile.contains("FarmaManager")) {
                        String[] temp = nameFile.split("-");
                        String[] temp2 = temp[1].split("\\.");
                        String jarVersion = temp2[0] + "." + temp2[1] + "." + temp2[2];
                        String newName = temp[0] + "." + temp2[3];
                        /*
                        JarFile jarFile  = new JarFile(properties.getProperty("targetLocationMain")+"//"+nameFile);
                        Manifest manifest = jarFile.getManifest();
                        Attributes attributes = manifest.getMainAttributes();
                        */
                            files[i].renameTo(new File(properties.getProperty("targetLocationMain") + "//" + newName));
                        try {
                            String tempVersionePrecedente = versioneProperties.getProperty("versioneAttuale");
                            versioneProperties.setProperty("versionePrecedente", tempVersionePrecedente);
                            versioneProperties.setProperty("versioneAttuale", jarVersion);
                            versioneProperties.store(new FileOutputStream(VERSION_PROPERTIES_FILE), null);
                        }catch(Exception ex){
                            logger.error("I'm writing version.properties: "+ex.getMessage());
                        }
                        FTPConnector ftp = new FTPConnector();
                        ftp.upLoadFile(VERSION_PROPERTIES_FILE,properties.getProperty("remotePath")+"/logs/version.properties");
                        logger.info("Versione installata: "+jarVersion);
                    }
                }
            }

        }

        logger.info("Operazione terminata.");
        updateMessage("Operazione terminata.");

        if(isCancelled()){
            updateMessage("Operazione cancellata!");
            logger.info("Operazione interrotta");
        }

        return null;
    }
}
