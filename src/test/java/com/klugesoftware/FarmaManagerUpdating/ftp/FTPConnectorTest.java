package com.klugesoftware.FarmaManagerUpdating.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class FTPConnectorTest {

    @org.junit.jupiter.api.Test
    void getFtpClientTest() {
        FTPConnector ftpConnector = new FTPConnector();
        assertNotNull(ftpConnector.getFtpClient());
        ftpConnector.closeFtpClient();
        assertFalse(ftpConnector.getFtpClient().isConnected());
    }

    @Test
    void downLoadDirectoryTest(){
        String PROPERTIES_FILE = "./resources/config/config.properties";
        Properties properties = new Properties();
        FTPConnector ftpConnector = new FTPConnector();
        try{
            properties.load(new FileInputStream(PROPERTIES_FILE));
            ftpConnector.downloadDirectory(properties.getProperty("remotePath"),"",properties.getProperty("saveDirPath"));
            ftpConnector.closeFtpClient();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


}