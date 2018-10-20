package main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.LinkedList;

// Class for storing the audioPaths for a name/concatenated that has to be played
public class PlayableNames {

    private String name;
    private BooleanProperty badQuality = new SimpleBooleanProperty(false);
    // Storing the audioPaths for multiple names in a list
    private LinkedList<String> audioPath;

    public PlayableNames(String name, LinkedList<String> audioPath) {
        this.name = name;
        this.audioPath = audioPath;
        this.badQuality.setValue(false);
    }

    public String getName() {
        return name;
    }

    public LinkedList<String> getAudioPath() {
        return audioPath;
    }

    public BooleanProperty getBadQuality() {
        return badQuality;
    }

}
