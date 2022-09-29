package com.structapp.eagle.controllers;

import com.jfoenix.controls.*;
import com.structapp.eagle.alerts.AlertType;
import com.structapp.eagle.alerts.AlertsBuilder;
import com.structapp.eagle.animations.Animations;
import com.structapp.eagle.database.DatabaseConnection;
import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.mask.RequieredFieldsValidators;
import com.structapp.eagle.mask.TextFieldMask;
import com.structapp.eagle.models.Bureau;
import com.structapp.eagle.models.Document;
import com.structapp.eagle.models.Service;
import com.structapp.eagle.notifications.NotificationType;
import com.structapp.eagle.notifications.NotificationsBuilder;
import com.structapp.eagle.preferences.Preferences;
import com.structapp.eagle.resources.Constants;
import com.structapp.eagle.util.ContextMenu;
import com.structapp.eagle.util.JFXDialogTool;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DocsController implements Initializable {

    private final ColorAdjust colorAdjust = new ColorAdjust();
    private final long LIMIT = 1000000;
    private final String ALREADY_EXISTS = "There is already a product with this barcode";
    private final String IS_GREATER = "Minimum price cannot be higher than sale price";
    private ObservableList<Document> listDocuments;
    private ObservableList<Document> filterDocuments;
    @FXML
    private StackPane stckDocuments;
    @FXML
    private AnchorPane rootDocuments;
    @FXML
    private AnchorPane containerDeleteDocuments;
    @FXML
    private HBox hBoxSearch;
    @FXML
    private TextField txtSearchDocuments;
    @FXML
    private TextField txtSearchBarCode;
    @FXML
    private JFXButton btnNewDocuments;
    @FXML
    private TableView<Document> tblDocuments;
    @FXML
    private TableColumn<Document, Integer> colId;
    @FXML
    private TableColumn<Document, Integer> colBarcode;
    @FXML
    private TableColumn<Document, String> colName;
    @FXML
    private TableColumn<Document, String> colDescription;
    @FXML
    private TableColumn<Document, Double> colBureau;
    @FXML
    private TableColumn<Document, Double> colService;
    @FXML
    private TableColumn<Document, Integer> colInsertion;
    @FXML
    private  TableColumn<Document, Integer> colTelecharger;
    @FXML
    private TableColumn<Document, Double> colExpiration;
    @FXML
    private AnchorPane containerAddDocuments;
    @FXML
    private JFXTextField txtBarCode;
    @FXML
    private JFXTextField txtNameDocuments;
    @FXML
    private JFXComboBox<String> cmbTypeBureau;
    @FXML
    private JFXComboBox<String> cmbTypeService;
    @FXML
    private JFXComboBox<Integer> cmbTypeDays;
    @FXML
    private Text textAddDocuments;
    @FXML
    private JFXButton btnUpdateDocuments;
    @FXML
    private JFXButton btnSaveDocuments;
    @FXML
    private JFXButton btnCancelAddDocuments;
    @FXML
    private JFXDatePicker txtInsertionDate;
    @FXML
    private JFXDatePicker txtExpirationDate;
    @FXML
    private ImageView imageDocuments;
    @FXML
    private Pane paneContainer;
    @FXML
    private HBox fileContainer;
    private JFXDialogTool dialogAddDocuments;
    private JFXDialogTool dialogDeleteDocuments;
    private static final Stage stage = new Stage();
    private File pdfFile;
    private ContextMenu contextMenu;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        animateNodes();
        validateUser();
        characterLimiter();
        initializeImage();
        setTextIfFieldIsEmpty();
        closeDialogWithTextFields();
        closeDialogWithEscapeKey();
        initalizeComboBox();
    }


    private void setContextMenu() {
        contextMenu = new ContextMenu(tblDocuments);

        contextMenu.setActionEdit(ev -> {
            showDialogEditDocuments();
            contextMenu.hide();
        });

        contextMenu.setActionDelete(ev -> {
            showDialogDeleteDocuments();
            contextMenu.hide();
        });

        contextMenu.setActionDetails(ev -> {
            showDialogDetailsDocuments();
            contextMenu.hide();
        });

        contextMenu.setActionRefresh(ev -> {
            loadData();
            contextMenu.hide();
        });

        contextMenu.show();
    }

    private void initializeImage() {
        fileContainer.hoverProperty().addListener((o, oldV, newV) -> {
            if (newV) {
                colorAdjust.setBrightness(0.25);
                imageDocuments.setEffect(colorAdjust);
            } else {
                imageDocuments.setEffect(null);
            }
        });

        fileContainer.setPadding(new Insets(5));
        filterDocuments = FXCollections.observableArrayList();
        imageDocuments.setFitHeight(fileContainer.getPrefHeight() - 10);
        imageDocuments.setFitWidth(fileContainer.getPrefWidth() - 10);
    }

    private void animateNodes() {
        Animations.fadeInUp(btnNewDocuments);
        Animations.fadeInUp(btnNewDocuments);
        Animations.fadeInUp(tblDocuments);
        Animations.fadeInUp(hBoxSearch);
    }

    private void setValidations() {
        RequieredFieldsValidators.toJFXComboBox(cmbTypeService);
        RequieredFieldsValidators.toJFXComboBox(cmbTypeBureau);
        RequieredFieldsValidators.toJFXDatePicker(txtInsertionDate);
        RequieredFieldsValidators.toJFXTextField(txtBarCode);
        RequieredFieldsValidators.toJFXDatePicker(txtExpirationDate);
        RequieredFieldsValidators.toJFXTextField(txtNameDocuments);
    }

    private void setTextIfFieldIsEmpty() {
        TextFieldMask.setTextIfFieldIsEmpty(txtBarCode);
        TextFieldMask.setTextIfFieldIsEmpty(txtNameDocuments);
    }

    private void characterLimiter() {
        TextFieldMask.characterLimit(txtBarCode, 20);
    }

    @FXML
    private void showDialogAddDocuments() {
        disableTable();
        resetValidations();
        enableEditControls();
        rootDocuments.setEffect(Constants.BOX_BLUR_EFFECT);

        textAddDocuments.setText("Ajouter un document");
        fileContainer.toFront();
        btnSaveDocuments.toFront();
        btnUpdateDocuments.setVisible(true);
        btnSaveDocuments.setDisable(false);
        containerAddDocuments.setVisible(true);

        dialogAddDocuments = new JFXDialogTool(containerAddDocuments, stckDocuments);
        dialogAddDocuments.show();

        dialogAddDocuments.setOnDialogOpened(ev -> {
            txtBarCode.requestFocus();
        });

        dialogAddDocuments.setOnDialogClosed(ev -> {
            closeStage();
            tblDocuments.setDisable(false);
            rootDocuments.setEffect(null);
            containerAddDocuments.setVisible(false);
            cleanControls();
        });
    }

    @FXML
    private void closeDialogAddDocuments() {
        if (dialogAddDocuments != null) {
            dialogAddDocuments.close();
        }
    }

    @FXML
    private void showDialogDeleteDocuments() {
        if (tblDocuments.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        rootDocuments.setEffect(Constants.BOX_BLUR_EFFECT);
        containerDeleteDocuments.setVisible(true);
        disableTable();

        dialogDeleteDocuments = new JFXDialogTool(containerDeleteDocuments, stckDocuments);
        dialogDeleteDocuments.show();

        dialogDeleteDocuments.setOnDialogClosed(ev -> {
            tblDocuments.setDisable(false);
            rootDocuments.setEffect(null);
            containerDeleteDocuments.setVisible(false);
            cleanControls();
        });

    }

    @FXML
    private void hideDialogDeleteDocuments() {
        if (dialogDeleteDocuments != null) {
            dialogDeleteDocuments.close();
        }
    }

    @FXML
    private void showDialogEditDocuments() {
        if (tblDocuments.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        showDialogAddDocuments();
        btnUpdateDocuments.toFront();
        textAddDocuments.setText("Modifier le document");
        selectedRecord();

    }

    @FXML
    private void showDialogDetailsDocuments() {
        if (tblDocuments.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        showDialogAddDocuments();
        textAddDocuments.setText("Documents details");
        selectedRecord();
        paneContainer.toFront();
        btnUpdateDocuments.setVisible(false);
        btnSaveDocuments.setDisable(true);
        btnSaveDocuments.toFront();
        disableEditControls();
    }

    @FXML
    private void loadData() {
        loadTable();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        colBureau.setCellValueFactory(new PropertyValueFactory<>("bureauName"));
        colService.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        colInsertion.setCellValueFactory(new PropertyValueFactory<>("insertionDate"));
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        colTelecharger.setCellValueFactory(new PropertyValueFactory<>("action"));
    }

    private void loadTable() {
        ArrayList<Document> list = new ArrayList<>();
        try {
            String sql = "SELECT id, barcode, documentName, insertionDate, documentFile ,expirationDate, bureauID, serviceID FROM Document";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String barcode = resultSet.getString("barcode");
                String documentName = resultSet.getString("documentName");
                java.sql.Date insertionDate = resultSet.getDate("insertionDate");
                java.sql.Date expirationDate = resultSet.getDate("expirationDate");
                int bureauID = resultSet.getInt("bureauID");
                int serviceID = resultSet.getInt("serviceID");
                String bureauName = "test";
                String serviceName = "";

                for (Bureau elm : laodComboboxBureau()){
                    if (elm.getId()==bureauID){
                        bureauName = elm.getBureauName();}
                }

                for (Service elm : laodComboboxService()){
                    if (elm.getId()==serviceID){
                        serviceName = elm.getServiceName();}
                }


                JFXButton action = new JFXButton("Save");
                InputStream documentFile = resultSet.getBinaryStream("documentFile");
                action.setStyle("-fx-background-color: #38d77d; -fx-color-label-visible: #f2f6f1;");
                action.addEventHandler(MouseEvent.MOUSE_CLICKED, (e6) -> {
                    onSaveButton(id, documentName+".pdf", documentFile);

                });
                list.add(new Document(id, barcode, documentName, documentFile, insertionDate, expirationDate, bureauName, serviceName, action));
            }
        } catch (SQLException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            AlertsBuilder.create(AlertType.ERROR, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }

        listDocuments = FXCollections.observableArrayList(list);
        tblDocuments.setItems(listDocuments);
        tblDocuments.setFixedCellSize(30);
        tblDocuments.setStyle("fx-alignment: RIGHT;");
    }

    private void onSaveButton(int id, String documentName, InputStream documentFile){
        File file = new File(documentName);
        try{
            FileOutputStream output = new FileOutputStream(file);
            System.out.println("Writing to file " + file.getAbsolutePath());
            byte[] buffer = new byte[1024];
            while (documentFile.read(buffer) > 0) {
                output.write(buffer);
                System.out.println(output);
                System.out.println(buffer);
            }
        }catch (Exception e){

        }

    }

    private void selectedRecord() {
        Document documents = tblDocuments.getSelectionModel().getSelectedItem();
        txtBarCode.setText(String.valueOf(documents.getBarcode()));
        txtNameDocuments.setText(documents.getDocumentName());
        //txtService.setText(String.valueOf(documents.getServiceID()));
        //cmbTypeBureau.setItems(documents.getDocumentName());
        expandImage(documents.getId(), documents.getDocumentName());
    }

    private InputStream getInputStream() {
        InputStream is;
        try {
            if (pdfFile != null) {
                is = new FileInputStream(pdfFile);
            } else {
                is = DocsController.class.getResourceAsStream(Constants.NO_IMAGE_AVAILABLE);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocsController.class.getName()).log(Level.SEVERE, null, ex);
            NotificationsBuilder.create(NotificationType.INFORMATION, Constants.MESSAGE_IMAGE_NOT_FOUND);
            is = DocsController.class.getResourceAsStream(Constants.NO_IMAGE_AVAILABLE);
        }
        return is;
    }

    @FXML
    private void updateProduct() {
        String barcode = txtBarCode.getText().trim();
        String documentName = txtNameDocuments.getText().trim();
        String bureauName = cmbTypeBureau.getValue().trim();
        String serviceName= cmbTypeService.getValue().trim();
        int nombreDays = cmbTypeDays.getValue();
        Date insertionDate = Date.valueOf(txtInsertionDate.getValue());
        Date expirationDate = Date.valueOf(txtExpirationDate.getValue());
        String barcodeFromTable = tblDocuments.getSelectionModel().getSelectedItem().getBarcode();

        if (barcode.isEmpty()) {
            txtBarCode.requestFocus();
            Animations.shake(txtBarCode);
            return;
        }

        if (DatabaseHelper.checkIfDocumentExists(barcode) != 0 && !barcodeFromTable.equals(barcode)) {
            txtBarCode.requestFocus();
            Animations.shake(txtBarCode);
            NotificationsBuilder.create(NotificationType.ERROR, ALREADY_EXISTS);
            return;
        }

        if (documentName.isEmpty()) {
            txtNameDocuments.requestFocus();
            Animations.shake(txtNameDocuments);
            return;
        }


        int bureauID = 0;
        int serviceID = 0;

        for (Service elm : laodComboboxService() ){
            if(elm.getServiceName().toLowerCase().trim().equals(serviceName.toLowerCase().trim())){
                serviceID = elm.getId();
            }
        }

        for (Bureau elm : laodComboboxBureau()){
            if(elm.getBureauName().toLowerCase().trim().equals(bureauName.toLowerCase().trim())){
                bureauID = elm.getId();
            }
        }


        Document documents = tblDocuments.getSelectionModel().getSelectedItem();
        documents.setId(documents.getId());
        documents.setBarcode(barcode);
        documents.setDocumentName(documentName);
        documents.setBureauID(bureauID);
        documents.setServiceID(serviceID);
        documents.setNombreDays(nombreDays);
        //documents.setDocumentFile((getInputStream()));
        documents.setInsertionDate(insertionDate);
        documents.setExpirationDate(expirationDate);

        if (pdfFile != null) {
            boolean result = DatabaseHelper.updateDocument(documents);
            if (result) {
                closeDialogAddDocuments();
                loadData();
                cleanControls();
                AlertsBuilder.create(AlertType.SUCCES, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_UPDATED);
            } else {
                NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
            }
        } else {
            boolean result = DatabaseHelper.updateDocumentIfFileIsNull(documents);
            if (result) {
                closeDialogAddDocuments();
                loadData();
                cleanControls();
                AlertsBuilder.create(AlertType.SUCCES, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_UPDATED);
            } else {
                NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
            }
        }
    }

    @FXML
    private void deleteDocuments() {
        boolean result = DatabaseHelper.deleteDocument(tblDocuments, listDocuments);
        if (result) {
            loadData();
            cleanControls();
            hideDialogDeleteDocuments();
            AlertsBuilder.create(AlertType.SUCCES, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_DELETED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }

    private void cleanControls() {

        txtBarCode.clear();
        txtNameDocuments.clear();
        imageDocuments.setImage(new Image(Constants.NO_IMAGE_AVAILABLE));
    }

    private void disableEditControls() {
        txtBarCode.setEditable(false);
        txtNameDocuments.setEditable(false);
        cmbTypeBureau.setEditable(false);
        cmbTypeService.setEditable(false);
        txtInsertionDate.setEditable(false);
        txtExpirationDate.setEditable(false);
    }

    private void enableEditControls() {
        txtBarCode.setEditable(true);
        txtNameDocuments.setEditable(true);
        cmbTypeBureau.setEditable(true);
        cmbTypeService.setEditable(true);
        txtInsertionDate.setEditable(true);
        txtExpirationDate.setEditable(true);
    }

    private void disableTable() {
        tblDocuments.setDisable(true);
    }

    private void resetValidations() {
        txtBarCode.resetValidation();
        txtNameDocuments.resetValidation();
        cmbTypeBureau.resetValidation();
        cmbTypeService.resetValidation();
        txtInsertionDate.resetValidation();
        txtExpirationDate.resetValidation();
    }

    private void validateUser() {
        setContextMenu();
        if (DatabaseHelper.getUserType().equals("Administrator")) {
            deleteUserDeleteKey();

            colExpiration.setVisible(true);
            colInsertion.setVisible(true);
            btnNewDocuments.setDisable(false);
            txtExpirationDate.setVisible(true);
            txtInsertionDate.setVisible(true);
            setEnableMenuItem();
        } else {
            colExpiration.setVisible(false);
            colInsertion.setVisible(false);
            btnNewDocuments.setDisable(true);
           // txtExpirationDate.setVisible(false);
            // txtInsertionDate.setVisible(false);
            setDisableMenuItem();
        }
    }

    private void setDisableMenuItem() {
        contextMenu.getEditButton().setDisable(true);
        contextMenu.getDeleteButton().setDisable(true);
    }

    private void setEnableMenuItem() {
        contextMenu.getEditButton().setDisable(false);
        contextMenu.getDeleteButton().setDisable(false);
    }

    private void closeDialogWithEscapeKey() {
        rootDocuments.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
            }

            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                hideDialogDeleteDocuments();
            }

            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                tblDocuments.setDisable(false);
                rootDocuments.setEffect(null);
                AlertsBuilder.close();
            }

        });
    }

    private void closeDialogWithTextFields() {
        txtBarCode.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
            }
        });

        txtNameDocuments.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
            }
        });


        cmbTypeService.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
            }
        });

        cmbTypeBureau.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
            }
        });

        txtExpirationDate.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
                tblDocuments.setDisable(false);
            }
        });

        txtInsertionDate.setOnKeyReleased(ev -> {
            if (ev.getCode().equals(KeyCode.ESCAPE)) {
                closeDialogAddDocuments();
                tblDocuments.setDisable(false);
            }
        });
    }

    public static void closeStage() {
        if (stage != null) {
            stage.hide();
        }
    }

    private void deleteUserDeleteKey() {
        rootDocuments.setOnKeyPressed(ev -> {
            if (ev.getCode().equals(KeyCode.DELETE)) {
                if (tblDocuments.isDisable()) {
                    return;
                }

                if (tblDocuments.getSelectionModel().getSelectedItems().isEmpty()) {
                    AlertsBuilder.create(AlertType.ERROR, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_NO_RECORD_SELECTED);
                    return;
                }

                deleteDocuments();
            }
        });
    }


    @FXML
    private void filterNameProduct() {
        String filterName = txtSearchDocuments.getText().trim();
        if (filterName.isEmpty()) {
            tblDocuments.setItems(listDocuments);
        } else {
            filterDocuments.clear();
            for (Document p : listDocuments) {
                if (p.getDocumentName().toLowerCase().contains(filterName.toLowerCase())) {
                    filterDocuments.add(p);
                }
            }
            tblDocuments.setItems(filterDocuments);
        }
    }

    @FXML
    private void filterCodeBar() {
        String filterCodeBar = txtSearchBarCode.getText().trim();
        if (filterCodeBar.isEmpty()) {
            tblDocuments.setItems(listDocuments);
        } else {
            filterDocuments.clear();
            for (Document p : listDocuments) {
                if (p.getBarcode().toLowerCase().contains(filterCodeBar.toLowerCase())) {
                    filterDocuments.add(p);
                }
            }
            tblDocuments.setItems(filterDocuments);
        }
    }

    @FXML
    private void showFileChooser() {
        pdfFile = getPdfFromFileChooser(getStage());
        if (pdfFile != null) {
            File pdf = new File(pdfFile.toURI().toString());
            setInitialDirectory();
        }
    }

    private Stage getStage() {
        return (Stage) btnCancelAddDocuments.getScene().getWindow();
    }

    private void setInitialDirectory() {
        Preferences preferences = Preferences.getPreferences();
        preferences.setInitialPathFileChooserProductsController(pdfFile.getParent());
        Preferences.writePreferencesToFile(preferences);
    }

    private File getPdfFromFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterPdf = new FileChooser.ExtensionFilter("pdf Files", "*.pdf", ".docx",".docx",".xlsx",".pptx");
        fileChooser.getExtensionFilters().addAll(extFilterPdf );
        fileChooser.setInitialDirectory(getInitialDirectoy());
        fileChooser.setTitle("Selectionner un fichier");

        File selectedFile = fileChooser.showOpenDialog(stage);
        return selectedFile;
    }

    private File getInitialDirectoy() {
        Preferences preferences = Preferences.getPreferences();
        File initPath = new File(preferences.getInitialPathFileChooserProductsController());
        if (!initPath.exists()) {
            preferences.setInitialPathFileChooserProductsController(System.getProperty("user.home"));
            Preferences.writePreferencesToFile(preferences);
            initPath = new File(preferences.getInitialPathFileChooserProductsController());
        }
        return initPath;
    }

    private void expandImage(int id, String title) {
        paneContainer.hoverProperty().addListener((o, oldV, newV) -> {
            if (newV) {
                colorAdjust.setBrightness(0.25);
                imageDocuments.setEffect(colorAdjust);
            } else {
                imageDocuments.setEffect(null);
            }
        });

        paneContainer.setOnMouseClicked(ev -> {
            final Image image = DatabaseHelper.getDocumentFile(id);
            final ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(550);

            final BorderPane boderPane = new BorderPane(imageView);
            boderPane.setStyle("-fx-background-color: white");
            boderPane.setCenter(imageView);

            final ScrollPane root = new ScrollPane(boderPane);
            root.setStyle("-fx-background-color: white");
            root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            root.getStylesheets().add(Constants.LIGHT_THEME);
            root.getStyleClass().add("scroll-bar");

            root.setFitToHeight(true);
            root.setFitToWidth(true);

            stage.getIcons().add(new Image(Constants.STAGE_ICON));
            stage.setScene(new Scene(root, 550, 555));
            stage.setTitle(title);
            stage.show();
        });
    }


    private void initalizeComboBox() {

        ArrayList<String> listeBureauNames = new ArrayList<>();
        ArrayList<String> listeServiceNames = new ArrayList<>();

        for (Bureau elm : laodComboboxBureau()){
            listeBureauNames.add(elm.getBureauName());
        }

        for (Service elm : laodComboboxService()){
            listeServiceNames.add(elm.getServiceName());
        }


        ObservableList<String> listeBureau = FXCollections.observableArrayList(listeBureauNames);
        ObservableList<String> listeService = FXCollections.observableArrayList(listeServiceNames);
        ObservableList<Integer> listeDays = FXCollections.observableArrayList(loadComboboxDays());

        cmbTypeBureau.setItems(listeBureau);
        cmbTypeService.setItems(listeService);
        cmbTypeDays.setItems(listeDays);
    }

    private ArrayList<Bureau> laodComboboxBureau() {
        ArrayList<Bureau> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM bureau";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String bureauName = resultSet.getString("bureauName");
                int id = resultSet.getInt("id");
                list.add(new Bureau(id,bureauName));
            }
        } catch (SQLException ex) {
            System.out.println("yes");
        }
        return list;
    }

    private ArrayList<Service> laodComboboxService() {
        ArrayList<Service> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM service";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String serviceName = resultSet.getString("serviceName");
                int id = resultSet.getInt("id");
                list.add(new Service(id,serviceName));
            }
        } catch (SQLException ex) {
            System.out.println("yes");
        }
        return list;
    }

    private ArrayList<Integer> loadComboboxDays(){
        ArrayList<Integer> days = new ArrayList<>();
        for (int i=1; i<366; i++){
            days.add(i);
        }
        return days;

    }

    @FXML
    private void newDocument() {
        String bureauName = cmbTypeBureau.getValue().trim();
        String serviceName = cmbTypeService.getValue().trim();
        int bureauID = 0;
        int serviceID = 0;

        for (Service elm : laodComboboxService() ){
            if(elm.getServiceName().trim().equals(serviceName)){
                serviceID = elm.getId();
            }
        }

        for (Bureau elm : laodComboboxBureau()){
            if(elm.getBureauName().trim().equals(bureauName)){
                bureauID = elm.getId();
            }
        }

        String barcode = txtBarCode.getText().trim();
        String documentName = txtNameDocuments.getText().trim();
        //Date insertionDate = Date.valueOf(txtInsertionDate.getValue());
        Date expirationDate = Date.valueOf(txtExpirationDate.getValue().plusDays(1));
        int nombreDays = cmbTypeDays.getValue();


        if (barcode.isEmpty()) {
            txtBarCode.requestFocus();
            Animations.shake(txtBarCode);
            return;
        }
        if (DatabaseHelper.checkIfDocumentExists(barcode) != 0) {
            txtBarCode.requestFocus();
            NotificationsBuilder.create(NotificationType.ERROR, ALREADY_EXISTS);
            return;
        }
        if (documentName.isEmpty()) {
            txtNameDocuments.requestFocus();
            Animations.shake(txtNameDocuments);
            return;
        }
        if (barcode.isEmpty()) {
            txtBarCode.requestFocus();
            Animations.shake(txtBarCode);
            return;
        }
        if (pdfFile != null && pdfFile.length() > LIMIT) {
            Animations.shake(fileContainer);
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_IMAGE_LARGE);
            return;
        }

        Document documents = new Document();
        documents.setBarcode(barcode);
        documents.setDocumentName(documentName);
        documents.setBureauID(bureauID);
        documents.setServiceID(serviceID);
        //documents.setInsertionDate(insertionDate);
        documents.setExpirationDate(expirationDate);
        documents.setDocumentFile(getInputStream());
        documents.setNombreDays(nombreDays);

        boolean result = DatabaseHelper.insertNewDocument(documents, listDocuments);
        if (result) {
            loadData();
            cleanControls();
            closeDialogAddDocuments();
            AlertsBuilder.create(AlertType.SUCCES, stckDocuments, rootDocuments, tblDocuments, Constants.MESSAGE_ADDED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }



}