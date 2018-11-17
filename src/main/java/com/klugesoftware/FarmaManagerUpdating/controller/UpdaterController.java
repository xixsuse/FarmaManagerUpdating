package com.klugesoftware.FarmaManagerUpdating.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdaterController implements Initializable {

    @FXML private Label lblScarica;
    @FXML private Label lblEsegui;
    @FXML private Label lblMessage;
    @FXML private TextArea txtEsegui;


    public void initialize(URL location, ResourceBundle resources) {
        lblMessage.setDisable(true);
    }

    @FXML private void scaricaClicked(MouseEvent event){

    }

    @FXML private void eseguiClicked(MouseEvent event){

    }

    @FXML private void esciClicked(MouseEvent event){
        System.exit(0);
    }

}
