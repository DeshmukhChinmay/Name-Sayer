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

    private static Stage primaryStage;
    private static Stage secondaryStage = new Stage();

    private static Scene mainMenuScene;
    private static Scene listMenuScene;
    private static Scene playMenuScene;
    private static Scene practiceMenuScene;
    private static Scene settingsScene;
    private static Scene databaseScene;
    private static Scene enterNamesScene;
    private static Scene helpMenuScene;

    private static ListMenuController listMenuController;
    private static DatabaseMenuController databaseMenuController;
    private static MainMenuController mainMenuController;
    private static PlayMenuController playMenuController;
    private static PracticeMenuController practiceMenuController;
    private static SettingsMenuController settingsMenuController;
    private static EnterNamesMenuController enterNamesMenuController;
    private static HelpMenuController helpMenuController;

    public static SceneChanger getInstance() {
        return ourInstance;
    }

    private SceneChanger() {
    }

    // Loads all Fxml files and gets the instance of each controller as well
    public void loadFXMLFiles() throws Exception {
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/mainMenu.fxml"));
        Parent mainMenuPane = mainMenuLoader.load();
        mainMenuScene = new Scene(mainMenuPane, 1366, 768);
        mainMenuController = mainMenuLoader.getController();

        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/listMenu.fxml"));
        Parent listMenuPane = listLoader.load();
        listMenuScene = new Scene(listMenuPane, 1366, 768);
        listMenuController = listLoader.getController();

        FXMLLoader enterNamesLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/enterNamesMenu.fxml"));
        Parent enterNamesPane = enterNamesLoader.load();
        enterNamesScene = new Scene(enterNamesPane, 1366, 768);
        enterNamesMenuController = enterNamesLoader.getController();

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

        FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/helpMenu.fxml"));
        Parent helpPane = helpLoader.load();
        helpMenuScene = new Scene(helpPane, 650, 700);
        helpMenuController = helpLoader.getController();
    }

    // Loads Main page in the primary stage
    public static void loadMainPage() {
        secondaryStage.close();
        primaryStage.setScene(mainMenuScene);
    }

    // Loads list page in the primary stage
    public static void loadListPage() {
        secondaryStage.close();
        primaryStage.setScene(listMenuScene);
    }

    // Loads the enter names scene in the primary stage
    public static void loadEnterNamesPage() {
        primaryStage.setScene(enterNamesScene);
    }

    // Loads play page in primary stage
    public static void loadPlayPage() {
        secondaryStage.close();
        primaryStage.setScene(playMenuScene);
    }

    // Loads practice page
    public static void loadPracticePage() {
        secondaryStage.setScene(practiceMenuScene);
        secondaryStage.show();
    }

    // Loads mic test page
    public static void loadSettingsPage() {
        secondaryStage.close();
        primaryStage.setScene(settingsScene);
    }

    // Loads the database menu page
    public static void loadDatabaseMenu() {
        primaryStage.setScene(databaseScene);
    }

    // Loads the help menu page
    public static void loadHelpMenu(){
        Stage helpStage = new Stage();
        helpStage.setScene(helpMenuScene);
        helpStage.show();
    }

    // The following methods all return a instance of the respective controller
    public static ListMenuController getListMenuController() {
        return listMenuController;
    }

    public static DatabaseMenuController getDatabaseMenuController() {
        return databaseMenuController;
    }

    public static PlayMenuController getPlayMenuController() {
        return playMenuController;
    }

    public static PracticeMenuController getPracticeMenuController() {
        return practiceMenuController;
    }

    public static EnterNamesMenuController getEnterNamesMenuController() {
        return enterNamesMenuController;
    }

    // Sets up the primary stage
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("NameSayer");
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Sets up the secondary stage
    public static void set_secondaryStage() {
        secondaryStage.setTitle("Name Sayer");
        secondaryStage.setResizable(false);
        secondaryStage.initStyle(StageStyle.UNDECORATED);
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
    }


}
