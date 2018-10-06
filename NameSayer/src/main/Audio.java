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
                normalizeAndCutSilenceOfUserRecording();
                ProcessBuilder playPracticeProcess = new ProcessBuilder("ffplay", "-autoexit", "-nodisp", "tempAudio.wav");
                playPracticeProcess.directory(new File("./NameSayer/Temp/"));
                Process process = playPracticeProcess.start();
                process.waitFor();
                ProcessBuilder playDatabaseName = new ProcessBuilder("ffplay", "-autoexit", "-nodisp", "finalWav.wav");
                playDatabaseName.directory(new File("./NameSayer/Temp/"));
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
        String removeSilenceCommand = "ffmpeg -y -i " + nameVersions.getAudioPath() + " -af silenceremove=1:0:-48dB " + currentWorkingDir + "/NameSayer/Temp/silenceRemoved.wav";
        ProcessBuilder silenceBuilder = new ProcessBuilder("/bin/bash", "-c", removeSilenceCommand);
        Process silenceProcess = silenceBuilder.start();
        silenceProcess.waitFor();

        String normaliseCommand = "ffmpeg -y -i silenceRemoved.wav -filter:a volume=5dB finalWav.wav";
        ProcessBuilder normaliseBuilder = new ProcessBuilder("/bin/bash", "-c", normaliseCommand);
        normaliseBuilder.directory(new File("./NameSayer/Temp/"));
        Process normaliseProcess = normaliseBuilder.start();
        normaliseProcess.waitFor();
        new File("./NameSayer/Temp/silenceRemoved.wav").delete();

    }

    public void normalizeAndCutSilenceOfUserRecording() throws Exception{
        String removeSilenceCommand = "ffmpeg -y -i tempAudio.wav -af silenceremove=1:0:-48dB tempAudio.wav";
        ProcessBuilder silenceBuilder = new ProcessBuilder("/bin/bash", "-c", removeSilenceCommand);
        silenceBuilder.directory(new File("./NameSayer/Temp/"));
        Process silenceProcess = silenceBuilder.start();
        silenceProcess.waitFor();

        String normaliseCommand = "ffmpeg -y -i tempAudio.wav -filter:a volume=5dB tempAudio.wav";
        ProcessBuilder normaliseBuilder = new ProcessBuilder("/bin/bash", "-c", normaliseCommand);
        normaliseBuilder.directory(new File("./NameSayer/Temp/"));
        Process normaliseProcess = normaliseBuilder.start();
        normaliseProcess.waitFor();
        new File("./NameSayer/Temp/silenceRemoved.wav").delete();

    }
}
