package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.Main;
import main.Names;
import main.Names.NameVersions;

import javax.swing.text.html.Option;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DatabaseMenuController implements Initializable {

    @FXML
    public ListView<NameVersions> practiceNamesListView;

    private ObservableList<NameVersions> practiceNamesList;

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

        practiceNamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        practiceNamesListView.setItems(practiceNamesList);

    }

    public void updateList() {

        File practiceFolder = new File(currentWorkingDir + "/NameSayer/PracticeNames");
        File[] practiceFolderFiles = practiceFolder.listFiles();

        for (File f: practiceFolderFiles) {
            for (NameVersions version: practiceNamesList) {
                if (!(version.getVersion().equals(f.getName()))) {
                    practiceNamesList.add(new NameVersions(f.getName(), f.getAbsolutePath()));
                }
            }
        }

    }

    public void addPracticeVersion(NameVersions practiceNameVersion) {
        practiceNamesList.add(practiceNameVersion);
    }

    public void backButtonPressed() {
        Main.loadMainPage();
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
                updateList();
            }
        }

    }

    public void playButtonPressed() {

        NameVersions selectedName = practiceNamesListView.getSelectionModel().getSelectedItem();

        //Play the selected creation on a separate thread

    }
}
