/*
 * Copyright 2020-2021 LaynezCode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.structapp.eagle.database;

import com.jfoenix.controls.JFXDialog;
import com.structapp.eagle.mailsNotification.Notifications;
import com.structapp.eagle.mailsNotification.SendMailHelper;
import com.structapp.eagle.models.Bureau;
import com.structapp.eagle.models.Document;
import com.structapp.eagle.models.Service;
import com.structapp.eagle.models.Users;
import com.structapp.eagle.resources.Constants;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHelper {

    public static ArrayList<String> getAllEmailUsers(){
        ArrayList<String> emails = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement0 = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement0.executeQuery();
            while (resultSet.next()){
                emails.add(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return emails;
    }

    public static boolean sendMAils(ArrayList<String> emails, String titre, String bureau, String service, String Date, int nbrDays, int id) throws MessagingException, IOException {
        SendMailHelper mail = new SendMailHelper();
        mail.setupServerProperties();
        mail.draftEmail(emails,titre,bureau,service,Date,nbrDays);
        mail.sendEmail();
        updateEmailNotification(id);
        return true;
    }

    public static boolean updateEmailNotification(int id) {
        try {
            String sql = "UPDATE notifications SET EmailSend = ? WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static boolean deleteNotification(int id, TableView<Notifications> tbl, ObservableList<Notifications> listNotification) {
        try {
            String sql = "DELETE FROM notifications WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    public static int checkIfNotificationExists(String titre) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM notifications WHERE titre = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, titre);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    public static boolean insertNewNotification(Notifications notif, ObservableList<Notifications> listNotifications) {

        try {
            if (checkIfNotificationExists(notif.getTitre())==0){
                String sql = "INSERT INTO notifications (titre, bureau, service, insertionDate, expirationDate, nombreDays, EmailSend) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, notif.getTitre());
                preparedStatement.setString(2, notif.getBureau());
                preparedStatement.setString(3, notif.getService());
                preparedStatement.setDate(4, notif.getInsertionDate());
                preparedStatement.setDate(5, notif.getExpirationDate());
                preparedStatement.setInt(6, notif.getNombreDays());
                if(notif.getEmailSend()==0){
                    sendMAils(getAllEmailUsers(),notif.getTitre(),notif.getBureau(),notif.getService(), notif.getExpirationDate().toString(),notif.getNombreDays(),notif.getId());
                    preparedStatement.setInt(7, 1);
                    listNotifications.add(notif);
                }
                preparedStatement.execute();
                return true;
            }
            else{
                System.out.println("Deja existe!!");
               /* if(notif.getEmailSend()==0){
                    updateEmailNotification(notif.getId());
                    sendMAils(getAllEmailUsers(),notif.getTitre(),notif.getBureau(),notif.getService(), notif.getExpirationDate().toString(),notif.getNombreDays(),notif.getId());
                }*/
                return true;
            }

        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

        public static ArrayList<Bureau> idBureauName (){
        ArrayList<Bureau> bureau = new ArrayList<>();
        String name = "";
        try {
            String sql = "SELECT id, bureauName FROM bureau";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String bureauName = resultSet.getString("bureauName");
                bureau.add(new Bureau(id, bureauName));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bureau;
    }

    public static ArrayList<Service> idServiceName (){
        ArrayList<Service> service = new ArrayList<>();
        String name = "";
        try {
            String sql = "SELECT id, serviceName FROM service";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String serviceName = resultSet.getString("serviceName");
                service.add(new Service(id, serviceName));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return service;
    }


    public static boolean insertNewBureau(Bureau bureau, ObservableList<Bureau> listBureau) {
        try {
            String sql = "INSERT INTO bureau (bureauName) VALUES (?)";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, bureau.getBureauName());
            preparedStatement.execute();
            listBureau.add(bureau);
            return true;
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean deleteBureau(TableView<Bureau> tbl, ObservableList<Bureau> listBureau) {
        try {
            String sql = "DELETE FROM bureau WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, tbl.getSelectionModel().getSelectedItem().getId());
            preparedStatement.execute();
            listBureau.remove(tbl.getSelectionModel().getSelectedIndex());
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean updateBureau(Bureau bureau) {
        try {
            String sql = "UPDATE bureau SET bureauName = ? WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, bureau.getBureauName());
            preparedStatement.setInt(2, bureau.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static Bureau searchBureau(String name) {
        Bureau bureau = null;
        try {
            String sql = "SELECT id, bureauName FROM bureau WHERE bureauName = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String bureauName = resultSet.getString("bureauName");
                bureau = new Bureau(id, bureauName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bureau;
    }

    public static boolean updateNotification(Notifications notification) {
        try {
            String sql = "UPDATE notifications SET EmailSend = ? WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, notification.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }





    public static boolean insertNewService(Service service, ObservableList<Service> listService) {
        try {
            String sql = "INSERT INTO service (ServiceName) VALUES (?)";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, service.getServiceName());
            preparedStatement.execute();
            listService.add(service);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean deleteService(TableView<Service> tbl, ObservableList<Service> listService) {
        try {
            String sql = "DELETE FROM service WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, tbl.getSelectionModel().getSelectedItem().getId());
            preparedStatement.execute();
            listService.remove(tbl.getSelectionModel().getSelectedIndex());
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean updateService(Service service) {
        try {
            String sql = "UPDATE service SET serviceName = ? WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, service.getServiceName());
            preparedStatement.setInt(2, service.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static Service searchService(String name) {
        Service service = null;
        try {
            String sql = "SELECT id, serviceName FROM service WHERE serviceName = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String serviceName = resultSet.getString("serviceName");
                service = new Service(id, serviceName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return service;
    }

















    public static boolean insertNewUser(Users users, ObservableList<Users> listUsers) {
        try {
            String sql = "INSERT INTO Users (nameUser, email, pass, userType, profileImage) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, users.getNameUser());
            preparedStatement.setString(2, users.getEmail());
            preparedStatement.setString(3, users.getPass());
            preparedStatement.setString(4, users.getUserType());
            preparedStatement.setBlob(5, users.getProfileImage());
            preparedStatement.execute();
            listUsers.add(users);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean insertNewUser(Users users) {
        try {
            String sql = "INSERT INTO Users (nameUser, email, pass, userType, profileImage) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, users.getNameUser());
            preparedStatement.setString(2, users.getEmail());
            preparedStatement.setString(3, users.getPass());
            preparedStatement.setString(4, users.getUserType());
            preparedStatement.setBlob(5, users.getProfileImage());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean deleteUser(TableView<Users> tbl, ObservableList<Users> listUsers) {
        try {
            String sql = "DELETE FROM Users WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, tbl.getSelectionModel().getSelectedItem().getId());
            preparedStatement.execute();
            listUsers.remove(tbl.getSelectionModel().getSelectedIndex());
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean updateUser(Users users) {
        try {
            String sql = "UPDATE Users SET nameUser = ?, email = ?, pass = ?, userType = ? WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, users.getNameUser());
            preparedStatement.setString(2, users.getEmail());
            preparedStatement.setString(3, users.getPass());
            preparedStatement.setString(4, users.getUserType());
            preparedStatement.setInt(5, users.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean updateUserFromSettings(Users users) {
        try {
            String sql = "UPDATE Users SET nameUser = ?, email = ?, pass = ?, biography = ?, dialogTransition = ? WHERE id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, users.getNameUser());
            preparedStatement.setString(2, users.getEmail());
            preparedStatement.setString(3, users.getPass());
            preparedStatement.setString(4, users.getBiography());
            preparedStatement.setString(5, users.getDialogTransition());
            preparedStatement.setInt(6, users.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static int checkIfUserExists(String username) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM Users WHERE email = BINARY ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    public static boolean insertUserSession(int id) {
        try {
            String sql = "INSERT INTO UserSession (userId) VALUES (?)";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static int checkIfUserExists() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM Users";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    private static String getDialogTransition() {
        String dialogTransition = null;
        try {
            String sql = "SELECT Users.dialogTransition FROM Users INNER JOIN UserSession ON UserSession.userId = Users.id WHERE UserSession.id = 1";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dialogTransition = resultSet.getString("dialogTransition");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            dialogTransition = "CENTER";
        }
        return dialogTransition;
    }

    public final static JFXDialog.DialogTransition dialogTransition() {
        return JFXDialog.DialogTransition.valueOf(DatabaseHelper.getDialogTransition());
    }

    public static String getUserType() {
        String userType = null;
        try {
            String sql = "SELECT Users.userType FROM Users INNER JOIN UserSession ON UserSession.userId = Users.id WHERE UserSession.id = 1";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userType = resultSet.getString("userType");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            userType = "User";
        }
        return userType;
    }

    public static int getSessionId() {
        int userId = 0;
        try {
            String sql = "SELECT userId FROM UserSession";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userId = resultSet.getInt("userId");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userId;
    }

    public static String getSessionUsername() {
        String user = null;
        try {
            String sql = "SELECT Users.email FROM Users INNER JOIN UserSession ON UserSession.userId = Users.id WHERE UserSession.id = 1";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = resultSet.getString("email");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }

    public static void logout() {
        try {
            String sql = "TRUNCATE TABLE UserSession";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static boolean insertNewDocument(Document document, ObservableList<Document> listDocument) {
            try {
                String sql = "INSERT INTO Document (barcode, documentName, expirationDate, bureauID, serviceID, documentFile, nombreDays) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, document.getBarcode());
                preparedStatement.setString(2, document.getDocumentName());
                preparedStatement.setDate(3, document.getExpirationDate());
                preparedStatement.setInt(4, document.getBureauID());
                preparedStatement.setInt(5, document.getServiceID());
                preparedStatement.setBlob(6, document.getDocumentFile());
                preparedStatement.setInt(7, document.getNombreDays());
                System.out.println(document.getInsertionDate());
                System.out.println(document.getExpirationDate());
                preparedStatement.execute();
                listDocument.add(document);
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        return false;
        }

        public static boolean deleteDocument(TableView<Document> tbl, ObservableList<Document> listDocument) {
            try {
                String sql = "DELETE FROM Document WHERE id = ?";
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStatement.setInt(1, tbl.getSelectionModel().getSelectedItem().getId());
                preparedStatement.execute();
                listDocument.remove(tbl.getSelectionModel().getSelectedIndex());
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        public static boolean updateDocument(Document document) {
            try {
                String sql = "UPDATE document SET barcode = ?, documentName = ?, documentFile = ?, insertionDate = ?, expirationDate = ?, bureauID = ?, serviceID = ?, nombreDays = ? WHERE  id = ?";
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, document.getBarcode());
                preparedStatement.setString(2, document.getDocumentName());
                preparedStatement.setBlob(3, document.getDocumentFile());
                Date insertion = java.sql.Date.valueOf(document.getInsertionDate().toLocalDate().plusDays(1));
                Date expiration = java.sql.Date.valueOf(document.getExpirationDate().toLocalDate().plusDays(1));
                preparedStatement.setDate(4, insertion);
                preparedStatement.setDate(5,  expiration);
                preparedStatement.setInt(6, document.getBureauID());
                preparedStatement.setInt(7, document.getServiceID());
                preparedStatement.setInt(8, document.getNombreDays());
                preparedStatement.setInt(9, document.getId());
                preparedStatement.execute();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        public static int checkIfDocumentExists(String barcode) {
            int count = 0;
            try {
                String sql = "SELECT COUNT(*) FROM Document WHERE barcode = ?";
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, barcode);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return count;
        }

        public static Image getDocumentFile(int id) {
            Image image = null;
            try {
                String sql = "SELECT imageDocument FROM document WHERE id = ?";
                PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    InputStream img = rs.getBinaryStream("imageDocument");
                    if (img != null) {
                        image = new Image(img);
                    } else {
                        image = new Image(Constants.NO_IMAGE_AVAILABLE);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                image = new Image(Constants.NO_IMAGE_AVAILABLE);
            }
            return image;
        }

        public static boolean updateImageFromSettings(InputStream inputStream) {
            try {
                String sql = "UPDATE Users SET profileImage = ? WHERE id = ?";
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStatement.setBlob(1, inputStream);
                preparedStatement.setInt(2, getSessionId());
                preparedStatement.execute();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        public static int getDocument(java.sql.Date date) {
            int count = 0;
            try {
                String sql = "SELECT COUNT(*) FROM Document WHERE insertionDate = ?";
                PreparedStatement preparedStetementDocument = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
                preparedStetementDocument.setDate(1, new java.sql.Date(date.getTime()));
                ResultSet rs = preparedStetementDocument.executeQuery();
                while (rs.next()) {
                    count = rs.getInt(1);
                }
            } catch (SQLException ex) {
                count = 0;
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return count;
        }

    public static boolean updateDocumentIfFileIsNull(Document documents) {
        try {
            String sql = "UPDATE Document SET barcode = ?, documentName = ?, documentDescription = ?, "
                    + "bureauID = ?, serviceID = ?, insertionDate = ?, expirationDate = ?, nombreDays = ?  "
                    + "WHERE  id = ?";
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, documents.getBarcode());
            preparedStatement.setString(2, documents.getDocumentName());
            preparedStatement.setInt(3, documents.getBureauID());
            preparedStatement.setInt(4, documents.getServiceID());
            preparedStatement.setDate(5, documents.getInsertionDate());
            preparedStatement.setDate(6,  documents.getExpirationDate());
            preparedStatement.setInt(7, documents.getNombreDays());
            preparedStatement.setInt(8, documents.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }



}

