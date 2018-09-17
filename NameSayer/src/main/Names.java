package main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Names {

    private String name;
    final private ObservableList<NameVersions> versions = FXCollections.observableArrayList();

    public Names(String name) {
        this.name = name;
        versions.add(new NameVersions("Version 1"));
    }

    public String getName() {
        return name;
    }

    public void addVersion(NameVersions version) {
        versions.add(version);
    }

    public ObservableList<NameVersions> getVersions() {
        return versions;
    }

    public static class NameVersions {

        String version;
        BooleanProperty selected = new SimpleBooleanProperty(false);
        BooleanProperty badQuality = new SimpleBooleanProperty(false);

        public NameVersions(String version) {
            this.version = version;
            selected.setValue(false);
            badQuality.setValue(false);
        }

        public String getVersion() {
            return version;
        }

        public BooleanProperty versionSelected() {
            return selected;
        }

        public boolean versionIsSelected() {
            return selected.get();
        }

        public void setVersionSelected(boolean selected) {
            this.selected.set(selected);
        }

        public BooleanProperty getBadQuality() {
            return badQuality;
        }

        public void setBadQuality(boolean badQuality) {
            this.badQuality.set(badQuality);
        }
    }

}