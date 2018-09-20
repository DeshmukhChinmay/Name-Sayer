package controllers;

import com.sun.org.apache.xml.internal.security.Init;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.BadAudioText;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    private Stage directoryChooserStage = new Stage();
    private TestMicrophoneController controller;

    //Initialize method to create the text file if not made
    public void initialize(URL location, ResourceBundle resources){
        try {
            BadAudioText.getInstance();
        }catch(IOException e){}

    }

    //Changes scene to where the list view of all creations are shown
    public void listButtonPressed() {
        Main.loadListPage();
    }

    public void micTestButtonPressed() {
        Main.loadMicTestPage();
        controller.testMic();
    }

    public void showAttemptsButtonPressed() {
        Main.loadDatabaseMenu();
    }

    public void setTestMicController(TestMicrophoneController controller) {
        this.controller = controller;
    }

    public void selectDatabaseFolderPressed() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(directoryChooserStage);
        if (selectedDirectory == null) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Directory Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Choose a Directory");
            errorAlert.showAndWait();
        } else {
            Main.setDatabaseFolder(selectedDirectory);
            Main.getListMenuController().initialiseNameObjects();
            Main.getListMenuController().updateMainList();
            System.out.println(selectedDirectory);
        }

    }

}
