package main;

import javafx.concurrent.Task;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

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
                //Checks if the file normalized and if the silence has been removed
                if(!nameVersions.isFileAdjusted()) {
                    normalizeAndCutSilence(nameVersions);
                }

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

        private void normalizeAndCutSilence(Names.NameVersions nameVersions) throws Exception{
            String removeSilenceCommand = "ffmpeg -y -i " + nameVersions.getAudioPath() + " -af silenceremove=1:0:-48dB " + nameVersions.getAudioPath();
            ProcessBuilder silenceBuilder = new ProcessBuilder("/bin/bash", "-c", removeSilenceCommand);
            Process silenceProcess = silenceBuilder.start();
            silenceProcess.waitFor();

            String normaliseCommand = "ffmpeg -y -i " + nameVersions.getAudioPath() + " -filter:a loudnorm " + nameVersions.getAudioPath();
            ProcessBuilder normaliseBuilder = new ProcessBuilder("/bin/bash", "-c", normaliseCommand);
            Process normaliseProcess = normaliseBuilder.start();
            normaliseProcess.waitFor();
            nameVersions.setFileAdjusted(true);
        }


}
