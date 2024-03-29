package com.structapp.eagle.controllers;

import com.jfoenix.controls.*;
import com.structapp.eagle.animations.Animations;
import com.structapp.eagle.database.DatabaseConnection;
import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.mask.RequieredFieldsValidators;
import com.structapp.eagle.mask.TextFieldMask;
import com.structapp.eagle.models.Users;
import com.structapp.eagle.notifications.NotificationType;
import com.structapp.eagle.notifications.NotificationsBuilder;
import com.structapp.eagle.resources.Constants;
import com.structapp.eagle.util.DefaultProfileImage;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartController implements Initializable {

    @FXML
    private StackPane stckStart;

    @FXML
    private Text title;

    @FXML
    private Pane paneStep1;

    @FXML
    private Text textStep1;

    @FXML
    private Pane paneControlsStep1;

    @FXML
    private JFXButton btnStep1;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXTextField txtUser;

    @FXML
    private JFXPasswordField txtConfirmPassword;

    @FXML
    private Pane paneStep2;

    @FXML
    private Text textStep2;

    @FXML
    private JFXTextArea txtBio;

    @FXML
    private HBox hBoxStep2;

    @FXML
    private Pane paneStep3;

    @FXML
    private Text textStep3;

    @FXML
    private JFXComboBox<String> cmbDialogTransition;

    @FXML
    private HBox hBoxStep3;

    @FXML
    private Pane paneFinish;

    @FXML
    private Text finishText;

    @FXML
    private JFXSpinner spinner;

    @FXML
    private JFXButton btnStart;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Text textProgressBar;

    private double x, y;

    private String name, user, password, confirmPassword, bio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startConfig();
        selectText();
        setMask();
        validations();
        StartStepOne();
        setOptionsToComboBox();
    }

    private void setOptionsToComboBox() {
        cmbDialogTransition.getItems().addAll("Left", "Right", "Top", "Bottom", "Center");
        cmbDialogTransition.setValue("Center");
    }

    private void startConfig() {
        progressBar.setProgress(0.00);
        textProgressBar.setText("1 de 3");

        Animations.fadeInUp(title);
        Animations.fadeInUp(textProgressBar);
        Animations.fadeInUp(progressBar);
    }

    @FXML
    private void StartStepOne() {
        paneStep1.setVisible(true);
        paneStep2.setVisible(false);

        textProgressBar.setText("1 de 3");
        Animations.progressAnimation(progressBar, 0.00);
        Animations.fadeInUp(paneStep1);
        Animations.fadeInUp(paneControlsStep1);
        Animations.fadeInUp(textStep1);
        Animations.fadeInUp(btnStep1);
    }

    @FXML
    private void stepOneToStepTwo() {
        name = txtName.getText().trim();
        user = txtUser.getText().trim();
        password = txtPassword.getText().trim();
        confirmPassword = txtConfirmPassword.getText().trim();

        if (name.isEmpty()) {
            txtName.requestFocus();
            Animations.shake(txtName);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }

        if (user.isEmpty()) {
            txtUser.requestFocus();
            Animations.shake(txtUser);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }

        if (user.length() < 4) {
            txtUser.requestFocus();
            Animations.shake(txtUser);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ENTER_AT_LEAST_4_CHARACTERES);
            return;
        }

        if (password.isEmpty()) {
            txtPassword.requestFocus();
            Animations.shake(txtPassword);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }

        if (password.length() < 4) {
            txtPassword.requestFocus();
            Animations.shake(txtPassword);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ENTER_AT_LEAST_4_CHARACTERES);
            return;
        }

        if (confirmPassword.isEmpty()) {
            txtConfirmPassword.requestFocus();
            Animations.shake(txtConfirmPassword);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }

        if (!confirmPassword.equals(password)) {
            Animations.shake(txtConfirmPassword);
            Animations.shake(txtPassword);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_PASSWORDS_NOT_MATCH);
            return;
        }

        startStepTwo();
    }

    @FXML
    private void startStepTwo() {
        paneStep1.setVisible(false);
        paneStep2.setVisible(true);
        paneStep3.setVisible(false);

        textProgressBar.setText("2 de 3");
        Animations.progressAnimation(progressBar, 0.33);
        Animations.fadeInUp(paneStep2);
        Animations.fadeInUp(textStep2);
        Animations.fadeInUp(txtBio);
        Animations.fadeInUp(hBoxStep2);
    }

    @FXML
    private void stepTwoToStepThree() {
        bio = txtBio.getText().trim();

        if (bio.isEmpty()) {
            Animations.shake(txtBio);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }

        startStepThree();
    }

    private void startStepThree() {
        paneStep2.setVisible(false);
        paneStep3.setVisible(true);

        textProgressBar.setText("3 de 3");
        Animations.progressAnimation(progressBar, 0.66);
        Animations.fadeInUp(paneStep3);
        Animations.fadeInUp(textStep3);
        Animations.fadeInUp(cmbDialogTransition);
        Animations.fadeInUp(hBoxStep3);
    }

    @FXML
    private void finish() {
        insertUserInDB();
        DatabaseHelper.insertUserSession(1);

        paneStep3.setVisible(false);
        paneFinish.setVisible(true);

        textProgressBar.setText("Finalized");
        Animations.progressAnimation(progressBar, 1);
        Animations.fadeInUp(paneFinish);
        Animations.fadeInUp(spinner);
        Animations.fadeOutWithDuration(btnStart);
        Animations.fadeOutWithDuration(finishText);

        PauseTransition pt = new PauseTransition(Duration.seconds(3));
        pt.setOnFinished(ev -> {
            Animations.fadeOut(spinner);
            Animations.fadeInUp(btnStart);
            Animations.fadeInUp(finishText);
        });

        pt.play();
    }

    private void insertUserInDB() {
        Users users = new Users();
        users.setNameUser(name);
        users.setEmail(user);
        users.setPass(password);
        users.setDialogTransition(getDialogTransition());
        users.setUserType("Administrateur");

        try {
            users.setProfileImage(DefaultProfileImage.getImage(name));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean result = DatabaseHelper.insertNewUser(users);
        if (result) {
            updateUserInDB(users);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    private void updateUserInDB(Users users) {
        try {
            String sql = "UPDATE Users SET  dialogTransition = ? WHERE id = 1";
            PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareCall(sql);
            stmt.setString(1, users.getDialogTransition());
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getDialogTransition() {
        return cmbDialogTransition.getSelectionModel().getSelectedItem().toUpperCase();
    }

    @FXML
    private void mainWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Constants.MAIN_VIEW));
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Constants.STAGE_ICON));
            stage.initStyle(StageStyle.DECORATED);
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

            NotificationsBuilder.create(NotificationType.SUCCESS, "Bienvenue dans le système " + name + "!");
        } catch (IOException ex) {
            Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeStage() {
        ((Stage) txtUser.getScene().getWindow()).close();
    }

    @FXML
    private void closeWindow() {
        System.exit(0);
    }

    @FXML
    private void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    
    @FXML
    private void dragged(MouseEvent event) {
        Stage stg = (Stage) btnStart.getScene().getWindow();
        stg.setX(event.getScreenX() - x);
        stg.setY(event.getScreenY() - y);
    }

    @FXML
    private void alert() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();

        String body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        JFXDialog dialog = new JFXDialog(stckStart, dialogLayout, JFXDialog.DialogTransition.valueOf(getDialogTransition()));
        dialogLayout.setBody(new Label(body));
        dialog.getStyleClass().add("jfx-dialog-overlay-pane");
        dialog.show();
    }

    private void selectText() {
        TextFieldMask.selectText(txtName);
        TextFieldMask.selectText(txtUser);
        TextFieldMask.selectText(txtPassword);
        TextFieldMask.selectText(txtConfirmPassword);
        TextFieldMask.selectTextToJFXTextArea(txtBio);
    }

    private void validations() {
        RequieredFieldsValidators.toJFXTextArea(txtBio);
        RequieredFieldsValidators.toJFXComboBox(cmbDialogTransition);
        RequieredFieldsValidators.toJFXTextField(txtName);
        RequieredFieldsValidators.toJFXTextField(txtUser);
        RequieredFieldsValidators.toJFXPasswordField(txtPassword);
        RequieredFieldsValidators.toJFXPasswordField(txtConfirmPassword);
    }

    private void setMask() {
        TextFieldMask.onlyLetters(txtName, 40);
        TextFieldMask.onlyEmail(txtUser, 70);
        TextFieldMask.onlyNumbersAndLettersNotSpaces(txtConfirmPassword, 40);
        TextFieldMask.onlyNumbersAndLettersNotSpaces(txtPassword, 40);
    }
}
