package com.structapp.eagle.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.structapp.eagle.alerts.AlertType;
import com.structapp.eagle.alerts.AlertsBuilder;
import com.structapp.eagle.animations.Animations;
import com.structapp.eagle.database.DatabaseConnection;
import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.mask.RequieredFieldsValidators;
import com.structapp.eagle.mask.TextFieldMask;
import com.structapp.eagle.models.Bureau;
import com.structapp.eagle.notifications.NotificationType;
import com.structapp.eagle.notifications.NotificationsBuilder;
import com.structapp.eagle.resources.Constants;
import com.structapp.eagle.util.ContextMenu;
import com.structapp.eagle.util.JFXDialogTool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BureauController implements Initializable {

    private ObservableList<Bureau> listBureau;

    private ObservableList<Bureau> filterBureau;

    @FXML
    private StackPane stckBureau;

    @FXML
    private AnchorPane rootBureau;

    @FXML
    private AnchorPane containerDeleteBureau;

    @FXML
    private AnchorPane containerAddBureau;

    @FXML
    private JFXButton btnUpdateBureau;

    @FXML
    private JFXButton btnSaveBureau;

    @FXML
    private TableView<Bureau> tblBureau;

    @FXML
    private TableColumn<Bureau, Integer> colId;

    @FXML
    private TableColumn<Bureau, String> colbureauName;

    @FXML
    private JFXButton btnAddBureau;

    @FXML
    private HBox rootSearchBureau;

    @FXML
    private TextField txtSearchNumber;

    @FXML
    private TextField txtSearchBureau;

    @FXML
    private JFXTextField txtBureauName;

    @FXML
    private Text titleAddBureau;

    private JFXDialogTool dialogAddBureau;

    private JFXDialogTool dialogDeleteBureau;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterBureau = FXCollections.observableArrayList();
        characterLimiter();
        setValidations();
        selectText();
        loadData();
        setMask();
        animateNodes();
        deleteUserDeleteKey();
        closeDialogWithTextFields();
        closeDialogWithEscapeKey();
        setContextMenu();
    }
    
    private void setContextMenu() {
        ContextMenu contextMenu = new ContextMenu(tblBureau);

        contextMenu.setActionEdit(ev -> {
            showDialogEditBureau();
            contextMenu.hide();
        });

        contextMenu.setActionDelete(ev -> {
            showDialogDeleteBureau();
            contextMenu.hide();
        });

        contextMenu.setActionDetails(ev -> {
            showDialogDetailsBureau();
            contextMenu.hide();
        });

        contextMenu.setActionRefresh(ev -> {
            loadData();
            contextMenu.hide();
        });

        contextMenu.show();
    }

    private void animateNodes() {
        Animations.fadeInUp(rootSearchBureau);
        Animations.fadeInUp(btnAddBureau);
        Animations.fadeInUp(tblBureau);
    }

    private void selectText() {
        TextFieldMask.selectText(txtBureauName);
    }

    private void setValidations() {
        RequieredFieldsValidators.toJFXTextField(txtBureauName);
    }

    private void characterLimiter() {
        TextFieldMask.onlyLetters(txtBureauName, 500);
    }

    private void setMask() {
        TextFieldMask.onlyLetters(txtBureauName, 150);
        TextFieldMask.onlyLetters(txtSearchBureau, 150);
    }

    @FXML
    private void showDialogddBureau() {
        resetValidation();
        disableTable();
        enableEditControls();
        rootBureau.setEffect(Constants.BOX_BLUR_EFFECT);

        titleAddBureau.setText("Ajouter un Bureau");
        btnUpdateBureau.setVisible(true);
        btnSaveBureau.setDisable(false);
        containerAddBureau.setVisible(true);
        btnSaveBureau.toFront();

        dialogAddBureau = new JFXDialogTool(containerAddBureau, stckBureau);
        dialogAddBureau.show();

        dialogAddBureau.setOnDialogOpened(ev -> {
            txtBureauName.requestFocus();
        });

        dialogAddBureau.setOnDialogClosed(ev -> {
            containerAddBureau.setVisible(false);
            tblBureau.setDisable(false);
            rootBureau.setEffect(null);
            cleanControls();
        });
    }

    @FXML
    private void closeDialogAddBureau() {
        if (dialogAddBureau != null) {
            dialogAddBureau.close();
        }
    }

    @FXML
    private void showDialogDeleteBureau() {
        if (tblBureau.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        disableTable();
        containerDeleteBureau.setVisible(true);
        rootBureau.setEffect(Constants.BOX_BLUR_EFFECT);

        dialogDeleteBureau = new JFXDialogTool(containerDeleteBureau, stckBureau);
        dialogDeleteBureau.show();

        dialogDeleteBureau.setOnDialogClosed(ev -> {
            containerDeleteBureau.setVisible(false);
            tblBureau.setDisable(false);
            rootBureau.setEffect(null);
            cleanControls();
        });

    }

    @FXML
    private void closeDialogDeleteBureau() {
        if (dialogDeleteBureau != null) {
            dialogDeleteBureau.close();
        }
    }

    @FXML
    private void showDialogEditBureau() {
        if (tblBureau.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        showDialogddBureau();
        titleAddBureau.setText("Modifier le Bureau");
        btnUpdateBureau.toFront();
        selectedRecord();
    }

    @FXML
    private void showDialogDetailsBureau() {
        if (tblBureau.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        showDialogddBureau();
        titleAddBureau.setText("Informations à propos du Bureau");
        btnUpdateBureau.setVisible(false);
        btnSaveBureau.setDisable(true);
        btnSaveBureau.toFront();
        disableEditControls();
        selectedRecord();

    }

    private void selectedRecord() {
        Bureau bureau = tblBureau.getSelectionModel().getSelectedItem();
        txtBureauName.setText(bureau.getBureauName());
    }

    @FXML
    private void loadData() {
        loadTable();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colbureauName.setCellValueFactory(new PropertyValueFactory<>("bureauName"));
    }

    private void loadTable() {
        ArrayList<Bureau> list = new ArrayList<>();
        try {
            String sql = "SELECT id, bureauName FROM bureau";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String bureauName = resultSet.getString("bureauName");
                list.add(new Bureau(id, bureauName));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            AlertsBuilder.create(AlertType.ERROR, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
        listBureau = FXCollections.observableArrayList(list);
        tblBureau.setItems(listBureau);
    }

    @FXML
    private void newBureau() {
        String name = txtBureauName.getText().trim();

        if (name.isEmpty()) {
            txtBureauName.requestFocus();
            Animations.shake(txtBureauName);
            return;
        }

        Bureau Bureau = new Bureau();
        Bureau.setBureauName(name);

        boolean result = DatabaseHelper.insertNewBureau(Bureau, listBureau);
        if (result) {
            loadData();
            cleanControls();
            closeDialogAddBureau();
            AlertsBuilder.create(AlertType.SUCCES, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_ADDED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }

    }

    @FXML
    private void deleteBureau() {
        boolean result = DatabaseHelper.deleteBureau(tblBureau, listBureau);
        if (result) {
            loadData();
            cleanControls();
            closeDialogDeleteBureau();
            AlertsBuilder.create(AlertType.SUCCES, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_DELETED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    @FXML
    private void updateBureau() {
        String name = txtBureauName.getText().trim();

        if (name.isEmpty()) {
            txtBureauName.requestFocus();
            Animations.shake(txtBureauName);
            return;
        }

        Bureau Bureau = tblBureau.getSelectionModel().getSelectedItem();
        Bureau.setId(Bureau.getId());
        Bureau.setBureauName(name);

        boolean result = DatabaseHelper.updateBureau(Bureau);
        if (result) {
            loadData();
            cleanControls();
            closeDialogAddBureau();
            AlertsBuilder.create(AlertType.SUCCES, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_UPDATED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    private void cleanControls() {
        txtBureauName.clear();
    }

    private void disableEditControls() {
        txtBureauName.setEditable(false);
    }

    private void enableEditControls() {
        txtBureauName.setEditable(true);
    }

    private void resetValidation() {
        txtBureauName.resetValidation();
    }

    private void disableTable() {
        tblBureau.setDisable(true);
    }

    private boolean validateEmailAddress(String email) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }

    private void closeDialogWithEscapeKey() {
        rootBureau.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogDeleteBureau();
            }

            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddBureau();
            }

            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                tblBureau.setDisable(false);
                rootBureau.setEffect(null);
                AlertsBuilder.close();
            }

        });
    }

    private void closeDialogWithTextFields() {
        txtBureauName.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddBureau();
            }
        });
    }

    private void deleteUserDeleteKey() {
        rootBureau.setOnKeyPressed(ev -> {
            if (ev.getCode().equals(KeyCode.DELETE)) {
                if (tblBureau.isDisable()) {
                    return;
                }

                if (tblBureau.getSelectionModel().getSelectedItems().isEmpty()) {
                    AlertsBuilder.create(AlertType.ERROR, stckBureau, rootBureau, tblBureau, Constants.MESSAGE_NO_RECORD_SELECTED);
                    return;
                }
                deleteBureau();
            }
        });
    }

    @FXML
    private void filterNameBureau() {
        String filterName = txtSearchBureau.getText().trim();
        if (filterName.isEmpty()) {
            tblBureau.setItems(listBureau);
        } else {
            filterBureau.clear();
            for (Bureau c : listBureau) {
                if (c.getBureauName().toLowerCase().contains(filterName.toLowerCase())) {
                    filterBureau.add(c);
                }
            }
            tblBureau.setItems(filterBureau);
        }
    }
}
