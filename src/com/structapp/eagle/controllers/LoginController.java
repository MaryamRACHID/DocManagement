
package com.structapp.eagle.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.structapp.eagle.animations.Animations;
import com.structapp.eagle.database.DatabaseConnection;
import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.mask.RequieredFieldsValidators;
import com.structapp.eagle.mask.TextFieldMask;
import com.structapp.eagle.models.Document;
import com.structapp.eagle.notifications.NotificationType;
import com.structapp.eagle.notifications.NotificationsBuilder;
import com.structapp.eagle.resources.Constants;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    private final String INCORRECT_CREDENTIALS = "Utilisateur ou mot de passe incorrect";

    @FXML
    private JFXButton btnLogin;

    @FXML
    private Pane paneIcon;

    @FXML
    private JFXTextField txtUser;

    @FXML
    private JFXTextField txtPassword;

    @FXML
    private JFXPasswordField pfPassword;

    @FXML
    private FontAwesomeIconView icon;

    @FXML
    private Text title;

    private double x, y;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showPassword();
        setValidations();
        selectText();
        setMask();
        animations();
    }

    private void animations() {
        Animations.fadeInUp(title);
        Animations.fadeInUp(txtUser);
        Animations.fadeInUp(txtPassword);
        Animations.fadeInUp(pfPassword);
        Animations.fadeInUp(btnLogin);
    }

    private void setValidations() {
        RequieredFieldsValidators.toJFXPasswordField(pfPassword);
        RequieredFieldsValidators.toJFXTextField(txtUser);
        RequieredFieldsValidators.toJFXTextField(txtPassword);
    }

    private void setMask() {
        TextFieldMask.characterLimit(txtUser, 150);
        TextFieldMask.onlyNumbersAndLettersNotSpaces(txtPassword, 40);
        TextFieldMask.onlyNumbersAndLettersNotSpaces(pfPassword, 40);
    }

    private void selectText() {
        TextFieldMask.selectText(txtUser);
        TextFieldMask.selectText(txtPassword);
        TextFieldMask.selectText(pfPassword);
    }

    @FXML
    private void login() {
        String user = txtUser.getText().trim();
        String pass = pfPassword.getText().trim();

        if (user.isEmpty() && pass.isEmpty()) {
            Animations.shake(txtUser);
            Animations.shake(pfPassword);
            Animations.shake(paneIcon);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }
        
        if (user.isEmpty()) {
            txtUser.requestFocus();
            Animations.shake(txtUser);
            return;
        }
        
        if (pass.isEmpty()) {
            pfPassword.requestFocus();
            Animations.shake(pfPassword);
            Animations.shake(paneIcon);
            return;
        }
        try {
            String sql = "SELECT id, nameUser FROM Users WHERE email = BINARY ? AND pass = BINARY ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nameUser = rs.getString("nameUser");

                boolean result = DatabaseHelper.insertUserSession(id);
                if (result) {
                    loadMain();
                    NotificationsBuilder.create(NotificationType.SUCCESS, "Bienvenue dans le système " + nameUser + "!");
                }
            } else {
                NotificationsBuilder.create(NotificationType.ERROR, INCORRECT_CREDENTIALS);
                Animations.shake(txtUser);
                Animations.shake(pfPassword);
                Animations.shake(txtPassword);
                Animations.shake(paneIcon);
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    private void loadMain() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(Constants.MAIN_VIEW));
            Parent root = loader.load();
            MainController main = loader.getController();

            if (DatabaseHelper.getUserType().equals("Administrateur")) {
                main.getBtnStatistics().setVisible(true);
                main.getBtnAddUser().setVisible(true);
//                main.getBtnAbout().setVisible(true);
            } else {
                main.getBtnStatistics().setVisible(false);
                main.getBtnAddUser().setVisible(false);
                main.getBtnAbout().setVisible(false);
            }

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(new Image(Constants.STAGE_ICON));
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setMinHeight(Constants.MIN_HEIGHT);
            stage.setMinWidth(Constants.MIN_WIDTH);
            stage.setTitle(Constants.TITLE);
            stage.setScene(new Scene(root));
            stage.show();
            closeStage();

            root.setOnKeyPressed((KeyEvent e) -> {
                if (e.getCode().equals(KeyCode.F11)) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });

            stage.setOnCloseRequest(ev -> {
                DatabaseHelper.logout();
            });
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void showPassword() {
        txtPassword.managedProperty().bind(icon.pressedProperty());
        txtPassword.visibleProperty().bind(icon.pressedProperty());
        txtPassword.textProperty().bindBidirectional(pfPassword.textProperty());

        pfPassword.managedProperty().bind(icon.pressedProperty().not());
        pfPassword.visibleProperty().bind(icon.pressedProperty().not());

        icon.pressedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                icon.setIcon(FontAwesomeIcon.EYE);
            } else {
                icon.setIcon(FontAwesomeIcon.EYE_SLASH);
            }
        });
    }

    @FXML
    private void closeStage() {
        ((Stage) txtUser.getScene().getWindow()).close();
    }

    @FXML
    private void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private void dragged(MouseEvent event) {
        Stage stg = (Stage) btnLogin.getScene().getWindow();
        stg.setX(event.getScreenX() - x);
        stg.setY(event.getScreenY() - y);
    }
}
