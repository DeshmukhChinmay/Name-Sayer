package main;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.File;

public class Audio {
    private static Audio ourInstance = new Audio();

    public static Audio getInstance() {
        return ourInstance;
    }

    //Method that returns a task that plays the selected audio format using Linux command ffplay by using a Processbuilder
    public Task playAudio(Names.NameVersions nameVersions) {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ProcessBuilder playProcess = new ProcessBuilder("ffplay", "-autoexit", "-nodisp", nameVersions.getAudioPath());
                Process process = playProcess.start();
                process.waitFor();
                return null;
            }
        };
        return task;


    }

    //Method that returns a task that plays the user recorded audio first and then the database name after to comapre
    //the 2 audio by using processbuilder and linux ffplay command
    public Task comparePracticeThenDatabase(Names.NameVersions databaseName) {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ProcessBuilder playPracticeProcess = new ProcessBuilder("ffplay", "-autoexit", "-nodisp", "tempAudio.wav");
                playPracticeProcess.directory(new File("./NameSayer/Temp/"));
                Process process = playPracticeProcess.start();
                process.waitFor();
                ProcessBuilder playDatabaseName = new ProcessBuilder("ffplay", "-autoexit", "-nodisp", databaseName.getAudioPath());
                Process secondProcess = playDatabaseName.start();
                secondProcess.waitFor();
                return null;
            }
        };
        return task;
    }


}
