package controllers;

import javafx.application.Platform;
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
    @FXML
    private ListView searchList;

    private File fileUploaded = null;

    private ObservableList<String> playableNames = FXCollections.observableArrayList();
    private ObservableList<PlayableNames> playableNamesObjects = FXCollections.observableArrayList();
    private ObservableList<String> namesSearchViewList = FXCollections.observableArrayList();

    private int characterLimit = 50;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Makes the suggestion list for the names in the database not clickable but still enabling the user
        //to scroll through the list
        searchList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        searchList.getSelectionModel().select(-1);
                    }
                });

            }
        });

        // Makes the playable names list not clickable but still enabling the user to scroll through the list
        playableNamesListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        playableNamesListView.getSelectionModel().select(-1);
                    }
                });

            }
        });

        // Focus on the select button when initializing the scene
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
                playableNamesObjects.clear();
                fileUploaded();
                fileUploaded = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Reading the uploaded text file and populating a list with names that are available
    // in the database from the text file. A text area also shows the contents of the file
    public void fileUploaded() throws IOException {

        if (fileUploaded != null) {
            inputFileTextArea.clear();
            playableNames.clear();
            playableNamesListView.setItems(playableNames);
            inputFileTextArea.setVisible(true);
            searchList.setVisible(false);

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

        setSearchList();
        enteredName.clear();

    }

    // Creating PlayableNames objects with the necessary audio paths
    public void createPlayableNames(String[] tempNames) {

        LinkedList<String> tempAudioPath = new LinkedList<>();
        boolean firstName = true;
        String tempString = "";

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
            } else{
                // If a name does not exist it will show an alert
                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                errorAlert.setTitle("Name Doesn't Exist!");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("The Name \"" + modifiedName + "\" does not exist. \n \n Will only add present names");
                errorAlert.showAndWait();
            }
        }

        if (!tempString.equals("")) {
            if (!playableNames.contains(tempString)) {
                playableNames.add(tempString);
                playableNamesObjects.add(new PlayableNames(tempString, tempAudioPath));
            }
        }

    }

    // Clear all of the lists and disable the text area
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
        } else {

            if (playableNamesObjects.size() == 1) {
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
    }

    // Getting the input from the textfield when the enter key is pressed and filters the searchList
    public void handleKeyReleased() {

        // Clears the list so there are no duplicate names shown
        namesSearchViewList.clear();
        String text = enteredName.getText();
        String temp = text.replace("-", " ");//replaces - with spaces

        boolean disableButton = text.isEmpty() || text.trim().isEmpty() || text.endsWith(" ") || text.startsWith(" ");
        selectButton.setDisable(disableButton);

        // If the button is disabled should show everything in the list
        if(disableButton){
            setSearchList();
        } else {
            // If the string contains a space then more names are being added and the suggestion list should
            // show a suggestion for the new names being added
            if(temp.contains(" ")){

                // Getting the index of last space
                int charAfterSpace = temp.substring(temp.lastIndexOf(" ")).length();

                for (Names n: SceneChanger.getListMenuController().getNameObjects()){
                    // Checks if the entered names are bigger than the actual name
                    if ((n.getName().length() >= charAfterSpace-1) &&
                            (n.getName().substring(0, charAfterSpace-1).toLowerCase().equals(temp.substring((temp.lastIndexOf(" "))).trim().toLowerCase()))) {
                        // If names are equal adds to observable list
                        namesSearchViewList.add(n.getName());
                    }
                }

                searchList.setItems(namesSearchViewList);

            } else {
                // Loops through all names and checks if the first letters are matching and outputs it
                for (Names n : SceneChanger.getListMenuController().getNameObjects()) {
                    if ((n.getName().length() >= temp.length()) &&
                            (n.getName().substring(0, temp.length()).toLowerCase().equals(temp.toLowerCase()))) {
                        namesSearchViewList.add(n.getName());
                    }
                }

                searchList.setItems(namesSearchViewList);
            }
        }

    }

    // Clearing the lists that are present in the current scene
    public void clearButtonPressed() {
        inputFileTextArea.clear();
        inputFileTextArea.setVisible(false);
        playableNames.clear();
        enteredName.clear();
        playableNamesObjects.clear();
        setSearchList();
    }

    // Sets the search list with all items
    public void setSearchList() {
        searchList.setItems(SceneChanger.getListMenuController().namesListView.getItems());
    }

    // Sets search list to visible and text area to not visible
    public void showSearchList() {
        searchList.setVisible(true);
        inputFileTextArea.setVisible(false);
    }

    public ObservableList<PlayableNames> getPlayableNamesObjects() {
        return playableNamesObjects;
    }

}