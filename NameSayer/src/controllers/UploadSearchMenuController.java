package controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import main.Names;
import main.Names.NameVersions;
import main.SceneChanger;

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

    private File fileUploaded = null;
    private ObservableList<String> playableNames = FXCollections.observableArrayList();
    private ObservableList<NameVersions> namesToPlay = FXCollections.observableArrayList();

    private int characterLimit = 50;

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
            String tempString;

            while ((line = reader.readLine()) != null) {
                String[] tempNames = line.split("[ -]");
                inputFileTextArea.appendText(line + "\n");
                tempString = "";
                for (String s: tempNames) {
                    if (SceneChanger.getListMenuController().isPresent(s)) {
                        tempString = tempString + s + " ";
                        boolean goodQualityFound = false;
                        for (NameVersions n: SceneChanger.getListMenuController().getNamesMap().get(s).getVersions()) {
                            if (!n.getBadQuality().get()) {
                                namesToPlay.add(n);
                                goodQualityFound = true;
                                break;
                            }
                        }
                        if (!goodQualityFound) {
                            namesToPlay.add(SceneChanger.getListMenuController().getNamesMap().get(s).getVersions().get(0));
                        }
                    }
                }
                if (!tempString.equals("")) {
                    playableNames.add(tempString);
                }
            }

        }
    }

    public void selectButtonPressed() {
        if (!enteredName.getText().equals("")) {
            playableNames.clear();
            playableNamesListView.setItems(playableNames);
            String[] tempNames = enteredName.getText().split("[ -]");
            String tempString = "";
            for (String s: tempNames) {
                if (SceneChanger.getListMenuController().isPresent(s)) {
                    tempString = tempString + s + " ";
                    boolean goodQualityFound = false;
                    for (NameVersions n: SceneChanger.getListMenuController().getNamesMap().get(s).getVersions()) {
                        if (!n.getBadQuality().get()) {
                            namesToPlay.add(n);
                            goodQualityFound = true;
                            break;
                        }
                    }
                    if (!goodQualityFound) {
                        namesToPlay.add(SceneChanger.getListMenuController().getNamesMap().get(s).getVersions().get(0));
                    }
                }
            }
            if (!tempString.equals("")) {
                playableNames.add(tempString);
            }
        }
    }

    public ObservableList<NameVersions> getNamesToPlay() {
        return namesToPlay;
    }

    public void backButtonPressed(){
        if (inputFileTextArea.isVisible()) {
            inputFileTextArea.clear();
            inputFileTextArea.setVisible(false);
        }
        playableNames.clear();
        playableNamesListView.setItems(null);
        namesToPlay.clear();
        SceneChanger.loadMainPage();
    }

    public void nextButtonPressed(){

        if (namesToPlay.size() == 0) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No names are playable");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("The selected names are not in the database");
            errorAlert.showAndWait();
        }

        SceneChanger.loadPlayPage();
        SceneChanger.getPlayMenuController().setFromUpload(true);
    }
}
