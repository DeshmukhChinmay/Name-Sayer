package controllers;

import main.SceneChanger;

public class MainMenuController {

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

    // Changes scene to the scene where the user can select names or upload a txt file containing names
    public void uploadFilePressed() {
        SceneChanger.getEnterNamesMenuController().setSearchList();
        SceneChanger.getEnterNamesMenuController().showSearchList();
        SceneChanger.loadEnterNamesPage();
    }
}
