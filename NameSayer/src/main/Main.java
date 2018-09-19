package main;

import controllers.MainMenuController;
import controllers.TestMicrophoneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class Main extends Application {

    private static Stage _primaryStage;
    private static Stage _secondaryStage = new Stage();

    private static Scene mainMenuScene;
    private static Scene listMenuScene;
    private static Scene playMenuScene;
    private static Scene practiceMenuScene;
    private static Scene micTestScene;
    private static Scene databaseScene;

    private static File _databaseFolder = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        _primaryStage = primaryStage;

        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/mainMenu.fxml"));
        Parent mainMenuPane = mainMenuLoader.load();
        mainMenuScene = new Scene(mainMenuPane, 554, 394);

        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/listMenu.fxml"));
        Parent listMenuPane = listLoader.load();
        listMenuScene = new Scene(listMenuPane, 635, 406);

        FXMLLoader playLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/playMenu.fxml"));
        Parent playMenuPane = playLoader.load();
        playMenuScene = new Scene(playMenuPane, 635, 406);

        FXMLLoader practiceLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/practiceMenu.fxml"));
        Parent practiceMenuPane = practiceLoader.load();
        practiceMenuScene = new Scene(practiceMenuPane, 534, 206);

        FXMLLoader micTestLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/testMicrophone.fxml"));
        Parent micTestPane = micTestLoader.load();
        micTestScene = new Scene(micTestPane, 534, 206);

        FXMLLoader databaseLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/databaseMenu.fxml"));
        Parent databasePane = databaseLoader.load();
        databaseScene = new Scene(databasePane, 635, 406);

        //Adds a TestMicController to MainMenuController
        TestMicrophoneController testMicrophoneController = micTestLoader.getController();
        MainMenuController mainMenuController = mainMenuLoader.getController();
        mainMenuController.setTestMicController(testMicrophoneController);

        _primaryStage.setTitle("NameSayer");
        _primaryStage.setScene(mainMenuScene);
        _primaryStage.setResizable(false);
        _primaryStage.show();

        _secondaryStage.setTitle("Name Sayer");
        _secondaryStage.setResizable(false);
        _secondaryStage.initStyle(StageStyle.UNDECORATED);
        _secondaryStage.initModality(Modality.APPLICATION_MODAL);

    }
    //Loads Main page in the primary stage
    public static void loadMainPage(){
        _secondaryStage.close();
        _primaryStage.setScene(mainMenuScene);
    }
    //Loads list page in the primary stage
    public static void loadListPage(){
        _secondaryStage.close();
        _primaryStage.setScene(listMenuScene);
    }
    //Loads play page in primary stage
    public static void loadPlayPage(){
        _secondaryStage.close();
        _primaryStage.setScene(playMenuScene);
    }
    //Loads practice page
    public static void loadPracticePage(){
        _secondaryStage.setScene(practiceMenuScene);
        _secondaryStage.show();
    }
    //Loads mic test page
    public static void loadMicTestPage(){
        _secondaryStage.setScene(micTestScene);
        _secondaryStage.show();
    }
    public static void loadDatabaseMenu(){
        _primaryStage.setScene(databaseScene);
    }

    public static File getDatabaseFolder() {
        return _databaseFolder;
    }

    public static void setDatabaseFolder(File databaseFolder) {
        _databaseFolder = databaseFolder;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
