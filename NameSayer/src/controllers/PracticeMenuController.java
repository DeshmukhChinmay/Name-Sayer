package controllers;


import javafx.application.Platform;

import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import main.Main;
import main.Names;
import main.Names.NameVersions;

public class PracticeMenuController {
    @FXML
    private Button recordButton;
    @FXML
    private Button listenButton;
    @FXML
    private Button compareButton;
    @FXML
    private Button saveButton;
    @FXML
    private ProgressBar progressBar;

    private String currentWorkingDir = System.getProperty("user.dir");
    private File fileName;
    private NameVersions nameVersion;

    public void compareToAudio() {



    }

    public void SaveAudio() throws IOException{

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime dateAndTime = LocalDateTime.now();
        fileName = new File(currentWorkingDir + "/NameSayer/PracticeNames/" + nameVersion.getParentName() + "_" + dateTimeFormatter.format(dateAndTime) + ".wav");
        Files.copy(new File(currentWorkingDir + "/NameSayer/Temp/tempAudio.wav").toPath(), fileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Main.getDatabaseMenuController().updateList();
        saveButton.setDisable(true);
        saveButton.setText("Saved!");
    }

    public void startRecording() {
        //Multi threading the recording
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                ProcessBuilder voiceRec = new ProcessBuilder("ffmpeg","-f","alsa","-ac","1","-ar","44100","-i","default","-t","5","tempAudio.wav");
                voiceRec.directory(new File(currentWorkingDir + "/NameSayer/Temp/"));
                voiceRec.start();
                return null;
            }
        };
        //Multi threaded the buttons to disable/enable each one accordingly after 5 seconds
        Task<Void> timer = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(5000);
                Platform.runLater(new Runnable() {
                    public void run() {
                        disableButtons(false);
                        recordButton.setText("Recorded!");
                        recordButton.setDisable(true);
                    }
                });
                return null;
            }
        };
        //Multi threaded the progress bar to show how long is left for recording
        Task<Void> update = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 1; i < 220; i++) {
                    Thread.sleep(20);
                    updateProgress(i, 220);
                }
                return null;
            }
        };
        progressBar.progressProperty().bind(update.progressProperty());
        new Thread(update).start();
        new Thread(timer).start();

        recordButton.setDisable(true);
        recordButton.setText("Recording...");
    }


    public void listenToAudio() {

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ProcessBuilder playProcess = new ProcessBuilder("ffplay","-autoexit","-nodisp",(currentWorkingDir + "/NameSayer/Temp/tempAudio.wav"));
                playProcess.start();
                return null;
            }
        };
        new Thread(task).start();

        task.setOnSucceeded(Event -> {
            System.out.println("Audio Playback Finished");
        });

    }

    public void setNameVersion(NameVersions version) {
        this.nameVersion = version;
    }

    public void disableButtons(boolean selector){
        saveButton.setDisable(selector);
        listenButton.setDisable(selector);
        compareButton.setDisable(selector);
    }
    public void goBackButton() {
        Main.loadPlayPage();
        recordButton.setText("Record");
        saveButton.setText("Save");
        recordButton.setDisable(false);
        disableButtons(true);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
    }
}
