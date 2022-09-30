package com.structapp.eagle.controllers;

import com.structapp.eagle.animations.Animations;
import com.structapp.eagle.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController implements Initializable {

    @FXML
    private StackPane stckHome;

    @FXML
    private AnchorPane rootSearchMain;

    @FXML
    private AnchorPane rootHome;

    @FXML
    private TextField txtSearchRecentCustomer;

    @FXML
    private AnchorPane rootWelcome;

    @FXML
    private Text textDescriptionWelcome;

    @FXML
    private Text textWelcome;

    @FXML
    private Label labelTotalCustomers;

    @FXML
    private Label labelTotalQuotes;

    @FXML
    private Label labelNowQuotes;

    @FXML
    private Label labelTotalProduct;
    @FXML
    private AnchorPane rootTotalCustomers;

    @FXML
    private AnchorPane rootTotalQuotes;

    @FXML
    private AnchorPane rootRecentQuotes;

    @FXML
    private AnchorPane rootProducts;
    @FXML
    private Label Date;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        animationsNodes();
        counterRecords();
        setDate();
    }

    private void setDate(){
        LocalDateTime currentDate = LocalDateTime.now();
        String dateForme = "EEEEE dd MMMMM yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateForme, new Locale("fr", "MA"));
        String date = simpleDateFormat.format(new Date());

        Date.setText(date);
    }

    private void animationsNodes() {
        Animations.fadeInUp(rootSearchMain);
        Animations.fadeInUp(rootWelcome);
        Animations.fadeInUp(rootTotalCustomers);
        Animations.fadeInUp(rootProducts);
    }

    private void counterRecords() {
        try {
            String sql = "SELECT (SELECT COUNT(*) FROM document) AS Documents, (SELECT COUNT(*) FROM notifications) AS Notifications";
            PreparedStatement preparedStatetent = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet rs = preparedStatetent.executeQuery();

            while (rs.next()) {
                labelTotalCustomers.setText(String.valueOf(rs.getInt(1)));
                labelTotalProduct.setText(String.valueOf(rs.getInt(2)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



}
