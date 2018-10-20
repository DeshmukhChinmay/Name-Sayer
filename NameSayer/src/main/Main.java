package main;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private static File _databaseFolder = null;
    private static String currentWorkingDir = System.getProperty("user.dir");

    @Override
    public void start(Stage primaryStage) throws Exception {
        _databaseFolder = new File(currentWorkingDir + "/names");
        initialiseFolders();
        SceneChanger sceneChanger = SceneChanger.getInstance();
        sceneChanger.loadFXMLFiles();
        SceneChanger.setPrimaryStage(primaryStage);
        SceneChanger.set_secondaryStage();
        SceneChanger.loadMainPage();
    }

    public static File getDatabaseFolder() {
        return _databaseFolder;
    }

    public static void setDatabaseFolder(File databaseFolder) {
        _databaseFolder = databaseFolder;
    }

    //Initialises all the necessary folders during startup
    public static void initialiseFolders() {

        File recordingsFolder = new File(currentWorkingDir + "/NameSayer/Recordings");
        File tempFolder = new File(currentWorkingDir + "/NameSayer/Temp");
        File tempConcatFolder = new File(currentWorkingDir + "/NameSayer/Temp/Concat/");
        File practiceFolder = new File(currentWorkingDir + "/NameSayer/PracticeNames");

        if (!(recordingsFolder.exists())) {
            recordingsFolder.mkdirs();
        }

        if (!(tempFolder.exists())) {
            tempFolder.mkdirs();
        }

        if (!(tempConcatFolder.exists())) {
            tempConcatFolder.mkdirs();
        }

        if (!(practiceFolder.exists())) {
            practiceFolder.mkdirs();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
