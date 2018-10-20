package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import main.ErrorAlerts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpMenuController implements Initializable {

    @FXML
    private TextArea readmeArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/README.txt"));
            String line = null;

            while ((line = reader.readLine()) != null) {
                readmeArea.appendText(line + "\n");
            }

        } catch (FileNotFoundException e) {
            new ErrorAlerts().showError("No README found","No README Found");
        } catch (IOException e) {
        }
    }

}
