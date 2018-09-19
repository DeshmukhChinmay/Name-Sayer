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
        String tempVersionName = name + " (" + (versions.size() + 1) + ")";
        versions.add(new NameVersions(tempVersionName, ""));
    }

    public String getName() {
        return name;
    }

    public void addVersion(String name) {
        String tempVersionName = name + " (" + (versions.size() + 1) + ")";
        versions.add(new NameVersions(tempVersionName, ""));
    }

    public ObservableList<NameVersions> getVersions() {
        return versions;
    }

    public static class NameVersions {

        String version;
        String audioPath;
        BooleanProperty selected = new SimpleBooleanProperty(false);
        BooleanProperty badQuality = new SimpleBooleanProperty(false);

        public NameVersions(String version, String audioPath) {
            this.version = version;
            this.audioPath = audioPath;
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

        public BooleanProperty getBadQuality() {
            return badQuality;
        }

    }

}
