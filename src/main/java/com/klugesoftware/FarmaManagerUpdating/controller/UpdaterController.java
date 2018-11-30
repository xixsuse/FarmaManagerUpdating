package com.klugesoftware.FarmaManagerUpdating.controller;


import com.klugesoftware.FarmaManagerUpdating.ftp.FTPConnector;
import com.klugesoftware.FarmaManagerUpdating.ftp.FTPUtilityTask;
import com.klugesoftware.FarmaManagerUpdating.utility.FileUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdaterController implements Initializable {

    @FXML private Label lblScarica;
    @FXML private Label lblEsegui;
    @FXML private Label lblMessage;
    @FXML private TextArea txtEsegui;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ListView<String> elencoMessaggi;

    private Logger logger = LogManager.getLogger(UpdaterController.class.getName());
    private FTPUtilityTask ftpUtilityTask;
    private FileUtility taskFileUtility;
    private String PROPERTIES_FILE = "./resources/config/config.properties";
    private String VERSION_PROPERTIES_FILE = "./resources/config/version.properties";
    private Properties properties;
    private ExecutorService executor;
    private ObservableList<String> listaMessaggi = FXCollections.observableArrayList();

    public UpdaterController(){
        try {
            properties = new Properties();
            properties.load(new FileInputStream(PROPERTIES_FILE));
        }catch (IOException ex){
            logger.error(ex.getStackTrace());
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        elencoMessaggi.setItems(listaMessaggi);
        lblMessage.setVisible(false);
        executor = Executors.newSingleThreadExecutor();
        lblEsegui.setDisable(true);
    }

    @FXML private void scaricaClicked(MouseEvent event){

        progressIndicator.setProgress(0);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ftpUtilityTask = new FTPUtilityTask(properties.getProperty("remotePath"),"",properties.getProperty("saveDirPath"));
        ftpUtilityTask.messageProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null){
                listaMessaggi.add(ftpUtilityTask.getMessage());
                elencoMessaggi.scrollTo(elencoMessaggi.getItems().size());
            }
        }));

        ftpUtilityTask.setOnSucceeded((succededEvent)->{
            progressIndicator.setProgress(1);
            lblMessage.setVisible(true);
            lblScarica.setDisable(true);
            lblEsegui.setDisable(false);
        });

        executor.execute(ftpUtilityTask);
    }

    @FXML private void eseguiClicked(MouseEvent event){

        progressIndicator.setProgress(0);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        taskFileUtility = new FileUtility();
        taskFileUtility.messageProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null){
                listaMessaggi.add(taskFileUtility.getMessage());
                elencoMessaggi.scrollTo(elencoMessaggi.getItems().size());
            }
        }));

        taskFileUtility.setOnSucceeded((succededEvent)->{
            progressIndicator.setProgress(1);
            lblMessage.setVisible(false);
            lblScarica.setDisable(true);
            lblEsegui.setDisable(true);
            try {
                Properties versioneProperties = new Properties();
                versioneProperties.load(new FileInputStream(VERSION_PROPERTIES_FILE));
                txtEsegui.setDisable(false);
                txtEsegui.setText("Aggiornamento dalla versione "+versioneProperties.getProperty("versionePrecedente")+" alla versione "+versioneProperties.getProperty("versioneAttuale"));
            }catch (IOException ex){
                logger.error("UpdateController->taskFileUtilityOnSuccedee: I can't, access to Version Properties file "+ex.getMessage());
            }

        });

        executor.execute(taskFileUtility);

    }

    @FXML private void esciClicked(MouseEvent event){
        System.exit(0);
    }

}
