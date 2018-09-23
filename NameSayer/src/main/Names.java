package main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Names {

    private String name;
    private int count;
    // Storing all of the versions for a name in an ObservableList
    final private ObservableList<NameVersions> versions = FXCollections.observableArrayList();

    public Names(String name, String audioPath) {
        this.name = name;
        count++;
        String tempVersionName = name + " (" + Integer.toString(count) + ")";
        versions.add(new NameVersions(this, tempVersionName, audioPath));
    }

    public String getName() {
        return name;
    }

    public void addVersion(String name, String audioPath) {
        String tempVersionName = name + " (" + (versions.size() + 1) + ")";
        versions.add(new NameVersions(this, tempVersionName, audioPath));
    }

    public ObservableList<NameVersions> getVersions() {
        return versions;
    }

    public static class NameVersions {

        // Fields for the versions include the versions quality and the path of the audio file
        String parentName;
        String version;
        String audioPath;
        BooleanProperty selected = new SimpleBooleanProperty(false);
        BooleanProperty badQuality = new SimpleBooleanProperty(false);

        public NameVersions(Names parentName, String version, String audioPath) {
            this.parentName = parentName.getName();
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

        public String getParentName() {
            return parentName;
        }

        public BooleanProperty getBadQuality() {
            return badQuality;
        }

        public String getAudioPath() {
            return audioPath;
        }

    }

}
