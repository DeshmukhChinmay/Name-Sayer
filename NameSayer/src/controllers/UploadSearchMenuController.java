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
        selectButton.defaultButtonProperty().bind(selectButton.focusedProperty());

        enteredName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (enteredName.getText().length() > characterLimit) {
                    String temp = enteredName.getText().substring(0, characterLimit);
                    enteredName.setText(temp);
                }
            }
        });
        enteredName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    selectButtonPressed();
                }
            }
        });
    }

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

    public void selectButtonPressed() {
        if (!enteredName.getText().equals("")) {
            String[] tempNames = enteredName.getText().split("[ -]");
            playableNamesListView.setItems(playableNames);

                createPlayableNames(tempNames);
            }
        enteredName.clear();
    }

    public void createPlayableNames(String[] tempNames) {
        String tempString = "";
        LinkedList<String> tempAudioPath = new LinkedList<>();
        boolean firstName = false;
        for (String s: tempNames) {
            String modifiedName = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
            if (SceneChanger.getListMenuController().isPresent(modifiedName)) {
                if (firstName) {
                    tempString = modifiedName;
                } else {
                    tempString = tempString + " " + modifiedName;
                }
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

    public void nextButtonPressed() {

        if (playableNamesObjects.size() == 0) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No names are playable");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("The selected names are not in the database");
            errorAlert.showAndWait();
        }
        selectButton.setDisable(true);
        SceneChanger.getPlayMenuController().setFromUpload(true);
        SceneChanger.getPlayMenuController().toggleQualityButtonVisibility();
        SceneChanger.loadPlayPage();
    }

    public void handleKeyReleased() {
        String text = enteredName.getText();
        boolean disableButton = text.isEmpty() || text.trim().isEmpty() || text.endsWith(" ");
        selectButton.setDisable(disableButton);
    }

    public void clearButtonPressed() {
        playableNames.clear();
        enteredName.clear();
        playableNamesObjects.clear();
    }

}