package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private static Stage _primaryStage;
    private static Stage _secondaryStage = new Stage();

    private static Scene mainMenuScene;
    private static Scene listMenuScene;
    private static Scene playMenuScene;
    private static Scene practiceMenuScene;


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
    public static void loadPracticePage(){
        _secondaryStage.setScene(practiceMenuScene);
        _secondaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
