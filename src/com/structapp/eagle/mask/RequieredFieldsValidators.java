package com.structapp.eagle.mask;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class RequieredFieldsValidators {

    private static final FontAwesomeIcon WARNING_ICON = FontAwesomeIcon.EXCLAMATION_TRIANGLE;
    
    private static final String MESSAGE = "Champs obligatoire";

    public static void toJFXTextField(JFXTextField txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXPasswordField(JFXPasswordField txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXTextArea(JFXTextArea txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXComboBox(JFXComboBox comboBox) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));
        
        comboBox.getValidators().add(validator);
        comboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                comboBox.validate();
            }
        });
    }

    public static void toJFXDatePicker(JFXDatePicker datePicker) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));
        
        datePicker.getValidators().add(validator);
        datePicker.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                datePicker.validate();
            }
        });
    }
}
