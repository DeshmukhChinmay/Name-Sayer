package controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.util.Callback;
import main.Main;
import main.Names;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class listMenuController implements Initializable {

    public ListView namesList;
    public ListView namesVersion;
    public ListView selectedNames;

    private LinkedList<Names> nameObjects = new LinkedList<>();
    final private ObservableList<String> namesViewList = FXCollections.observableArrayList();
    final private ObservableList<String> versionsViewList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameObjects.add(new Names("John Doe"));
        nameObjects.add(new Names("Jane Doe"));
        nameObjects.add(new Names("Jack Doe"));

        nameObjects.get(0).addVersion("Version 2");
        nameObjects.get(0).addVersion("Version 3");
        nameObjects.get(1).addVersion("Version 2");
        nameObjects.get(2).addVersion("Version 2");
        nameObjects.get(2).addVersion("Version 3");
        nameObjects.get(2).addVersion("Version 4");

        for (Names n: nameObjects) {
            namesViewList.add(n.getName());
        }

        namesList.setItems(namesViewList);
        namesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectedNames.setItems(versionsViewList);

        namesList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                Names tempName = null;

                for (Names n: nameObjects) {
                    if (n.getName().equals(namesList.getSelectionModel().getSelectedItem())) {
                        tempName = n;
                    }
                }

                if (tempName != null) {
                    namesVersion.setItems(tempName.getVersions());
                }

            }
        });

    }

    //Returns to the main menu
    public void backButtonPressed(){
        Main.loadMainPage();
    }



}
