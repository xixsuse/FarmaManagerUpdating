package com.klugesoftware.FarmaManagerUpdating.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class FTPConnectorTest {

/*
    @Test
    void downLoadDirectoryTest(){
        String PROPERTIES_FILE = "./resources/config/config.properties";
        Properties properties = new Properties();
        FTPConnector ftpConnector = new FTPConnector();
        try{
            properties.load(new FileInputStream(PROPERTIES_FILE));
            ftpConnector.downLoadDirectory(properties.getProperty("remotePath"),"",properties.getProperty("saveDirPath"));
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
*/
    @Test
    void uploadSingleFile(){
        String VERSION_PROPERTIES_FILE = "./resources/config/version.properties";
        String PROPERTIES_FILE = "./resources/config/config.properties";
        Properties properties = new Properties();
        FTPConnector ftpConnector = new FTPConnector();
        try {
            properties.load(new FileInputStream(PROPERTIES_FILE));
            boolean done = ftpConnector.upLoadFile(VERSION_PROPERTIES_FILE,properties.getProperty("remotePath")+"/version.properties");
            assertTrue(done);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}