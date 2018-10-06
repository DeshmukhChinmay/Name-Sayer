package controllers;

import javafx.fxml.Initializable;

import main.BadAudioText;
import main.SceneChanger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    //Initialize method to create the text file if not made
    public void initialize(URL location, ResourceBundle resources) {
        try {
            BadAudioText.getInstance();
        } catch (IOException e) {
        }
    }

    //Changes scene to where the list view of all creations are shown
    public void listButtonPressed() {
        SceneChanger.loadListPage();
    }

    //Changes the scene to the settings scene and also opens the mic line
    public void settingsButtonPressed() {
        SceneChanger.loadSettingsPage();
    }

    // Changes scene to the database and practiced names
    public void showAttemptsButtonPressed() {
        SceneChanger.loadDatabaseMenu();
    }

    public void uploadFilePressed() {
        SceneChanger.loadUploadSearchPage();
    }
}
