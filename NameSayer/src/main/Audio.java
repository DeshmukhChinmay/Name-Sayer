package main;

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

//    public Task normaliseAudioAndRemoveSilence(File audio) {
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                String normaliseCommand = "ffmpeg -i " + audio.getAbsolutePath() + " -af silenceremove=1:0:-50dB " + currentWorkingDir + "/NameSayer/Temp/silenceRemoved.wav;";
//                ProcessBuilder normaliseBuilder = new ProcessBuilder("/bin/bash","-c",normaliseCommand);
//                Process normaliseProcess = normaliseBuilder.start();
//                normaliseProcess.waitFor();
//                String removeSilenceCommand = "ffmpeg -i " + currentWorkingDir + "/NameSayer/Temp/silenceRemoved.wav -filter:a loudnorm " + currentWorkingDir + "/NameSayer/Temp/finalAudio.wav";
//                ProcessBuilder silenceBuilder = new ProcessBuilder("/bin/bash","-c",removeSilenceCommand);
//                Process silenceProcess = silenceBuilder.start();
//                silenceProcess.waitFor();
//                return null;
//            }
//        };
//        return task;
//    }

    public Task normaliseAudio(File audio) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String normaliseCommand = "ffmpeg -i " + audio.getAbsolutePath() + " -filter:a loudnorm " + currentWorkingDir + "/NameSayer/Temp/normalisedAudio.wav";
                ProcessBuilder normaliseBuilder = new ProcessBuilder("/bin/bash","-c",normaliseCommand);
                Process normaliseProcess = normaliseBuilder.start();
                normaliseProcess.waitFor();
                return null;
            }
        };
        return task;
    }

    public Task removeSilence() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String removeSilenceCommand = "ffmpeg -i " + currentWorkingDir + "/NameSayer/Temp/normalisedAudio.wav -af silenceremove=1:0:-50dB " + currentWorkingDir + "/NameSayer/Temp/finalAudio.wav";
                ProcessBuilder silenceBuilder = new ProcessBuilder("/bin/bash","-c",removeSilenceCommand);
                Process silenceProcess = silenceBuilder.start();
                silenceProcess.waitFor();
                return null;
            }
        };
        return task;
    }



}