package controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;

import main.*;
import main.Names.NameVersions;

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

    @FXML
    public ListView namesListView;
    @FXML
    public ListView<NameVersions> namesVersionListView;
    @FXML
    public ListView<PlayableNames> selectedNames;
    @FXML
    public TextField nameTagField;
    @FXML
    public TextField tagField;
    @FXML
    public Label tagName;
    @FXML
    public Label qualityField;
    @FXML
    public Label durationField;
    @FXML
    public ComboBox searchBy;

    private String currentWorkingDir = System.getProperty("user.dir");
    private File databaseFolder;

    private LinkedList<Names> nameObjects = new LinkedList<>();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    private ObservableList<PlayableNames> playableNamesObjects = FXCollections.observableArrayList();
    final private ObservableList<String> selectedVersionsViewList = FXCollections.observableArrayList();
    private ObservableList<String> namesSearchViewList = FXCollections.observableArrayList();

    private HashMap<String, Names> namesMap = new HashMap<>();
    private HashMap<String, NameVersions> nameVersionsMap = new HashMap<>();

    private NameVersions currentlySelected;
    private Names tempName = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Calling the initialise name objects method so that appropriate objects are created
        // for the audio files that are present in the Recordings folder
        initialiseNameObjects();

        // Calling update name objects method so that any new files from the Database folder are
        // adding to the Recordings folder
        try {
            updateNameObjects();
            updateMainList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calling the methods to initialize the Hash Map
        initialiseNameMap();
        initialiseNameVersionsMap();

        // Setting the cell of the selectedListView to a custom cell so custom text is displayed
        selectedNames.setCellFactory(param -> new ListCell<PlayableNames>() {

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
                                    LinkedList<String> tempArray = new LinkedList<>();
                                    tempArray.add(n.getAudioPath());
                                    playableNamesObjects.add(new PlayableNames(n.getVersion(), tempArray));
                                }
                            } else {
                                if (selectedVersionsViewList.contains(n.getVersion())) {
                                    selectedVersionsViewList.remove(n.getVersion());
                                    PlayableNames tempName = null;
                                    for (PlayableNames pN : playableNamesObjects) {
                                        if (n.getVersion().equals(pN.getName())) {
                                            tempName = pN;
                                            break;
                                        }
                                    }
                                    playableNamesObjects.remove(tempName);
                                }
                            }
                        }
                    }));
                }
            }
        });

        // Calling the methods to set the tags and quality for the necessary objects
        try {
            checkQualityStatus();
            initialiseTags();
        } catch (IOException e) {
        }

        // Makes the selected names list not clickable but still enabling the user to scroll through the list
        selectedNames.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldvalue, Number newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        selectedNames.getSelectionModel().select(-1);
                    }
                });

            }
        });

        searchBy.getItems().addAll("Name", "Tag");
        searchBy.getSelectionModel().select("Name");

        selectedNames.setItems(playableNamesObjects);

    }

    // Checks whether there are any names present in the Recordings folder and creates
    // the necessary objects
    private void initialiseNameObjects() {

        File recordingsFolder = new File(currentWorkingDir + "/NameSayer/Recordings/");
        File[] nameFolders = recordingsFolder.listFiles();
        for (File nameFolder : nameFolders) {
            String tempName = nameFolder.getName();
            if (!nameFolder.isHidden()) {
                File[] nameFiles = nameFolder.listFiles();
                boolean firstFile = true;
                for (File name : nameFiles) {
                    if (firstFile) {
                        nameObjects.add(new Names(tempName, name.getAbsolutePath()));
                        firstFile = false;
                    } else {
                        Names nameFound = null;
                        for (Names n : nameObjects) {
                            if (n.getName().equals(tempName)) {
                                nameFound = n;
                                break;
                            }
                        }
                        nameFound.addVersion(tempName, name.getAbsolutePath());
                    }
                }
            }
        }

    }

    // Checks whether there are any audio files present in the database and copies them
    // into the appropriate folders. 'Names' objects are also created and added to the appropriate
    // ObservableLists. The name for the 'Names' objects are extracted from the file names and the
    // path of the file assigned to the field of a version for that name
    private void updateNameObjects() throws IOException {

        databaseFolder = Main.getDatabaseFolder();
        if (databaseFolder.exists()) {
            File[] namesInDatabase = databaseFolder.listFiles();
            String tempFilename;
            String tempName;

            for (File f : namesInDatabase) {
                if (!f.isHidden()) {
                    tempFilename = f.getName();
                    String[] tempFilenameParts = tempFilename.split("_");
                    String[] tempNameParts = tempFilenameParts[3].split("\\.");
                    tempName = tempNameParts[0].substring(0, 1).toUpperCase() + tempNameParts[0].substring(1);
                    File tempFolder = new File(currentWorkingDir + "/NameSayer/Recordings/" + tempName + "/");
                    File destination = new File(tempFolder + "/" + f.getName());

                    if (!destination.exists()) {
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

        } else if(new File(currentWorkingDir+"/NameSayer/Recordings").listFiles() == null ||
                new File(currentWorkingDir+"/NameSayer/Recordings").listFiles().length == 0 ) {
            new ErrorAlerts().showError("No Default Folder","No Default Names Folder");
        }

    }

    // Updating the namesListView so that it displays the names available from the database alphabetically
    private void updateMainList() {

        for (Names n : nameObjects) {
            namesViewList.add(n.getName());
        }

        namesListView.setItems(namesViewList.sorted());
        namesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    // Initialising the necessary objects with the tags that are assigned to them from the text file
    private void initialiseTags() throws IOException {

        File tagFile = new File("Tags_File.txt");

        // Checks if such file already exists
        if (tagFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(tagFile));
            String line = null;

            while ((line = reader.readLine()) != null) {

                // Gets the key by only using the string up to the white space
                String key = line.substring(0, line.indexOf(" "));
                // Returns the name object associated with the key
                Names name = namesMap.get(key);

                if (name != null) {
                    // Loops through all the versions of that name until the string is the same
                    for (NameVersions nVer : name.getVersions()) {
                        if (nVer.getVersion().equals(line.substring(0, line.indexOf(')') + 1))) {
                            // Sets bad quality to true
                            nVer.setTag(line.substring(line.indexOf('_') + 1));
                        }
                    }
                }
            }
        }
    }

    // Creates the map of Names using the corresponding string as the key
    private void initialiseNameMap() {
        for (Names n : nameObjects) {
            namesMap.put(n.getName(), n);
        }
    }

    // Creates the map of NameVersions using the corresponding string as the key
    private void initialiseNameVersionsMap() {
        for (Names n : nameObjects) {
            for (NameVersions nV : n.getVersions()) {
                nameVersionsMap.put(nV.getVersion(), nV);
            }
        }
    }

    // Initializing the quality rating by checking the Bad_Recordings.txt file
    public void checkQualityStatus() throws IOException {

        File file = new File("Bad_Recordings.txt");

        // Checks if such file already exists
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = reader.readLine()) != null) {

                // Gets the key by only using the string up to the white space
                String key = line.substring(0, line.indexOf(" "));
                // Returns the name object associated with the key
                Names name = namesMap.get(key);

                if (name != null) {
                    // Loops through all the versions of that name until the string is the same
                    for (NameVersions nVer : name.getVersions()) {
                        if (nVer.getVersion().equals(line.substring(0, line.indexOf(')') + 1))) {
                            // Sets bad quality to true
                            nVer.getBadQuality().setValue(true);
                        }
                    }
                }
            }
        }
    }

    // Re-initializing everything necessary after a new database is added
    public void reinitialiseAll() throws IOException {
        updateNameObjects();
        initialiseNameMap();
        initialiseNameVersionsMap();
        checkQualityStatus();
        initialiseTags();
        updateMainList();
    }

    // Clearing all of the selected items from all of the ListViews
    private void clearSelection() {

        if (nameObjects != null) {
            //Resets all items from selected lists except first one
            for (Names n : nameObjects) {
                for (NameVersions v : n.getVersions()) {
                    if (v != null) {
                        v.versionSelected().setValue(false);
                    }
                }
            }
        }

        // Clears lists for selected versions
        namesListView.getSelectionModel().clearSelection();
        namesVersionListView.setItems(null);
        playableNamesObjects.clear();
        selectedVersionsViewList.clear();
    }

    // Clears the selected items
    public void clearButtonPressed() {
        clearSelection();
    }

    // Returns to the main menu
    public void backButtonPressed() {
        clearInfoPanel();
        clearSelection();
        namesListView.setItems(null);
        namesListView.setItems(namesViewList.sorted());
        SceneChanger.loadMainPage();
    }

    // Changing the screen to the play menu
    public void nextButtonPressed() {
        if (selectedNames.getItems().size() == 0) {
            new ErrorAlerts().showError("No Names Selected","Please Select a Name");
        } else {
            // If only one creation selected should not be able to press next or prev
            if (selectedNames.getItems().size() == 1) {
                SceneChanger.getPlayMenuController().single = true;
                SceneChanger.getPlayMenuController().nextButton.setDisable(true);
            }
            // This will set the buttons in the next scene to be disabled as no creation will be currently selected
            SceneChanger.getPlayMenuController().setFromUpload(false);
            SceneChanger.getPlayMenuController().toggleQualityButtonVisibility();
            SceneChanger.getPlayMenuController().playButton.setDisable(true);
            SceneChanger.getPlayMenuController().practiceButton.setDisable(true);
            SceneChanger.loadPlayPage();
            clearInfoPanel();
        }
    }

    // Dynamically updating the list view for the user to select single names to play
    public void searchFunction() {
        namesSearchViewList.clear();

        // Searches by name
        if (searchBy.getSelectionModel().getSelectedItem().equals("Name")) {
            // If nothing entered then output everything
            if (nameTagField.getText().length() == 0 || nameTagField.getText() == null) {
                namesListView.setItems(namesViewList.sorted());
            } else {
                // Loops through all names and checks if the first letters are matching and outputs it
                for (Names n : nameObjects) {
                    if ((n.getName().length() >= nameTagField.getText().length()) &&
                            (n.getName().substring(0, nameTagField.getText().length()).toLowerCase().equals(nameTagField.getText().toLowerCase()))) {
                        namesSearchViewList.add(n.getName());
                    }
                }
                namesListView.setItems(namesSearchViewList);
            }
        } else if (searchBy.getSelectionModel().getSelectedItem().equals("Tag")) { // Searches by tag
            if (nameTagField.getText().length() == 0 || nameTagField.getText() == null) {
                namesListView.setItems(namesViewList.sorted());
            } else {
                // If the versions of a name has the same tag it outputs the parent name case insensitive
                for (Names n : nameObjects) {
                    for (NameVersions nameVersions : n.getVersions()) {
                        if ((nameVersions.getTag() != null) && (nameVersions.getTag().toLowerCase().equals(nameTagField.getText().toLowerCase()))) {
                            namesSearchViewList.add(nameVersions.getParentName());
                            break;
                        }
                    }
                }
                namesListView.setItems(namesSearchViewList);
            }
        }
    }

    // Method for tag button. When tag button is pressed it removes the current tag from the text file if there is one
    // and then changes the field in the respective NameVersion object and then adds the new tag to the text file
    public void onTagButtonPressed() throws Exception {

        if (currentlySelected == null) {
            new ErrorAlerts().showError("No Name Selected","Please Select a Name");
        } else {
            // Removes current tag from text file and adds new tag
            if (currentlySelected.getTag() != null) {
                TagText.getInstance().removeTextFromFile(currentlySelected.getVersion(), currentlySelected.getTag());
            }

            TagText.getInstance().writeText(currentlySelected.getVersion(), tagField.getText());
            currentlySelected.setTag(tagField.getText()); //Sets the field in the object
            tagName.setText(currentlySelected.getTag()); //Sets the text on the label in the UI
        }

        currentlySelected = null;
        selectedNames.getSelectionModel().clearSelection();
        namesVersionListView.getSelectionModel().clearSelection();

    }

    // Calling the method to set the info labels when any item in the ListView is selected
    public void onSelectVersion() throws Exception {
        setInfoTab(namesVersionListView);
    }

    // Sets the info labels using the selected version from the ListView
    private void setInfoTab(ListView<NameVersions> listView) throws Exception {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            currentlySelected = listView.getSelectionModel().getSelectedItem();

            // Setting the appropriate labels
            tagName.setText(currentlySelected.getTag());

            durationField.setText(Double.toString(Audio.getInstance().getWavFileLength(new File(currentlySelected.getAudioPath()))));

            if (currentlySelected.getBadQuality().getValue()) {
                qualityField.setText("Bad");
            } else {
                qualityField.setText("Good");
            }
        }
    }

    // Clears the labels for the info panel
    public void clearInfoPanel() {
        nameTagField.clear();
        tagField.clear();
        tagName.setText(null);
        durationField.setText(null);
        qualityField.setText(null);

    }

    // Checks whether a given name is present in the database
    public boolean isPresent(String name) {
        if (namesMap.get(name) == null) {
            return false;
        } else {
            return true;
        }
    }

    public LinkedList<Names> getNameObjects(){
        return nameObjects;
    }

    ObservableList<PlayableNames> getPlayableNamesObjects() {
        return playableNamesObjects;
    }

    HashMap<String, Names> getNamesMap() {
        return namesMap;
    }

    HashMap<String, NameVersions> getNameVersionsMap() {
        return nameVersionsMap;
    }

}