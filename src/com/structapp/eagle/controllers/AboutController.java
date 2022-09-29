package com.structapp.eagle.controllers;

import com.structapp.eagle.animations.Animations;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutController implements Initializable {

    private final String GITHUB = "https://github.com/MaryamRACHID";
    
    private final String FACEBOOK = "https://web.facebook.com/mary.rachiid/";
    
    private final String GMAIL = "https://www.google.com/";
    
    private final String LINKEDIN = "https://www.linkedin.com/in/maryam-rachid-0b78021b9/";

    @FXML
    private ImageView estfx;

    @FXML
    private Text developer;

    @FXML
    private ImageView laynezcode;

    @FXML
    private MaterialDesignIconView facebook;

    @FXML
    private MaterialDesignIconView linkedin;

    @FXML
    private MaterialDesignIconView github;

    @FXML
    private MaterialDesignIconView google;

    @FXML
    private Text mark;

    @FXML
    private ImageView laynezcorporation;

    @FXML
    private Separator separator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAnimations();
        setURL();
    }

    private void setURL() {
        url(GITHUB, github);
        url(FACEBOOK, facebook);
        url(GMAIL, google);
       // url(LINKEDIN, linkedin);
    }

    private void setAnimations() {
        transition(estfx, 0);
        transition(developer, 2);
        transition(laynezcode, 3);
        transition(separator, 4);
        transition(facebook, 5);
        //transition(linkedin, 6);
        transition(github, 7);
        transition(google, 8);
        transition(mark, 9);
        transition(laynezcorporation, 10);
    }

    private void transition(Node node, int duration) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), node);
        fadeTransition.setFromValue(2);
        fadeTransition.setToValue(0.5);

        PauseTransition pauseTransition = new PauseTransition();
        pauseTransition.setDuration(Duration.seconds(duration));
        pauseTransition.setOnFinished(ev -> {
            PauseTransition pauseTransition2 = new PauseTransition();
            pauseTransition2.setDuration(Duration.seconds(0.1));
            pauseTransition2.setOnFinished(ev2 -> {
                node.setVisible(true);
            });

            pauseTransition2.play();
            Animations.fadeInUp(node);
            fadeTransition.play();
        });

        pauseTransition.play();

        node.setOnMouseEntered(ev -> {
            fadeTransition.setToValue(1);
            fadeTransition.playFromStart();

            scaleTransition.setRate(1.0);
            scaleTransition.play();
        });

        node.setOnMouseExited(ev -> {
            fadeTransition.setDuration(Duration.millis(100));
            fadeTransition.setToValue(0.5);
            fadeTransition.playFromStart();

            scaleTransition.setRate(-1.0);
            scaleTransition.play();
        });
    }

    private void url(String url, Node node) {
        node.setOnMouseClicked(ev -> {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                Logger.getLogger(AboutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
