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
package com.structapp.eagle.resources;

import javafx.scene.effect.BoxBlur;

public class Constants {

    public static final String TITLE = "eagle app";
    public static final Double MIN_WIDTH = 1040.00;
    public static final Double MIN_HEIGHT = 640.00;

    public static final String SOURCE_PACKAGES = "/com/structapp/eagle";
    public static final String MEDIA_PACKAGE = "/resources/media/";
    public static final String VIEWS_PACKAGE = SOURCE_PACKAGES + "/view/";
    public static final String PROFILE_PICTURES_PACKAGE = MEDIA_PACKAGE + "profiles/";
    public static final String FONTS_PACKAGE = SOURCE_PACKAGES + "/fonts/";

    public static final String LOGIN_VIEW = VIEWS_PACKAGE + "LoginView.fxml";
    public static final String START_VIEW = VIEWS_PACKAGE + "StartView.fxml";
    public static final String MAIN_VIEW = VIEWS_PACKAGE + "MainView.fxml";

    public static final String STAGE_ICON = MEDIA_PACKAGE + "icon.png";
    public static final String NO_IMAGE_AVAILABLE = MEDIA_PACKAGE + "empty-image.jpg";
    public static final String INFORMATION_IMAGE = MEDIA_PACKAGE + "information.png";
    public static final String ERROR_IMAGE = MEDIA_PACKAGE + "error.png";
    public static final String SUCCESS_IMAGE = MEDIA_PACKAGE + "success.png";

    public static final String CSS_LIGHT_THEME = "/resources/LightTheme.css";
    public static final String LIGHT_THEME = Constants.class.getResource(CSS_LIGHT_THEME).toExternalForm();
    
    public static final String MESSAGE_ERROR_CONNECTION_MYSQL = "Une erreur s'est produite lors de la connexion à la base de donnée.\nVérifiez votre connexion";
    public static final String MESSAGE_IMAGE_LARGE = "Veuillez télécharger une image inférieure à 1 Mo.";
    public static final String MESSAGE_IMAGE_NOT_FOUND = "Image non trouvée. L'enregistrement sera sauvegardé.";
    public static final String MESSAGE_INSUFFICIENT_DATA = "Données insuffisantes";
    public static final String MESSAGE_USER_ALREADY_EXISTS = "Cet utilisateur existe déjà.";
    public static final String MESSAGE_PASSWORDS_NOT_MATCH = "Les mots de passe ne correspondent pas.";
    public static final String MESSAGE_ENTER_AT_LEAST_4_CHARACTERES = "Veuillez entrer au moins 4 caractères";
    public static final String MESSAGE_NO_RECORD_SELECTED = "Sélectionnez un élément dans le tableau.";

    public static final String MESSAGE_ADDED = "Enregistrement ajouté avec succès";
    public static final String MESSAGE_UPDATED = "Enregistrement modifié avec succès";
    public static final String MESSAGE_DELETED = "Enregistrement supprimé avec succès";

    public static final String EXISTENT = "Existe";
    public static final String NOT_EXISTENT = "N'existe pas";

    public static final String REALIZED = "Realisé";
    public static final String NOT_REALIZED = "Non realisé";

    public static final String REPORTED = "Signalée";
    public static final String NOT_REPORTED = "Non Signalée";

    public static final BoxBlur BOX_BLUR_EFFECT = new BoxBlur(3, 3, 3);
}
