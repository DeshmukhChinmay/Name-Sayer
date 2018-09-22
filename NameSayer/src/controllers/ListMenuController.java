package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Map;
import java.util.ResourceBundle;

public class ListMenuController implements Initializable {

    private String currentWorkingDir = System.getProperty("user.dir");
    private File databaseFolder;

    public ListView namesListView;
    public ListView<NameVersions> namesVersionListView;
    public ListView selectedNames;

    private LinkedList<Names> nameObjects = new LinkedList<>();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    private ObservableList<NameVersions> selectedVersionObjects = FXCollections.observableArrayList();
    final private ObservableList<String> selectedVersionsViewList = FXCollections.observableArrayList();
    private HashMap<String,Names> namesMap = new HashMap<>();

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
        }catch(IOException e){}
    }
    //Initalises the quality rating by checking the Bad_Recordings.txt file
    public void checkQualityStatus() throws IOException{
        File file = new File("Bad_Recordings.txt");
        //Checks if such file already exists
        if(file.exists()){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line = reader.readLine())!=null){
                //Gets the key by only using the string up to the white space
                String key = line.substring(0,line.indexOf(" "));
                //Returns the name object associated with the key
                Names name = namesMap.get(key);
                if(name != null){
                    //Loops through all the versions of that name until the string is the same
                    for(NameVersions nVer : name.getVersions()){
                        if (nVer.getVersion().equals(line)){
                            //Sets bad quality to true
                            nVer.getBadQuality().setValue(true);
                        }
                    }
                }
            }
        }
    }

    public void initialiseNameMap(){
        for (Names n : nameObjects) {
            namesMap.put(n.getName(), n);
        }
    }

    public void initialiseNameObjects() throws IOException {

        databaseFolder = Main.getDatabaseFolder();

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

    public void updateMainList() {
        for (Names n : nameObjects) {
            namesViewList.add(n.getName());
        }

        namesListView.setItems(namesViewList.sorted());
        namesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public ObservableList<String> getSelectedVersionsViewList() {
        return selectedVersionsViewList;
    }

    public ObservableList<NameVersions> getSelectedVersionObjects() {
        return selectedVersionObjects;
    }

    public HashMap<String, Names> getNamesMap() {
        return namesMap;
    }

    //Returns to the main menu
    public void backButtonPressed() {
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
            if(selectedNames.getItems().size() == 1){
                SceneChanger.getPlayMenuController().single = true;
                SceneChanger.getPlayMenuController().nextButton.setDisable(true);
            }
            SceneChanger.getPlayMenuController().playButton.setDisable(true);
            SceneChanger.getPlayMenuController().practiceButton.setDisable(true);
            SceneChanger.loadPlayPage();
        }
    }
}