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
import com.structapp.eagle.models.Service;
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

public class ServiceController implements Initializable {

    private ObservableList<Service> listService;

    private ObservableList<Service> filterService;

    @FXML
    private StackPane stckService;

    @FXML
    private AnchorPane rootService;

    @FXML
    private AnchorPane containerDeleteService;

    @FXML
    private AnchorPane containerAddService;

    @FXML
    private JFXButton btnUpdateService;

    @FXML
    private JFXButton btnSaveService;

    @FXML
    private TableView<Service> tblService;

    @FXML
    private TableColumn<Service, Integer> colId;

    @FXML
    private TableColumn<Service, String> colServiceName;

    @FXML
    private JFXButton btnAddService;

    @FXML
    private HBox rootSearchService;

    @FXML
    private TextField txtSearchNumber;

    @FXML
    private TextField txtSearchService;

    @FXML
    private JFXTextField txtServiceName;

    @FXML
    private Text titleAddService;

    private JFXDialogTool dialogAddService;

    private JFXDialogTool dialogDeleteService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterService = FXCollections.observableArrayList();
        setValidations();
        selectText();
        loadData();
        animateNodes();
        deleteUserDeleteKey();
        closeDialogWithTextFields();
        closeDialogWithEscapeKey();
        setContextMenu();
    }
    
    private void setContextMenu() {
        ContextMenu contextMenu = new ContextMenu(tblService);

        contextMenu.setActionEdit(ev -> {
            showDialogEditService();
            contextMenu.hide();
        });

        contextMenu.setActionDelete(ev -> {
            showDialogDeleteService();
            contextMenu.hide();
        });

        contextMenu.setActionDetails(ev -> {
            showDialogDetailsService();
            contextMenu.hide();
        });

        contextMenu.setActionRefresh(ev -> {
            loadData();
            contextMenu.hide();
        });

        contextMenu.show();
    }

    private void animateNodes() {
        Animations.fadeInUp(rootSearchService);
        Animations.fadeInUp(btnAddService);
        Animations.fadeInUp(tblService);
    }

    private void selectText() {
        TextFieldMask.selectText(txtServiceName);
    }

    private void setValidations() {
        RequieredFieldsValidators.toJFXTextField(txtServiceName);
    }



    @FXML
    private void showDialogddService() {
        resetValidation();
        disableTable();
        enableEditControls();
        rootService.setEffect(Constants.BOX_BLUR_EFFECT);

        titleAddService.setText("Ajouter un Service");
        btnUpdateService.setVisible(true);
        btnSaveService.setDisable(false);
        containerAddService.setVisible(true);
        btnSaveService.toFront();

        dialogAddService = new JFXDialogTool(containerAddService, stckService);
        dialogAddService.show();

        dialogAddService.setOnDialogOpened(ev -> {
            txtServiceName.requestFocus();
        });

        dialogAddService.setOnDialogClosed(ev -> {
            containerAddService.setVisible(false);
            tblService.setDisable(false);
            rootService.setEffect(null);
            cleanControls();
        });
    }

    @FXML
    private void closeDialogAddService() {
        if (dialogAddService != null) {
            dialogAddService.close();
        }
    }

    @FXML
    private void showDialogDeleteService() {
        if (tblService.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckService, rootService, tblService, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        disableTable();
        containerDeleteService.setVisible(true);
        rootService.setEffect(Constants.BOX_BLUR_EFFECT);

        dialogDeleteService = new JFXDialogTool(containerDeleteService, stckService);
        dialogDeleteService.show();

        dialogDeleteService.setOnDialogClosed(ev -> {
            containerDeleteService.setVisible(false);
            tblService.setDisable(false);
            rootService.setEffect(null);
            cleanControls();
        });

    }

    @FXML
    private void closeDialogDeleteService() {
        if (dialogDeleteService != null) {
            dialogDeleteService.close();
        }
    }

    @FXML
    private void showDialogEditService() {
        if (tblService.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckService, rootService, tblService, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        showDialogddService();
        titleAddService.setText("Modifier le Service");
        btnUpdateService.toFront();
        selectedRecord();
    }

    @FXML
    private void showDialogDetailsService() {
        if (tblService.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckService, rootService, tblService, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        showDialogddService();
        titleAddService.setText("Informations à propos du Service");
        btnUpdateService.setVisible(false);
        btnSaveService.setDisable(true);
        btnSaveService.toFront();
        disableEditControls();
        selectedRecord();

    }

    private void selectedRecord() {
        Service Service = tblService.getSelectionModel().getSelectedItem();
        txtServiceName.setText(Service.getServiceName());
    }

    @FXML
    private void loadData() {
        loadTable();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServiceName.setCellValueFactory(new PropertyValueFactory<>("ServiceName"));
    }

    private void loadTable() {
        ArrayList<Service> list = new ArrayList<>();
        try {
            String sql = "SELECT id, ServiceName FROM Service";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ServiceName = resultSet.getString("ServiceName");
                list.add(new Service(id, ServiceName));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            AlertsBuilder.create(AlertType.ERROR, stckService, rootService, tblService, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
        listService = FXCollections.observableArrayList(list);
        tblService.setItems(listService);
    }

    @FXML
    private void newService() {
        String name = txtServiceName.getText().trim();

        if (name.isEmpty()) {
            txtServiceName.requestFocus();
            Animations.shake(txtServiceName);
            return;
        }

        Service Service = new Service();
        Service.setServiceName(name);

        boolean result = DatabaseHelper.insertNewService(Service, listService);
        if (result) {
            loadData();
            cleanControls();
            closeDialogAddService();
            AlertsBuilder.create(AlertType.SUCCES, stckService, rootService, tblService, Constants.MESSAGE_ADDED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }

    }

    @FXML
    private void deleteService() {
        boolean result = DatabaseHelper.deleteService(tblService, listService);
        if (result) {
            loadData();
            cleanControls();
            closeDialogDeleteService();
            AlertsBuilder.create(AlertType.SUCCES, stckService, rootService, tblService, Constants.MESSAGE_DELETED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    @FXML
    private void updateService() {
        String name = txtServiceName.getText().trim();

        if (name.isEmpty()) {
            txtServiceName.requestFocus();
            Animations.shake(txtServiceName);
            return;
        }

        Service Service = tblService.getSelectionModel().getSelectedItem();
        Service.setId(Service.getId());
        Service.setServiceName(name);

        boolean result = DatabaseHelper.updateService(Service);
        if (result) {
            loadData();
            cleanControls();
            closeDialogAddService();
            AlertsBuilder.create(AlertType.SUCCES, stckService, rootService, tblService, Constants.MESSAGE_UPDATED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    private void cleanControls() {
        txtServiceName.clear();
    }

    private void disableEditControls() {
        txtServiceName.setEditable(false);
    }

    private void enableEditControls() {
        txtServiceName.setEditable(true);
    }

    private void resetValidation() {
        txtServiceName.resetValidation();
    }

    private void disableTable() {
        tblService.setDisable(true);
    }

    private boolean validateEmailAddress(String email) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }

    private void closeDialogWithEscapeKey() {
        rootService.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogDeleteService();
            }

            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddService();
            }

            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                tblService.setDisable(false);
                rootService.setEffect(null);
                AlertsBuilder.close();
            }

        });
    }

    private void closeDialogWithTextFields() {
        txtServiceName.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddService();
            }
        });
    }

    private void deleteUserDeleteKey() {
        rootService.setOnKeyPressed(ev -> {
            if (ev.getCode().equals(KeyCode.DELETE)) {
                if (tblService.isDisable()) {
                    return;
                }

                if (tblService.getSelectionModel().getSelectedItems().isEmpty()) {
                    AlertsBuilder.create(AlertType.ERROR, stckService, rootService, tblService, Constants.MESSAGE_NO_RECORD_SELECTED);
                    return;
                }
                deleteService();
            }
        });
    }

    @FXML
    private void filterNameService() {
        String filterName = txtSearchService.getText().trim();
        if (filterName.isEmpty()) {
            tblService.setItems(listService);
        } else {
            filterService.clear();
            for (Service c : listService) {
                if (c.getServiceName().toLowerCase().contains(filterName.toLowerCase())) {
                    filterService.add(c);
                }
            }
            tblService.setItems(filterService);
        }
    }
}
