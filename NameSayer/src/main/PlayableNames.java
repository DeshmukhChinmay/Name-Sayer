package main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PlayableNames {

    private String name;
    private String audioPath;
    private BooleanProperty badQuality = new SimpleBooleanProperty(false);
    private boolean fileAdjusted;

    public PlayableNames(String name, String audioPath) {
        this.name = name;
        this.audioPath = audioPath;
        this.badQuality.setValue(false);
        this.fileAdjusted = false;
    }

    public String getName() {
        return name;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public BooleanProperty getBadQuality() {
        return badQuality;
    }

    public boolean isFileAdjusted() {
        return fileAdjusted;
    }

    public void setFileAdjusted(boolean fileAdjusted) {
        this.fileAdjusted = fileAdjusted;
    }

}
