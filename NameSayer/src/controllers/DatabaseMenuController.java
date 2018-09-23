package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.Audio;
import main.Main;
import main.Names;
import main.Names.NameVersions;
import main.SceneChanger;

import javax.swing.text.html.Option;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class DatabaseMenuController implements Initializable {

    @FXML
    public Button playButton;
    @FXML
    public Button playDatabaseNameButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button backButton;

    @FXML
    public ListView<NameVersions> practiceNamesListView;
    @FXML
    public ListView<NameVersions> databaseNamesListView;

    private ObservableList<NameVersions> practiceNamesList = FXCollections.observableArrayList();

    private String currentWorkingDir = System.getProperty("user.dir");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateList();

        practiceNamesListView.setCellFactory(param -> new ListCell<NameVersions>() {

            @Override
            protected void updateItem(NameVersions version, boolean empty) {
                super.updateItem(version, empty);

                if (empty || version == null || version.getVersion() == null) {
                    setText(null);
                } else {
                    setText(version.getVersion());
                }
            }

        });

        practiceNamesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NameVersions>() {
            @Override
            public void changed(ObservableValue<? extends NameVersions> observable, NameVersions oldValue, NameVersions newValue) {

                if (practiceNamesListView.getSelectionModel().getSelectedItem() != null) {

                    Names tempNameObjects = SceneChanger.getListMenuController().getNamesMap().get(practiceNamesListView.getSelectionModel().getSelectedItem().getParentName());

                    if (tempNameObjects != null) {
                        databaseNamesListView.setItems(tempNameObjects.getVersions());
                    }

                }

            }
        });

        databaseNamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        databaseNamesListView.setCellFactory(param -> new ListCell<NameVersions>() {

            @Override
            protected void updateItem(NameVersions version, boolean empty) {
                super.updateItem(version, empty);

                if (empty || version == null || version.getVersion() == null) {
                    setText(null);
                } else {
                    setText(version.getVersion());
                }
            }

        });

        practiceNamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        practiceNamesListView.setItems(practiceNamesList);

    }

    public void updateList() {

        File practiceFolder = new File(currentWorkingDir + "/NameSayer/PracticeNames/");
        File[] practiceFolderFiles = practiceFolder.listFiles();
        String tempName;
        String tempAudioPath;

        for (File f : practiceFolderFiles) {
            if (f.isHidden()) {
                continue;
            } else {
                String[] tempFileName = f.getName().split("_");
                String tempDate = "(Date: " + tempFileName[1].replaceAll("-", "/") + ")";
                String tempTime = "(Time: " + tempFileName[2].split("\\.")[0].replaceAll("-", ":") + ")";
                tempName = tempFileName[0] + " " + tempDate + " " + tempTime;
                tempAudioPath = f.getAbsolutePath();
                boolean nameFound = false;

                if (practiceNamesList.size() != 0) {
                    for (NameVersions version : practiceNamesList) {
                        if (version.getVersion().equals(tempName)) {
                            nameFound = true;
                            break;
                        }
                    }

                    if (!nameFound) {
                        Names tempNameObject = new Names(tempFileName[0], tempAudioPath);
                        practiceNamesList.add(new NameVersions(tempNameObject, tempName, tempAudioPath));
                    }

                } else {
                    Names tempNameObject = new Names(tempFileName[0], tempAudioPath);
                    practiceNamesList.add(new NameVersions(tempNameObject, tempName, tempAudioPath));
                }
            }
        }

    }

    //Changes the scene back to the main page
    public void backButtonPressed() {
        SceneChanger.loadMainPage();
    }

    //Deletes the selected user amde practice file
    public void deleteButtonPressed() {
        NameVersions selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();
        //Checks if there is a name selected or else shows a alert box
        if (selectedName == null) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Name Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select a Name");
            errorAlert.showAndWait();
        } else {
            //Add the code to ask for confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setHeaderText("Are you sure you want to delete " + selectedName.getVersion());
            Optional<ButtonType> option = confirmationAlert.showAndWait();
            if (option.get() == ButtonType.OK) {
                File tempAudioFile = new File(selectedName.getAudioPath());
                if (tempAudioFile.exists()) {
                    tempAudioFile.delete();
                }
                practiceNamesList.remove(selectedName);
                databaseNamesListView.setItems(null);
                practiceNamesListView.getSelectionModel().clearSelection();
            }
        }

    }

    //
    public void playButtonPressed() {

        NameVersions selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();
        if (selectedName != null) {
            playButton.setText("Playing");
            playButton.setDisable(true);
            playDatabaseNameButton.setDisable(true);
            deleteButton.setDisable(true);
            backButton.setDisable(true);
            //Play the selected creation on a separate thread

            Task task = Audio.getInstance().playAudio(selectedName);
            task.setOnSucceeded(e -> { //Enables all buttons after finished playing
                playButton.setDisable(false);
                playDatabaseNameButton.setDisable(false);
                deleteButton.setDisable(false);
                backButton.setDisable(false);
                playButton.setText("Play Practice Recording");
            });
            new Thread(task).start();
        } else {//If no name is selected then it shows an alert to the user
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No File Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select File");
            errorAlert.showAndWait();
        }

    }

    public void playDatabaseVersionButtonPressed() {

        NameVersions selectedDatabaseName = databaseNamesListView.getSelectionModel().getSelectedItem();
        //If there is a selected name then disable the required buttons during playing and then it
        //re enables them after it finished playing
        if (selectedDatabaseName != null) {
            playDatabaseNameButton.setText("Playing");
            playDatabaseNameButton.setDisable(true);
            playButton.setDisable(true);
            deleteButton.setDisable(true);
            backButton.setDisable(true);
            Task task = Audio.getInstance().playAudio(selectedDatabaseName);
            task.setOnSucceeded(e -> {
                playButton.setDisable(false);
                playDatabaseNameButton.setDisable(false);
                deleteButton.setDisable(false);
                backButton.setDisable(false);
                playDatabaseNameButton.setText("Play Database Recording");
            });
            new Thread(task).start();
        } else { //If no name is selected then it shows an alert to the user
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Database Name Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select a Database Names");
            errorAlert.showAndWait();
        }
    }

}
