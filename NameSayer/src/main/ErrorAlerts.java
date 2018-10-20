package main;

import javafx.scene.control.Alert;

public class ErrorAlerts {
    //This method creates the appropriate alerts for the user for an unexpected input
    public void showError(String title, String content){

    Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle(title);
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(content);
            errorAlert.showAndWait();
    }

}