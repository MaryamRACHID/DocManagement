package com.structapp.eagle.controllers;


import com.jfoenix.controls.JFXButton;
import com.structapp.eagle.alerts.AlertType;
import com.structapp.eagle.alerts.AlertsBuilder;
import com.structapp.eagle.database.DatabaseConnection;
import com.structapp.eagle.database.DatabaseHelper;
import com.structapp.eagle.mailsNotification.Notifications;
import com.structapp.eagle.mailsNotification.SendMailHelper;
import com.structapp.eagle.models.Bureau;
import com.structapp.eagle.models.Document;
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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotifiController implements Initializable {


    private ObservableList<Notifications> listNotifications;
    @FXML
    private StackPane stckNotifications;
    @FXML
    private AnchorPane rootNotifications;
    @FXML
    private AnchorPane containerDeleteNotifications;
    @FXML
    private HBox hBoxSearch;
    @FXML
    private JFXButton deleteNotifications;
    @FXML
    private TableView<Notifications> tblNotifications;
    @FXML
    private TableColumn<Notifications, Integer> colId;
    @FXML
    private TableColumn<Notifications, Integer> colTitre;
    @FXML
    private TableColumn<Notifications, Integer> colBureau;
    @FXML
    private TableColumn<Notifications, Integer> colService;
    @FXML
    private TableColumn<Notifications, Integer> colinsertionDate;
    @FXML
    private TableColumn<Notifications, Integer> colexpirationDate;
    @FXML
    private TableColumn<Notifications, Integer> colAction;
    private ContextMenu contextMenu;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        notifTrigger();
    }

    public void addNotification(Document document) throws UnsupportedOperationException {
        Notifications notification = new Notifications();
        DateTimeFormatter dateForme = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime currentDate = LocalDateTime.now();
        ArrayList<Notifications> notifs = new ArrayList<>();
        try{
            LocalDate docExpirationDate = document.getExpirationDate().toLocalDate();
            LocalDate dateBeforeExpiration = docExpirationDate.minusDays(document.getNombreDays());
            LocalDate TwoDaysBeforeExpiration = docExpirationDate.minusDays(2);

            if ((dateForme.format(currentDate).compareTo(dateForme.format(dateBeforeExpiration)) >= 0
                    || dateForme.format(currentDate).compareTo(dateForme.format(TwoDaysBeforeExpiration)) == 0)
                    && (dateForme.format(currentDate).compareTo(dateForme.format(docExpirationDate)) < 0)) {
                System.out.println(currentDate);
                notification.setTitre(document.getDocumentName());
                notification.setBureau(document.getBureauName());
                notification.setService(document.getServiceName());
                notification.setInsertionDate(Date.valueOf(currentDate.toLocalDate()));
                notification.setExpirationDate(document.getExpirationDate());
                notification.setNombreDays(document.getNombreDays());
                notifs.add(notification);
            }
            for (Notifications elm : notifs){
                boolean result = DatabaseHelper.insertNewNotification(elm, listNotifications);
                if (result) {
                    loadData();
                    //AlertsBuilder.create(AlertType.SUCCES, stckNotifications, rootNotifications, tblNotifications, Constants.MESSAGE_ADDED);
                }
                else {
                    NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
                }
            }
                //sendMAils();

        }catch (UnsupportedOperationException e){
            System.out.println("Cast doesn't work");
        } //catch (MessagingException e) {
            //throw new RuntimeException(e);
        //} catch (IOException e) {
          //  throw new RuntimeException(e);
      //  }

    }

    public ArrayList<Document> getDateExpiration(){
        ArrayList<Document> list = new ArrayList<>();
        try {
            String sql = "SELECT id, barcode, documentName, insertionDate, documentFile ,expirationDate, bureauID, serviceID, nombreDays FROM document";
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
                String bureauName = "";
                String serviceName = "";
                int nombreDays = resultSet.getInt("nombreDays");

                for (Bureau elm : laodComboboxBureau()){
                    if (elm.getId()==bureauID){
                        bureauName = elm.getBureauName();}
                }

                for (Service elm : laodComboboxService()){
                    if (elm.getId()==serviceID){
                        serviceName = elm.getServiceName();}
                }

                list.add(new Document(id, barcode, documentName, insertionDate, expirationDate, bureauName, serviceName, nombreDays));
            }
        } catch (SQLException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
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


    public void notifTrigger(){

        for (Document doc : getDateExpiration()){
            addNotification(doc);
            System.out.println(doc.getDocumentName());
        }

    }

    @FXML
    private void loadData() {
        loadTable();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colBureau.setCellValueFactory(new PropertyValueFactory<>("bureau"));
        colService.setCellValueFactory(new PropertyValueFactory<>("service"));
        colinsertionDate.setCellValueFactory(new PropertyValueFactory<>("insertionDate"));
        colexpirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
    }

    private void loadTable() {
        ArrayList<Notifications> list = new ArrayList<>();
        try {
            String sql = "SELECT id, titre, bureau, service, insertionDate ,expirationDate FROM notifications";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titre = resultSet.getString("titre");
                String bureau= resultSet.getString("bureau");
                String service = resultSet.getString("service");
                java.sql.Date insertionDate = resultSet.getDate("insertionDate");
                java.sql.Date expirationDate = resultSet.getDate("expirationDate");
                JFXButton action = new JFXButton(" Désactiver ");
                action.setStyle("-fx-background-color: #ff6f6f; -fx-color-label-visible: #f2f6f1;");
                action.addEventHandler(MouseEvent.MOUSE_CLICKED, (e6) -> {
                    deleteNotifcation(id);
                    loadData();
                });
                list.add(new Notifications(id, titre, bureau, service, insertionDate, expirationDate, action));
            }
        } catch (SQLException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            AlertsBuilder.create(AlertType.ERROR, stckNotifications, rootNotifications, tblNotifications, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }

        listNotifications = FXCollections.observableArrayList(list);
        tblNotifications.setItems(listNotifications);
        tblNotifications.setFixedCellSize(30);
        tblNotifications.setStyle("fx-alignment: RIGHT;");
    }



    @FXML
    private void deleteNotifcation(int id) {
        boolean result = DatabaseHelper.deleteNotification(id, tblNotifications, listNotifications);
        if (result) {
            loadData();
            AlertsBuilder.create(AlertType.SUCCES, stckNotifications, rootNotifications, tblNotifications, Constants.MESSAGE_DELETED);
        } else {
            NotificationsBuilder.create(NotificationType.ERROR, Constants.MESSAGE_ERROR_CONNECTION_MYSQL);
        }
    }


}
