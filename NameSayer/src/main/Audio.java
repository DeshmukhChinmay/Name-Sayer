package main;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

public class Audio {
    private static Audio ourInstance = new Audio();

    private String currentWorkingDir = System.getProperty("user.dir");

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

    public double getWavFileLength(File file) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        long audioFileLength = file.length();
        int frameSize = format.getFrameSize();
        double frameRate = format.getFrameRate();
        return Math.round((audioFileLength / (frameSize * frameRate)) * 100.0) / 100.0;
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

    //// -----------------------------------------------------
    //// TEST THIS CODE IF POSSIBLE



    public Service<Void> normaliseAndRemoveSilence(File audio) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String removeSilenceCommand = "ffmpeg -y -i " + audio.getAbsolutePath() + " -af silenceremove=1:0:-48dB " + currentWorkingDir + "/NameSayer/Temp/silenceRemoved.wav";
                        ProcessBuilder silenceBuilder = new ProcessBuilder("/bin/bash", "-c", removeSilenceCommand);
                        Process silenceProcess = silenceBuilder.start();
                        silenceProcess.waitFor();

                        String normaliseCommand = "ffmpeg -y -i " + currentWorkingDir + "/NameSayer/Temp/silenceRemoved.wav" + " -filter:a loudnorm " + currentWorkingDir + "/NameSayer/Temp/finalAudio.wav";
                        ProcessBuilder normaliseBuilder = new ProcessBuilder("/bin/bash", "-c", normaliseCommand);
                        Process normaliseProcess = normaliseBuilder.start();
                        normaliseProcess.waitFor();

                        return null;
                    }
                };
            }
        };

        return service;
    }



    //// -----------------------------------------------------

}
