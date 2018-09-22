package main;

import controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {

//    private static Stage _primaryStage;
//    private static Stage _secondaryStage = new Stage();
//
//    private static Scene mainMenuScene;
//    private static Scene listMenuScene;
//    private static Scene playMenuScene;
//    private static Scene practiceMenuScene;
//    private static Scene micTestScene;
//    private static Scene databaseScene;
//
//    private static ListMenuController listMenuController;
//    private static DatabaseMenuController databaseMenuController;
//    private static MainMenuController mainMenuController;
//    private static PlayMenuController playMenuController;
//    private static PracticeMenuController practiceMenuController;
//    private static TestMicrophoneController testMicrophoneController;

    private static File _databaseFolder = null;
    private static String currentWorkingDir = System.getProperty("user.dir");

    @Override
    public void start(Stage primaryStage) throws Exception {

        _databaseFolder = new File(currentWorkingDir + "/names");
        initialiseFolders();
        SceneChanger sceneChanger = SceneChanger.getInstance();
        sceneChanger.loadFXMLFiles();
        SceneChanger.set_primaryStage(primaryStage);
        SceneChanger.loadMainPage();
    }

    public static File getDatabaseFolder() {
        return _databaseFolder;
    }

    public static void initialiseFolders() {

        File recordingsFolder = new File(currentWorkingDir + "/NameSayer/Recordings");
        File tempFolder = new File(currentWorkingDir + "/NameSayer/Temp");
        File practiceFolder = new File(currentWorkingDir + "/NameSayer/PracticeNames");

        if (!(recordingsFolder.exists())) {
            recordingsFolder.mkdirs();
        }

        if (!(tempFolder.exists())) {
            tempFolder.mkdirs();
        }

        if (!(practiceFolder.exists())) {
            practiceFolder.mkdirs();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
