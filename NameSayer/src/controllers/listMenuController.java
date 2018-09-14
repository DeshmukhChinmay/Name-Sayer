package controllers;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import main.Main;
import main.Names;

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

        for (Names n: nameObjects) {
            namesViewList.add(n.getName());
        }

        namesList.setItems(namesViewList);

    }

    //Returns to the main menu
    public void backButtonPressed(){
        Main.loadMainPage();
    }



}
