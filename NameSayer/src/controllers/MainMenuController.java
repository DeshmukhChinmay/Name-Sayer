package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.Main;

import java.net.URL;

public class MainMenuController{

    private TestMicrophoneController controller;

    //Changes scene to where the list view of all creations are shown
    public void listButtonPressed(){
        Main.loadListPage();
    }

    public void micTestButtonPressed(){
        Main.loadMicTestPage();
        controller.testMic();
    }
    public void showAttemptsButtonPressed(){
        Main.loadDatabaseMenu();
    }
    public void setTestMicController(TestMicrophoneController controller){
        this.controller = controller;
    }

}
