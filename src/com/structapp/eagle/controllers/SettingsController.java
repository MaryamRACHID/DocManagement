package com.structapp.eagle.controllers;

import com.jfoenix.controls.*;
import com.structapp.eagle.alerts.AlertType;
import com.structapp.eagle.alerts.AlertsBuilder;
import com.structapp.eagle.animations.Animations;
import com.structapp.eagle.database.DatabaseConnection;
import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.mask.RequieredFieldsValidators;
import com.structapp.eagle.mask.TextFieldMask;
import com.structapp.eagle.models.Users;
import com.structapp.eagle.notifications.NotificationType;
import com.structapp.eagle.notifications.NotificationsBuilder;
import com.structapp.eagle.preferences.Preferences;
import com.structapp.eagle.resources.Constants;
import com.structapp.eagle.util.CropImageProfile;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsController implements Initializable {

    private final long LIMIT = 1000000;

    private final String MESSAGE_PROFILE_IMAGE_SAVED = "Success, profile picture saved.";

    @FXML
    private StackPane stckSettings;

    @FXML
    private AnchorPane rootSettings;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtUser;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXPasswordField txtConfirmPassword;

    @FXML
    private JFXComboBox<String> cmbDialogTransition;

    @FXML
    private JFXTextArea txtBio;

    @FXML
    private JFXButton btnSave;

    @FXML
    private Pane imageProfileContainer;

    @FXML
    private Group parentImage;

    @FXML
    private HBox headerContainer;

    @FXML
    private Text textName;

    @FXML
    private Text textUserType;

    @FXML
    private ImageView imageViewProfile;

    @FXML
    private MaterialDesignIconView icon;

    private File imageFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        animationNodes();
        selectText();
        loadData();
        validations();
        setMask();
        setOptionsToComboBox();
        initializeProfileImage();
        effectEditImageProfile();
    }

    private void effectEditImageProfile() {
        Animations.fade(parentImage, imageProfileContainer, icon);
    }

    private void initializeProfileImage() {
        loadProfileImage();

        Circle circle = new Circle(45);
        circle.setCenterX(imageViewProfile.getFitWidth() / 2);
        circle.setCenterY(imageViewProfile.getFitHeight() / 2);
        imageViewProfile.setClip(circle);

        Circle clip = new Circle(45);
        clip.setCenterX(imageViewProfile.getFitWidth() / 2);
        clip.setCenterY(imageViewProfile.getFitHeight() / 2);
        imageProfileContainer.setClip(clip);
    }

    private void loadProfileImage() {
        Image image = null;
        try {
            String sql = "SELECT profileImage FROM Users WHERE id = ?";
            PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ps.setInt(1, DatabaseHelper.getSessionId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InputStream img = rs.getBinaryStream("profileImage");
                if (img != null) {
                    image = new Image(img, 100, 100, true, true);
                }
            }
            imageViewProfile.setImage(image);
        } catch (SQLException ex) {
            Logger.getLogger(DocsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadData() {
        try {
            String sql = "SELECT Users.nameUser, Users.email, Users.pass, Users.biography, Users.dialogTransition, Users.userType "
                    + "FROM Users INNER JOIN UserSession ON UserSession.userId = Users.id WHERE UserSession.id = 1";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                txtName.setText(rs.getString(1));
                textName.setText(rs.getString(1));
                txtUser.setText(rs.getString(2));
                txtPassword.setText(rs.getString(3));
                txtConfirmPassword.setText(rs.getString(3));
                txtBio.setText(rs.getString(4));
                initializeJFXComboBox(rs.getString(5));
                textUserType.setText(rs.getString(6));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
            AlertsBuilder.create(AlertType.ERROR, stckSettings, rootSettings, txtBio, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    @FXML
    private void updateCredentials() {
        String name = txtName.getText().trim();
        String user = txtUser.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        String bio = txtBio.getText().trim();

        if (name.isEmpty() && user.isEmpty() && password.isEmpty() && confirmPassword.isEmpty() && bio.isEmpty()) {
            Animations.shake(txtName);
            Animations.shake(txtUser);
            Animations.shake(txtPassword);
            Animations.shake(txtConfirmPassword);
            Animations.shake(txtBio);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_INSUFFICIENT_DATA);
            return;
        }

        if (name.isEmpty()) {
            txtName.requestFocus();
            Animations.shake(txtName);
            return;
        }

        if (user.isEmpty()) {
            txtUser.requestFocus();
            Animations.shake(txtUser);
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
            return;
        }

        if (!confirmPassword.equals(password)) {
            txtConfirmPassword.requestFocus();
            Animations.shake(txtPassword);
            Animations.shake(txtConfirmPassword);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_PASSWORDS_NOT_MATCH);
            return;
        }

        if (cmbDialogTransition.getSelectionModel().isEmpty()) {
            Animations.shake(cmbDialogTransition);
            return;
        }

        if (bio.isEmpty()) {
            txtBio.requestFocus();
            Animations.shake(txtBio);
            return;
        }

        if (DatabaseHelper.checkIfUserExists(user) != 0 && !user.equals(DatabaseHelper.getSessionUsername())) {
            txtUser.requestFocus();
            Animations.shake(txtUser);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_USER_ALREADY_EXISTS);
            return;
        }

        Users users = new Users();
        users.setId(DatabaseHelper.getSessionId());
        users.setNameUser(name);
        users.setEmail(user);
        users.setPass(password);
        users.setBiography(bio);
        users.setDialogTransition(getDialogTransition());

        boolean result = DatabaseHelper.updateUserFromSettings(users);
        if (result) {
            loadData();
            AlertsBuilder.create(AlertType.SUCCES, stckSettings, rootSettings, rootSettings, Constants.MESSAGE_ADDED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    private String getDialogTransition() {
        return cmbDialogTransition.getSelectionModel().getSelectedItem().toUpperCase();
    }

    private void initializeJFXComboBox(String dialogTransition) {
        switch (dialogTransition) {
            case "LEFT":
                cmbDialogTransition.setValue("Left");
            break;
                
            case "RIGHT":
                cmbDialogTransition.setValue("Right");
            break;
                
            case "TOP":
                cmbDialogTransition.setValue("Top");
            break;
                
            case "BOTTOM":
                cmbDialogTransition.setValue("Bottom");
            break;
                
            case "CENTER":
                cmbDialogTransition.setValue("Center");
            break;
        }
    }

    private void setOptionsToComboBox() {
        cmbDialogTransition.getItems().addAll("Left", "Right", "Top", "Bottom", "Center");
    }

    private void animationNodes() {
        Animations.fadeInUp(txtName);
        Animations.fadeInUp(txtUser);
        Animations.fadeInUp(txtPassword);
        Animations.fadeInUp(btnSave);
        Animations.fadeInUp(txtConfirmPassword);
        Animations.fadeInUp(txtBio);
        Animations.fadeInUp(cmbDialogTransition);
        Animations.fadeInUp(headerContainer);
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
        TextFieldMask.characterLimit(txtUser, 150);
        TextFieldMask.onlyNumbersAndLettersNotSpaces(txtPassword, 40);
        TextFieldMask.onlyNumbersAndLettersNotSpaces(txtConfirmPassword, 40);
    }

    @FXML
    private void showFileChooser() {
        imageFile = getImageFromFileChooser(getStage());
        if (imageFile != null) {
            if (imageFile.length() < LIMIT) {
                try {
                    CropImageProfile crop = new CropImageProfile(imageFile);
                    boolean result = DatabaseHelper.updateImageFromSettings(crop.getInputStream());
                    if (result) {
                        loadProfileImage();
                    } else {
                        NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
                    }
                    setInitialDirectory();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_IMAGE_LARGE);
            }
        }
    }

    private Stage getStage() {
        return (Stage) btnSave.getScene().getWindow();
    }

    private void setInitialDirectory() {
        Preferences preferences = Preferences.getPreferences();
        preferences.setInitialPathFileChooserSettingsController(imageFile.getParent());
        Preferences.writePreferencesToFile(preferences);
    }

    private File getImageFromFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterImages = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().addAll(extFilterImages);
        fileChooser.setInitialDirectory(getInitialDirectoy());
        fileChooser.setTitle("Select an image");

        File selectedImage = fileChooser.showOpenDialog(stage);
        return selectedImage;
    }

    private File getInitialDirectoy() {
        Preferences preferences = Preferences.getPreferences();
        File initPath = new File(preferences.getInitialPathFileChooserSettingsController());
        if (!initPath.exists()) {
            preferences.setInitialPathFileChooserSettingsController(System.getProperty("user.home"));
            Preferences.writePreferencesToFile(preferences);
            initPath = new File(preferences.getInitialPathFileChooserSettingsController());
        }
        return initPath;
    }
}
