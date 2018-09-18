package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private static Stage _primaryStage;

    private static Scene mainMenuScene;
    private static Scene listenMenuScene;

    private static File _databaseFolder;

    @Override
    public void start(Stage primaryStage) throws Exception{
        _primaryStage = primaryStage;

        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/mainMenu.fxml"));
        Parent mainMenuPane = mainMenuLoader.load();
        mainMenuScene = new Scene(mainMenuPane, 554, 394);

        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/listMenu.fxml"));
        Parent listenMenuPane = listLoader.load();
        listenMenuScene = new Scene(listenMenuPane, 635, 406);

        _primaryStage.setTitle("NameSayer");
        _primaryStage.setScene(mainMenuScene);
        _primaryStage.setResizable(false);
        _primaryStage.show();
    }
    //Loads Main page in the primary stage
    public static void loadMainPage(){
        _primaryStage.setScene(mainMenuScene);
    }
    //Loads list page in the primary stage
    public static void loadListenPage(){
        _primaryStage.setScene(listenMenuScene);
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
