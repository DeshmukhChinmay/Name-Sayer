package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private static Stage _primaryStage;
    private static Stage _secondaryStage;

    private static Scene mainMenuScene;
    private static Scene listMenuScene;

    private static File databaseFolder;

    @Override
    public void start(Stage primaryStage) throws Exception{
        _primaryStage = primaryStage;

        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/mainMenu.fxml"));
        Parent mainMenuPane = mainMenuLoader.load();
        mainMenuScene = new Scene(mainMenuPane, 499, 206);

        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("../fxmlFiles/listMenu.fxml"));
        Parent listMenuPane = listLoader.load();
        listMenuScene = new Scene(listMenuPane, 843, 487);

        _primaryStage.setTitle("NameSayer");
        _primaryStage.setScene(mainMenuScene);
        _primaryStage.show();
    }
    //Loads Main page in the primary stage
    public static void loadMainPage(){
        _primaryStage.setScene(mainMenuScene);
    }
    //Loads list page in the primary stage
    public static void loadListPage(){
        _primaryStage.setScene(listMenuScene);
    }

    public static File getDatabaseFolder() {
        return databaseFolder;
    }

    public static void setDatabaseFolder(File databaseFolder) {
        this.databaseFolder = databaseFolder;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
