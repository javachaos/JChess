package com.github.javachaos.jchess.gamelogic;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.util.Optional;

/**
 * Alerts class.
 */
public final class Alerts {

    /**
     * Unused.
     */
    private Alerts() {
    } //Unused

    /**
     * Get the user password.
     * @param prompt the password prompt show the user
     * @return the user password
     */
    public static char[] passwordPrompt(final String prompt) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password");
        dialog.setHeaderText(prompt);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);
        PasswordField pwd = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(new Label("Password:"), pwd);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> pwd.getText());
        Optional<String> result = dialog.showAndWait();
        return result.map(String::toCharArray).orElse(null);
    }

    /**
     * Show an info Alert.
     * @param s the info to display on the alert
     */
    public static void info(final String s) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(s);
        a.show();
    }

    /**
     * Show an info Alert.
     * @param s the info to display on the alert
     */
    public static void err(final String s) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(s);
        a.show();
    }

}
