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
        SceneChanger.set_primaryStage(primaryStage);
        SceneChanger.set_secondaryStage();
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
