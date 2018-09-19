package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;

import javafx.util.StringConverter;
import main.Main;
import main.Names;
import main.Names.NameVersions;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ListMenuController implements Initializable {

    private String currentWorkingDir;
    private File databaseFolder;

    public ListView namesList;
    public ListView<NameVersions> namesVersion;
    public ListView selectedNames;
    private LinkedList<Names> nameObjects = new LinkedList<>();
    private ObservableList<NameVersions> selectedVersionObjects = FXCollections.observableArrayList();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    final private ObservableList<String> selectedVersionsViewList = FXCollections.observableArrayList();

    Names tempName = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        currentWorkingDir = System.getProperty("user.dir");
        initialiseFolders();

        nameObjects.add(new Names("John Doe"));
        nameObjects.add(new Names("Jane Doe"));
        nameObjects.add(new Names("Jack Doe"));

        nameObjects.get(0).addVersion("John Doe");
        nameObjects.get(0).addVersion("John Doe");
        nameObjects.get(1).addVersion("Jane Doe");
        nameObjects.get(2).addVersion("Jack Doe");
        nameObjects.get(2).addVersion("Jack Doe");
        nameObjects.get(2).addVersion("Jack Doe");


        for (Names n: nameObjects) {
            namesViewList.add(n.getName());
        }

        namesList.setItems(namesViewList);
        namesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        namesVersion.setCellFactory(CheckBoxListCell.forListView(NameVersions::versionSelected, new StringConverter<NameVersions>() {
            @Override
            public String toString(NameVersions object) {
                return object.getVersion();
            }

            @Override
            public NameVersions fromString(String string) {
                return null;
            }
        }));

        namesList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                for (Names n: nameObjects) {
                    if (n.getName().equals(namesList.getSelectionModel().getSelectedItem())) {
                        tempName = n;
                    }
                }

                if (tempName != null) {
                    namesVersion.setItems(tempName.getVersions());

                    tempName.getVersions().forEach(NameVersions -> NameVersions.versionSelected().addListener((obs, oldVal, newVal) -> {
                        for (NameVersions n: tempName.getVersions()) {
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
    }

    public void initialiseFolders() {

        databaseFolder = Main.getDatabaseFolder();

    }

    public void intialiseNameObjects() {

        File[] namesInDatabase = databaseFolder.listFiles();
        String tempFilename;
        String tempName;

        for (File f: namesInDatabase) {
            tempFilename = f.getName();
            String[] tempFilenameParts = tempFilename.split("_");
            String[] tempNameParts = tempFilenameParts[3].split(".");
            tempName = tempNameParts[0];
            tempName.toLowerCase().charAt(0);

            File tempFolder = new File(currentWorkingDir + "/NameSayer/Recordings/" + tempName);

            if (tempFolder.exists()) {

            } else {
                tempFolder.mkdirs();

            }

        }

    }

    public ObservableList<String> getSelectedVersionsViewList() {
        return selectedVersionsViewList;
    }

    public ObservableList<NameVersions> getSelectedVersionObjects() {
        return selectedVersionObjects;
    }

    //Returns to the main menu
    public void backButtonPressed(){
        //Resets all items from selected lists except first one
        for (Names n: nameObjects) {
            for (NameVersions v: n.getVersions()) {
                v.versionSelected().setValue(false);
            }
        }

        selectedVersionObjects.clear();
        Main.loadMainPage();
    }


    //Goes to the play menu
    public void playButtonPressed(){
        Main.loadPlayPage();
    }

}