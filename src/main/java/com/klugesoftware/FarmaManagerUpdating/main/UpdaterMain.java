package com.klugesoftware.FarmaManagerUpdating.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UpdaterMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("/com/klugesoftware/FarmaManagerUpdating/view/Updater.fxml"));
        Scene scene = new Scene((root));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }
}
