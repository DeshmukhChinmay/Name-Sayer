package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;

import main.Main;
import main.Names;
import main.Names.NameVersions;
import main.SceneChanger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ListMenuController implements Initializable {

    private String currentWorkingDir = System.getProperty("user.dir");
    private File databaseFolder;

    @FXML
    public ListView namesListView;
    @FXML
    public ListView<NameVersions> namesVersionListView;
    @FXML
    public ListView selectedNames;

    private LinkedList<Names> nameObjects = new LinkedList<>();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    private ObservableList<NameVersions> selectedVersionObjects = FXCollections.observableArrayList();
    final private ObservableList<String> selectedVersionsViewList = FXCollections.observableArrayList();
    private HashMap<String, Names> namesMap = new HashMap<>();

    Names tempName = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            initialiseNameObjects();
            updateMainList();

        } catch (IOException e) {
            e.printStackTrace();
        }

        initialiseNameMap();

        // Setting the cell of the namesVersionListView to a custom cell so that a checkbox is shown
        namesVersionListView.setCellFactory(CheckBoxListCell.forListView(NameVersions::versionSelected, new StringConverter<NameVersions>() {
            @Override
            public String toString(NameVersions object) {
                return object.getVersion();
            }

            @Override
            public NameVersions fromString(String string) {
                return null;
            }
        }));

        // Adding a listener to the selection from namesListView. Once the user selects a name, the namesVersionsListView
        // populates with the ObservableList present for the selected name instance
        namesListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                for (Names n : nameObjects) {
                    if (n.getName().equals(namesListView.getSelectionModel().getSelectedItem())) {
                        tempName = n;
                    }
                }

                if (tempName != null) {
                    namesVersionListView.setItems(tempName.getVersions());

                    tempName.getVersions().forEach(NameVersions -> NameVersions.versionSelected().addListener((obs, oldVal, newVal) -> {
                        for (NameVersions n : tempName.getVersions()) {
                            if (n.versionIsSelected()) {
                                if (!(selectedVersionsViewList.contains(n.getVersion()))) {
                                    selectedVersionsViewList.add(n.getVersion());
                                    selectedVersionObjects.add(n);
                                }
                            } else {
                                if (selectedVersionsViewList.contains(n.getVersion())) {
                                    selectedVersionsViewList.remove(n.getVersion());
                                    selectedVersionObjects.remove(n);
                                }
                            }
                        }
                    }));
                }
            }
        });

        selectedNames.setItems(selectedVersionsViewList);

        try {
            checkQualityStatus();
        } catch (IOException e) {
        }
    }

    //Initalises the quality rating by checking the Bad_Recordings.txt file
    public void checkQualityStatus() throws IOException {
        File file = new File("Bad_Recordings.txt");
        //Checks if such file already exists
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //Gets the key by only using the string up to the white space
                String key = line.substring(0, line.indexOf(" "));
                //Returns the name object associated with the key
                Names name = namesMap.get(key);
                if (name != null) {
                    //Loops through all the versions of that name until the string is the same
                    for (NameVersions nVer : name.getVersions()) {
                        if (nVer.getVersion().equals(line.substring(0, line.indexOf(')')+1))) {
                            //Sets bad quality to true
                            nVer.getBadQuality().setValue(true);
                        }
                    }
                }
            }
        }
    }

    //Creates the map of Names using the corresponding string as the key
    public void initialiseNameMap() {
        for (Names n : nameObjects) {
            namesMap.put(n.getName(), n);
        }
    }

    // Checks whether there are any audio files present in the database and copies them
    // into the appropriate folders. 'Names' objects are also created and added to the appropriate
    // ObservableLists. The name for the 'Names' objects are extracted from the file names and the
    // path of the file assigned to the field of a version for that name
    public void initialiseNameObjects() throws IOException {

        databaseFolder = Main.getDatabaseFolder();
        if(databaseFolder.exists()) {
            File[] namesInDatabase = databaseFolder.listFiles();
            String tempFilename;
            String tempName;

            for (File f : namesInDatabase) {
                if (f.isHidden()) {
                    continue;
                } else {
                    tempFilename = f.getName();
                    String[] tempFilenameParts = tempFilename.split("_");
                    String[] tempNameParts = tempFilenameParts[3].split("\\.");
                    tempName = tempNameParts[0].substring(0, 1).toUpperCase() + tempNameParts[0].substring(1);
                    File tempFolder = new File(currentWorkingDir + "/NameSayer/Recordings/" + tempName + "/");
                    File destination = new File(tempFolder + "/" + f.getName());

                    if (destination.exists()) {
                        boolean namePresent = false;
                        Names nameFound = null;
                        for (Names n : nameObjects) {
                            if (n.getName().equals(tempName)) {
                                namePresent = true;
                                nameFound = n;
                                break;
                            }
                        }

                        if (namePresent) {
                            nameFound.addVersion(tempName, destination.getAbsolutePath());
                        } else {
                            nameObjects.add(new Names(tempName, destination.getAbsolutePath()));
                        }

                    } else {
                        if (tempFolder.exists()) {
                            Files.copy(f.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            for (Names n : nameObjects) {
                                if (n.getName().equals(tempName)) {
                                    n.addVersion(tempName, destination.getAbsolutePath());

                                }
                            }
                        } else {
                            tempFolder.mkdirs();
                            Files.copy(f.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            nameObjects.add(new Names(tempName, destination.getAbsolutePath()));

                        }
                    }

                }
            }
        }
        else{
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Names Folder");
            errorAlert.setHeaderText(null);
            errorAlert.setHeaderText("Please Select Names");
            errorAlert.showAndWait();
        }
    }

    // Updating the namesListView so that it displays the names available from the database alphabetically
    public void updateMainList() {
        for (Names n : nameObjects) {
            namesViewList.add(n.getName());
        }

        namesListView.setItems(namesViewList.sorted());
        namesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public ObservableList<NameVersions> getSelectedVersionObjects() {
        return selectedVersionObjects;
    }

    public HashMap<String, Names> getNamesMap() {
        return namesMap;
    }

    private void clearSelection() {
        //Resets all items from selected lists except first one
        for (Names n : nameObjects) {
            for (NameVersions v : n.getVersions()) {
                v.versionSelected().setValue(false);
            }
        }
        //Clears lists for selected versions
        namesListView.getSelectionModel().clearSelection();
        namesVersionListView.setItems(null);
        selectedVersionObjects.clear();
        selectedVersionsViewList.clear();
    }


    public void clearButtonPressed() {

        clearSelection();

    }

    //Returns to the main menu
    public void backButtonPressed() {
        clearSelection();
        SceneChanger.loadMainPage();
    }

    //Goes to the play menu
    public void nextButtonPressed() {
        if (selectedNames.getItems().size() == 0) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Names Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select Names");
            errorAlert.showAndWait();
        } else {
            //If only one creation selected should not be able to press next or prev
            if (selectedNames.getItems().size() == 1) {
                SceneChanger.getPlayMenuController().single = true;
                SceneChanger.getPlayMenuController().nextButton.setDisable(true);
            }
            //This will set the buttons in the next scene to be disabled as no creation will be currently selected
            SceneChanger.getPlayMenuController().setFromUpload(false);
            SceneChanger.getPlayMenuController().playButton.setDisable(true);
            SceneChanger.getPlayMenuController().practiceButton.setDisable(true);
            SceneChanger.loadPlayPage();
        }
    }
}