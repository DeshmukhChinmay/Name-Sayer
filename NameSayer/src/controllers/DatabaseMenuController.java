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
import main.Names;
import main.Names.NameVersions;
import main.PlayableNames;
import main.SceneChanger;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
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
    public ListView<PlayableNames> practiceNamesListView;
    @FXML
    public ListView<NameVersions> databaseNamesListView;

    private ObservableList<PlayableNames> practiceNamesList = FXCollections.observableArrayList();

    private String currentWorkingDir = System.getProperty("user.dir");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateList();

        // Setting the cell of the selectedListView to a custom cell so custom text is displayed
        practiceNamesListView.setCellFactory(param -> new ListCell<PlayableNames>() {

            @Override
            protected void updateItem(PlayableNames name, boolean empty) {
                super.updateItem(name, empty);

                if (empty || name == null || name.getName() == null) {
                    setText(null);
                } else {
                    setText(name.getName());
                }
            }

        });

        // Adding listeners to the objects in practiceNamesListView. Once a practice name is selected, the corresponding
        // versions of that name present in the database are shown in another list
        practiceNamesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlayableNames>() {
            @Override
            public void changed(ObservableValue<? extends PlayableNames> observable, PlayableNames oldValue, PlayableNames newValue) {
                 ObservableList<NameVersions> test = FXCollections.observableArrayList();

                if (practiceNamesListView.getSelectionModel().getSelectedItem() != null) {

                    String[] tempNameString = practiceNamesListView.getSelectionModel().getSelectedItem().getName().split(" ");

                    for (String s : tempNameString) {
                            Names tempNameObjects = SceneChanger.getListMenuController().getNamesMap().get(s.trim());
                        if (tempNameObjects != null) {
                            for (NameVersions n : tempNameObjects.getVersions()) {
                                if(test.contains(n)){
                                    continue;
                                }
                                else {
                                    test.add(n);
                                }
                            }
                        }
                    }
                }
                    databaseNamesListView.setItems(test);
                }


        });

        databaseNamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Setting the cell of the databaseNamesListView to a custom cell so custom text is displayed
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

    // Updating the practiceNamesLists with the files that are already present in the PracticeNames folder.
    // All of the required objects are created appropriately
    public void updateList() {

        File practiceFolder = new File(currentWorkingDir + "/NameSayer/PracticeNames/");
        File[] practiceFolderFiles = practiceFolder.listFiles();
        String tempName;

        for (File f : practiceFolderFiles) {
            if (!f.isHidden()) {
                String[] tempFileName = f.getName().split("_");
                String tempDate = "(Date: " + tempFileName[1].replaceAll("-", "/") + ")";
                String tempTime = "(Time: " + tempFileName[2].split("\\.")[0].replaceAll("-", ":") + ")";
                tempName = tempFileName[0] + " " + tempDate + " " + tempTime;
                LinkedList<String> tempAudioPath = new LinkedList<>();
                tempAudioPath.add(f.getAbsolutePath());
                boolean nameFound = false;

                if (practiceNamesList.size() != 0) {
                    for (PlayableNames name : practiceNamesList) {
                        if (name.getName().equals(tempName)) {
                            nameFound = true;
                            break;
                        }
                    }

                    if (!nameFound) {
                        practiceNamesList.add(new PlayableNames(tempName, tempAudioPath));
                    }

                } else {
                    practiceNamesList.add(new PlayableNames(tempName, tempAudioPath));
                }
            }

        }

    }

    //Changes the scene back to the main page
    public void backButtonPressed() {
        SceneChanger.loadMainPage();
    }

    //Deletes the selected user made practice file
    public void deleteButtonPressed() {
        PlayableNames selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();
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
            confirmationAlert.setHeaderText("Are you sure you want to delete " + selectedName.getName());
            Optional<ButtonType> option = confirmationAlert.showAndWait();
            if (option.get() == ButtonType.OK) {
                File tempAudioFile = new File(selectedName.getAudioPath().get(0));
                if (tempAudioFile.exists()) {
                    tempAudioFile.delete();
                }
                practiceNamesList.remove(selectedName);
                databaseNamesListView.setItems(null);
                practiceNamesListView.getSelectionModel().clearSelection();
            }
        }
    }

    // Playing the selected practice name
    public void playButtonPressed() {

        PlayableNames selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();
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

    // Playing the selected version of a name from the database
    public void playDatabaseVersionButtonPressed() {
        if (databaseNamesListView.getSelectionModel().getSelectedItem() != null) {
            LinkedList<String> tempAudioPath = new LinkedList<>();
            tempAudioPath.add(databaseNamesListView.getSelectionModel().getSelectedItem().getAudioPath());
            PlayableNames selectedDatabaseName = new PlayableNames(databaseNamesListView.getSelectionModel().getSelectedItem().getVersion(), tempAudioPath);
            //If there is a selected name then disable the required buttons during playing and then
            //re enable them after it has finished playing
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

        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Database Name Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select a Database Names");
            errorAlert.showAndWait();
        }
    }

}