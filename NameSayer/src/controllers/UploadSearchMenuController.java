package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import main.*;
import main.Names.NameVersions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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
    private String currentWorkingDir = System.getProperty("user.dir");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        enteredName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (enteredName.getText().length() > characterLimit) {
                    String temp = enteredName.getText().substring(0, characterLimit);
                    enteredName.setText(temp);
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
            try {
                createPlayableNames(tempNames);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createPlayableNames(String[] tempNames) throws IOException {
        Service<Void> concatService;
        String tempString = "";
        String tempAudioPath = "";
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
                        if (firstName) {
                            tempAudioPath = n.getAudioPath();
                        } else {
                            tempAudioPath = tempAudioPath + "%" + n.getAudioPath();
                        }
                        goodQualityFound = true;
                        break;
                    }
                }
                if (!goodQualityFound) {
                    if (firstName) {
                        tempAudioPath = SceneChanger.getListMenuController().getNamesMap().get(modifiedName).getVersions().get(0).getAudioPath();
                    } else {
                        tempAudioPath = tempAudioPath + "%" + SceneChanger.getListMenuController().getNamesMap().get(modifiedName).getVersions().get(0).getAudioPath();
                    }
                }
                firstName = false;
            }
        }

        if (!tempString.equals("")) {
            if (!playableNames.contains(tempString)) {
                File file = new File(currentWorkingDir + "/NameSayer/Temp/Concat/concatFiles.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                playableNames.add(tempString);
                ConcatFilesText.getInstance().writeText(tempAudioPath);
                final String finalName = tempString;
                try {
                    concatService = Audio.getInstance().concatAudioFiles(tempString);
                    concatService.start();
                    concatService.setOnSucceeded(event -> {
                        playableNamesObjects.add(new PlayableNames(finalName, currentWorkingDir + "/NameSayer/Temp/Concat/" + finalName + ".wav"));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        playableNames.clear();
        playableNamesListView.setItems(null);
        playableNamesObjects.clear();
        selectButton.setDisable(true);
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
    }

}