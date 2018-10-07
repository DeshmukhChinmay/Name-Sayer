package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import main.*;
import main.Names.NameVersions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class UploadSearchMenuController implements Initializable {

    @FXML
    private TextArea inputFileTextArea;
    @FXML
    private ListView<String> playableNamesListView;
    @FXML
    private TextField enteredName;
    @FXML
    private Button selectButton;

    private File fileUploaded = null;
    private ObservableList<String> playableNames = FXCollections.observableArrayList();
    private ObservableList<PlayableNames> playableNamesObjects = FXCollections.observableArrayList();

    private int characterLimit = 50;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Makes the listview unselectable
        playableNamesListView.setMouseTransparent( true );
        playableNamesListView.setFocusTraversable( false );
        selectButton.defaultButtonProperty().bind(selectButton.focusedProperty());

        // Limiting the user input to 50 characters
        enteredName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (enteredName.getText().length() > characterLimit) {
                    String temp = enteredName.getText().substring(0, characterLimit);
                    enteredName.setText(temp);
                }
            }
        });

        // Added a key event so that the user can press enter after entering a name
        // instead of clicking the button
        enteredName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    selectButtonPressed();
                }
            }
        });
    }

    // Method to let the user select a text file that contains names
    public void fileChooser() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a text file containing names");
        fileUploaded = fileChooser.showOpenDialog(null);

        if (fileUploaded != null) {
            try {
                fileUploaded();
                fileUploaded = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Reading the uploaded txt file and populating a list with names that are available
    // in the database from the txt file
    public void fileUploaded() throws IOException {
        if (fileUploaded != null) {
            inputFileTextArea.clear();
            playableNames.clear();
            playableNamesListView.setItems(playableNames);
            inputFileTextArea.setVisible(true);

            BufferedReader reader = new BufferedReader(new FileReader(fileUploaded));
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] tempNames = line.split("[ -]");
                inputFileTextArea.appendText(line + "\n");
                createPlayableNames(tempNames);
            }

        }

    }

    // Reading the name the user has entered in the textfield and populating a list
    // with the names that can be played from the inputted name
    public void selectButtonPressed() {
        if (!enteredName.getText().equals("")) {
            String[] tempNames = enteredName.getText().split("[ -]");
            playableNamesListView.setItems(playableNames);
            createPlayableNames(tempNames);
        }
        enteredName.clear();
    }

    // Creating PlayableNames objects with the necessary audio paths
    public void createPlayableNames(String[] tempNames) {
        String tempString = "";
        LinkedList<String> tempAudioPath = new LinkedList<>();
        boolean firstName = true;
        for (String s: tempNames) {
            String modifiedName = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
            if (SceneChanger.getListMenuController().isPresent(modifiedName)) {
                if (firstName) {
                    tempString = modifiedName;
                } else {
                    tempString = tempString + " " + modifiedName;
                }
                // Checking if a good quality recording is present for a certain name and
                // adding it to the audioPath for the PlayableNames object. If there is no good
                // quality present then the first version of the name is adding to the audioPath
                boolean goodQualityFound = false;
                for (NameVersions n : SceneChanger.getListMenuController().getNamesMap().get(modifiedName).getVersions()) {
                    if (!n.getBadQuality().get()) {
                        tempAudioPath.add(n.getAudioPath());
                        goodQualityFound = true;
                        break;
                    }
                }
                if (!goodQualityFound) {
                    tempAudioPath.add(SceneChanger.getListMenuController().getNamesMap().get(modifiedName).getVersions().get(0).getAudioPath());
                }
                firstName = false;
            }
        }

        if (!tempString.equals("")) {
            if (!playableNames.contains(tempString)) {
                playableNames.add(tempString);
                playableNamesObjects.add(new PlayableNames(tempString, tempAudioPath));
            }
        }

    }

    public ObservableList<PlayableNames> getPlayableNamesObjects() {
        return playableNamesObjects;
    }

    // Clear all of the lists and disabling the text area
    public void backButtonPressed() {

        if (inputFileTextArea.isVisible()) {
            inputFileTextArea.clear();
            inputFileTextArea.setVisible(false);
        }

        playableNamesListView.setItems(null);
        selectButton.setDisable(true);
        clearButtonPressed();
        SceneChanger.loadMainPage();

    }

    // Changing the scene to the play menu
    public void nextButtonPressed() {

        if (playableNamesObjects.size() == 0) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No names are playable");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("The selected names are not in the database");
            errorAlert.showAndWait();
        }

        if(playableNamesObjects.size() == 1){
            SceneChanger.getPlayMenuController().single = true;
            SceneChanger.getPlayMenuController().nextButton.setDisable(true);

        }

        selectButton.setDisable(true);
        SceneChanger.getPlayMenuController().playButton.setDisable(true);
        SceneChanger.getPlayMenuController().practiceButton.setDisable(true);
        SceneChanger.getPlayMenuController().setFromUpload(true);
        SceneChanger.getPlayMenuController().toggleQualityButtonVisibility();
        SceneChanger.loadPlayPage();

    }

    // Getting the input from the textfield when the enter key is pressed
    public void handleKeyReleased() {
        String text = enteredName.getText();
        boolean disableButton = text.isEmpty() || text.trim().isEmpty() || text.endsWith(" ");
        selectButton.setDisable(disableButton);
    }

    // Clearing the lists that are present in the current scene
    public void clearButtonPressed() {
        inputFileTextArea.clear();
        inputFileTextArea.setVisible(false);
        playableNames.clear();
        enteredName.clear();
        playableNamesObjects.clear();
    }

}