package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage _primaryStage;

    private static Scene mainMenuScene;
    private static Scene listMenuScene;
    private static Scene playMenuScene;


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
    public static void loadListPage(){
        _primaryStage.setScene(listMenuScene);
    }
    //Loads play page in primary stage
    public static void loadPlayPage(){
        _primaryStage.setScene(playMenuScene);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
