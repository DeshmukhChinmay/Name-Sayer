package main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class Names {

    private String name;
    private int count;
    final private ObservableList<NameVersions> versions = FXCollections.observableArrayList();

    public Names(String name, String audioPath) {
        this.name = name;
        count++;
        String tempVersionName = name + " (" + Integer.toString(count) + ")";
        versions.add(new NameVersions(tempVersionName, audioPath));
    }

    public String getName() {
        return name;
    }

    public void addVersion(String name, String audioPath) {
        String tempVersionName = name + " (" + (versions.size() + 1) + ")";
        versions.add(new NameVersions(tempVersionName, audioPath));
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

        public String getAudioPath() {
            return audioPath;
        }

    }

}
