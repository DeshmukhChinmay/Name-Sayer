package controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import javafx.util.StringConverter;
import main.Main;
import main.Names;
import main.Names.NameVersions;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class listMenuController implements Initializable {

    public ListView namesList;
    public ListView<NameVersions> namesVersion;
    public ListView selectedNames;

    private LinkedList<Names> nameObjects = new LinkedList<>();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    final private ObservableList<String> selectedVersionsViewList = FXCollections.observableArrayList();

    Names tempName = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameObjects.add(new Names("John Doe"));
        nameObjects.add(new Names("Jane Doe"));
        nameObjects.add(new Names("Jack Doe"));

        nameObjects.get(0).addVersion(new NameVersions("Version 2a"));
        nameObjects.get(0).addVersion(new NameVersions("Version 3a"));
        nameObjects.get(1).addVersion(new NameVersions("Version 2b"));
        nameObjects.get(2).addVersion(new NameVersions("Version 2c"));
        nameObjects.get(2).addVersion(new NameVersions("Version 3c"));
        nameObjects.get(2).addVersion(new NameVersions("Version 4c"));

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
                                }
                            } else {
                                if (selectedVersionsViewList.contains(n.getVersion())) {
                                    selectedVersionsViewList.remove(n.getVersion());
                                }
                            }
                        }
                    }));
                }

            }
        });

        selectedNames.setItems(selectedVersionsViewList);

    }

    //Returns to the main menu
    public void backButtonPressed(){
        Main.loadMainPage();
    }

}