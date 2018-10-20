package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import main.ErrorAlerts;
import main.SceneChanger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HelpMenuController {
    @FXML
    private TextArea readmeArea;

    @FXML
    public void initialize() throws IOException {
        System.out.println(System.getProperty("user.dir")+"/README.md");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/README.txt"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                readmeArea.appendText(line + "\n");
            }
        }catch(FileNotFoundException e){
            new ErrorAlerts().showError("No README found","No README Found");
        }

    }

}
