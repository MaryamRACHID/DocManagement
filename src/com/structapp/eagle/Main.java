package com.structapp.eagle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.resources.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        if (DatabaseHelper.checkIfUserExists() == 0) {
            startWindow(stage);
        } else {
            loginWindow(stage);
        }
    }

    private void loginWindow(Stage stage) {
        try {
            DatabaseHelper.logout();
            Parent root = FXMLLoader.load(getClass().getResource(Constants.LOGIN_VIEW));
            stage.getIcons().add(new Image(Constants.STAGE_ICON));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(Constants.TITLE);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startWindow(Stage stage) {
        System.out.println(getClass().getResource(Constants.START_VIEW));
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Constants.START_VIEW));
            stage.getIcons().add(new Image(Constants.STAGE_ICON));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.setTitle(Constants.TITLE);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
