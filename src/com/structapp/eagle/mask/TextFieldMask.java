package com.structapp.eagle.mask;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TextFieldMask {

    public static void onlyDoubleNumbers5Integers(TextField txt) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,5}([\\.]\\d{0,2})?")) {
                txt.setText(oldValue);
            }
        });
    }

    public static void onlyDoubleNumbers10Integers(TextField txt) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,10}([\\.]\\d{0,2})?")) {
                txt.setText(oldValue);
            }
        });
    }

    public static void onlyNumbers(TextField txt) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txt.setText(oldValue);
            }
        });
    }

    public static void onlyLetters(TextField txt, int limit) {
        Pattern pattern = Pattern.compile("[a-zA-Z\\s]*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                int newLength = change.getControlNewText().length();
                if (newLength > limit) {
                    String trimmedText = change.getControlNewText().substring(0, limit);
                    change.setText(trimmedText);
                    int oldLength = change.getControlText().length();
                    change.setRange(0, oldLength);
                }
                return change;
            } else {
                return null;
            }
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txt.setTextFormatter(formatter);
    }

    public static void onlyEmail(TextField txt, int limit) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9+_.+@(.+)$\\d]*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                int newLength = change.getControlNewText().length();
                if (newLength > limit) {
                    String trimmedText = change.getControlNewText().substring(0, limit);
                    change.setText(trimmedText);
                    int oldLength = change.getControlText().length();
                    change.setRange(0, oldLength);
                }
                return change;
            } else {
                return null;
            }
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txt.setTextFormatter(formatter);
    }


    public static void onlyNumbersAndLettersNotSpaces(TextField txt, int limit) {

        Pattern pattern = Pattern.compile("[a-zA-Z\\d]*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                int newLength = change.getControlNewText().length();
                if (newLength > limit) {
                    String trimmedText = change.getControlNewText().substring(0, limit);
                    change.setText(trimmedText);
                    int oldLength = change.getControlText().length();
                    change.setRange(0, oldLength);
                }
                return change;
            } else {
                return null;
            }
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txt.setTextFormatter(formatter);
    }

    public static void characterLimit(TextField textField, int limit) {
        UnaryOperator<TextFormatter.Change> textLimitFilter = change -> {
            if (change.isContentChange()) {
                int newLength = change.getControlNewText().length();
                if (newLength > limit) {
                    String trimmedText = change.getControlNewText().substring(0, limit);
                    change.setText(trimmedText);
                    int oldLength = change.getControlText().length();
                    change.setRange(0, oldLength);
                }
            }
            return change;
        };
        textField.setTextFormatter(new TextFormatter(textLimitFilter));
    }

    public static void setTextIfFieldIsEmpty(TextField text) {
        text.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (text.getText().isEmpty() && newValue) {
                text.setText("0");
            }
        });
    }

    public static void selectText(TextField txt) {
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (!txt.getText().isEmpty() && newVal) {
                    txt.selectAll();
                }
            });
        });
    }

    public static void selectTextToJFXTextArea(TextArea txt) {
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (!txt.getText().isEmpty() && newVal) {
                    txt.selectAll();
                }
            });
        });
    }
}
