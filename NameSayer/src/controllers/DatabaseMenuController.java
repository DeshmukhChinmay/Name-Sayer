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

                Names tempNameObjects = SceneChanger.getListMenuController().getNamesMap().get(practiceNamesListView.getSelectionModel().getSelectedItem().getParentName());

                if (tempNameObjects != null) {
                    databaseNamesListView.setItems(tempNameObjects.getVersions());
                }

                databaseNamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            }
        });

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

        for (File f: practiceFolderFiles) {
            if (f.isHidden()){
                continue;
            } else {
                String[] tempFileName = f.getName().split("_");
                String tempDate = "(Date: " + tempFileName[1].replaceAll("-", "/") + ")";
                String tempTime = "(Time: " + tempFileName[2].split("\\.")[0].replaceAll("-", ":") + ")";
                tempName = tempFileName[0] + " " + tempDate + " " + tempTime;
                tempAudioPath = f.getAbsolutePath();

                if (practiceNamesList.size() > 0) {
                    for (NameVersions version: practiceNamesList) {
                        if (!(version.getVersion().equals(tempName))) {
                            Names tempNameObject = new Names(tempFileName[0], tempAudioPath);
                            practiceNamesList.add(new NameVersions(tempNameObject, tempName, tempAudioPath));
                            break;
                        }
                    }
                } else {
                    Names tempNameObject = new Names(tempFileName[0], tempAudioPath);
                    practiceNamesList.add(new NameVersions(tempNameObject, tempName, tempAudioPath));
                }
            }
        }

    }

    public void backButtonPressed() {
        SceneChanger.loadMainPage();
    }

    public void deleteButtonPressed() {

        NameVersions selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();

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
                practiceNamesList.remove(selectedName);
//                updateList();
            }
        }

    }

    public void playButtonPressed() {

        NameVersions selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();
        //Play the selected creation on a separate thread
        if (selectedName != null) {
            playButton.setText("Playing");
            playButton.setDisable(true);
            Audio.getInstance().playAudio(playButton,selectedName);
        }
    }
}
