package controllers;

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

import javax.naming.Name;
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
    public ListView<NameVersions> selectedNames;
    @FXML
    public TextField nameTagField;
    @FXML
    public ToggleButton filterButton;
    @FXML
    public TextField tagField;
    @FXML
    public Label tagName;
    @FXML
    public Label qualityField;
    @FXML
    public Label durationField;


    private LinkedList<Names> nameObjects = new LinkedList<>();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    private ObservableList<NameVersions> selectedVersionObjects = FXCollections.observableArrayList();
    final private ObservableList<String> selectedVersionsViewList = FXCollections.observableArrayList();
    private ObservableList<String> namesSearchViewList = FXCollections.observableArrayList();

    private HashMap<String, Names> namesMap = new HashMap<>();
    NameVersions currentlySelected;
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
        //initalises the lsit view of selected names to store NameVersion objects
        selectedNames.setCellFactory(param -> new ListCell<NameVersions>() {

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

        selectedNames.setItems(selectedVersionObjects);

        try {
            checkQualityStatus();
            initialiseTags();
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
                        if (nVer.getVersion().equals(line.substring(0, line.indexOf(')') + 1))) {
                            //Sets bad quality to true
                            nVer.getBadQuality().setValue(true);
                        }
                    }
                }
            }
        }
    }

    public void initialiseTags() throws IOException {
        File tagFile = new File("Tags_File.txt");
        //Checks if such file already exists
        if (tagFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(tagFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //Gets the key by only using the string up to the white space
                String key = line.substring(0, line.indexOf(" "));
                //Returns the name object associated with the key
                Names name = namesMap.get(key);
                if (name != null) {
                    //Loops through all the versions of that name until the string is the same
                    for (NameVersions nVer : name.getVersions()) {
                        if (nVer.getVersion().equals(line.substring(0, line.indexOf(')') + 1))) {
                            //Sets bad quality to true
                            nVer.setTag(line.substring(line.indexOf('_') + 1, line.length()));
                            System.out.println(nVer.getTag());
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
        if (databaseFolder.exists()) {
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
        } else {
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
        clearInfoPanel();
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
            clearInfoPanel();
        }
    }

    //This dynamically updates the list view for the user to select single names to play
    public void searchFunction() {
        namesSearchViewList.clear();
        if (!filterButton.isSelected()) { //Searches by name
            if (nameTagField.getText().length() == 0 || nameTagField.getText() == null) { //If notthing entered then output everything
                namesListView.setItems(namesViewList.sorted());
            } else {
                for (Names n : nameObjects) { //Loops through all names and checks if the first letters are matching and outputs it
                    if ((n.getName().length() >= nameTagField.getText().length()) &&
                            (n.getName().substring(0, nameTagField.getText().length()).toLowerCase().equals(nameTagField.getText().toLowerCase()))) {
                        namesSearchViewList.add(n.getName());
                    }
                }
                namesListView.setItems(namesSearchViewList);
            }
        } else { //Searches by tag
            if (nameTagField.getText().length() == 0 || nameTagField.getText() == null) {
                namesListView.setItems(namesViewList.sorted());
            } else {
                for (Names n : nameObjects) { //If the versions of a name has the same tag it outputs the parent name case insensitive
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

    //Changes the text on toggle button
    public void onFilterButtonPressed() {
        if (filterButton.isSelected()) {
            filterButton.setText("Tag");

        } else {
            filterButton.setText("Name");
        }
        searchFunction();
    }

    //Method for tag button. WHen tag button is pressed it removes the current tag from the text file if there is one
    //and then changes the field in the respective NameVersion object and then adds the new tag to the text file
    public void onTagButtonPressed() throws Exception {
        if (currentlySelected == null) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Name Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select Name and try again");
            errorAlert.showAndWait();
        } else {
            if (currentlySelected.getTag() != null) { //Removes current tag from text file and adds new tag
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
    //sets the info when the items in the selected list are clicked
    public void selectedListClicked() throws Exception {
        setInfoTab(selectedNames);
    }
    //sets the info when the items in the version selection are clicked
    public void onSelectVersion() throws Exception {
        setInfoTab(namesVersionListView);
    }
    //Sets the info to using whichever list was clicked where listView is whichever list was clicked
    public void setInfoTab(ListView<NameVersions> listView) throws Exception {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            currentlySelected = listView.getSelectionModel().getSelectedItem();
            tagName.setText(currentlySelected.getTag());
            durationField.setText(Double.toString(Audio.getInstance().getWavFileLength(new File(currentlySelected.getAudioPath()))) + " Seconds");
            if (currentlySelected.getBadQuality().getValue()) { //gets the quality button
                qualityField.setText("Bad");
            } else {
                qualityField.setText("Good");
            }
        }
    }
    //Clears the info panel
    private void clearInfoPanel(){
        nameTagField.clear();
        tagField.clear();
        tagName.setText(null);
        durationField.setText(null);
        qualityField.setText(null);

    }

}