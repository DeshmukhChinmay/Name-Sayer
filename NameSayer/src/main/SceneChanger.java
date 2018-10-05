package main;

import controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneChanger {
    private static SceneChanger ourInstance = new SceneChanger();

    private  static Stage _primaryStage;
    private static Stage _secondaryStage = new Stage();

    private static Scene mainMenuScene;
    private static Scene listMenuScene;
    private static Scene playMenuScene;
    private static Scene practiceMenuScene;
    private static Scene settingsScene;
    private static Scene databaseScene;
    private static Scene uploadSearchScene;

    private static ListMenuController listMenuController;
    private static DatabaseMenuController databaseMenuController;
    private static MainMenuController mainMenuController;
    private static PlayMenuController playMenuController;
    private static PracticeMenuController practiceMenuController;
    private static SettingsMenuController settingsMenuController;
    private static UploadSearchMenuController uploadSearchMenuController;

    public static SceneChanger getInstance() {
        return ourInstance;
    }

    private SceneChanger(){

    }
    //Loads all Fxml files and gets the instance of each controller as well
    public void loadFXMLFiles() throws Exception{
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/mainMenu.fxml"));
        Parent mainMenuPane = mainMenuLoader.load();
        mainMenuScene = new Scene(mainMenuPane, 1366, 768);
        mainMenuController = mainMenuLoader.getController();

        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/listMenu.fxml"));
        Parent listMenuPane = listLoader.load();
        listMenuScene = new Scene(listMenuPane, 1366, 768);
        listMenuController = listLoader.getController();

        FXMLLoader uploadSearchLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/uploadSearchMenu.fxml"));
        Parent uploadSearchPane = uploadSearchLoader.load();
        uploadSearchScene = new Scene(uploadSearchPane, 1366, 768);
        uploadSearchMenuController = uploadSearchLoader.getController();

        FXMLLoader playLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/playMenu.fxml"));
        Parent playMenuPane = playLoader.load();
        playMenuScene = new Scene(playMenuPane, 1366, 768);
        playMenuController = playLoader.getController();

        FXMLLoader practiceLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/practiceMenu.fxml"));
        Parent practiceMenuPane = practiceLoader.load();
        practiceMenuScene = new Scene(practiceMenuPane, 534, 206);
        practiceMenuController = practiceLoader.getController();

        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/settingsMenu.fxml"));
        Parent settingsPane = settingsLoader.load();
        settingsScene = new Scene(settingsPane, 1366, 768);
        settingsMenuController = settingsLoader.getController();

        FXMLLoader databaseLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/databaseMenu.fxml"));
        Parent databasePane = databaseLoader.load();
        databaseScene = new Scene(databasePane, 1366, 768);
        databaseMenuController = databaseLoader.getController();
    }
    //Loads Main page in the primary stage
    public static void loadMainPage() {
        _secondaryStage.close();
        _primaryStage.setScene(mainMenuScene);
    }

    //Loads list page in the primary stage
    public static void loadListPage() {
        _secondaryStage.close();
        _primaryStage.setScene(listMenuScene);
    }

    public static void loadUploadSearchPage(){
        _primaryStage.setScene(uploadSearchScene);
    }

    //Loads play page in primary stage
    public static void loadPlayPage() {
        _secondaryStage.close();
        _primaryStage.setScene(playMenuScene);
    }

    //Loads practice page
    public static void loadPracticePage() {
        _secondaryStage.setScene(practiceMenuScene);
        _secondaryStage.show();
    }

    //Loads mic test page
    public static void loadSettingsPage() {
        _primaryStage.setScene(settingsScene);
    }

    public static void loadDatabaseMenu() {
        _primaryStage.setScene(databaseScene);
    }

    //The following methods all return a instance of the respective controller
    public static ListMenuController getListMenuController() {
        return listMenuController;
    }

    public static DatabaseMenuController getDatabaseMenuController() {
        return databaseMenuController;
    }

    public static MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public static PlayMenuController getPlayMenuController() {
        return playMenuController;
    }

    public static PracticeMenuController getPracticeMenuController() {
        return practiceMenuController;
    }

    public static SettingsMenuController getSettingsMenuController() {
        return settingsMenuController;
    }
    //Sets up first and primary stage
    public static void set_primaryStage(Stage stage){
        _primaryStage = stage;
        _primaryStage.setTitle("NameSayer");
        _primaryStage.setScene(mainMenuScene);
        _primaryStage.setResizable(false);
        _primaryStage.show();
    }
    //Sets up secondary stage
    public static void set_secondaryStage(){
        _secondaryStage.setTitle("Name Sayer");
        _secondaryStage.setResizable(false);
        _secondaryStage.initStyle(StageStyle.UNDECORATED);
        _secondaryStage.initModality(Modality.APPLICATION_MODAL);
    }


}
