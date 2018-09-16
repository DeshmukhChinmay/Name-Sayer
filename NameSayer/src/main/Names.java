package main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class Names {

    private String name;
    private ObservableList<String> versions = FXCollections.observableArrayList();
    private BooleanProperty state = new SimpleBooleanProperty();

    public Names(String name) {
        this.name = name;
        versions.add("Version 1");
        state.setValue(false);
    }

    public String getName() {
        return name;
    }

    public void addVersion(String version) {
        versions.add(version);
    }

    public ObservableList<String> getVersions() {
        return versions;
    }

    public BooleanProperty getState() {
        return state;
    }

}