package controllers;


import javafx.application.Platform;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import main.Main;

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

    public void compareToAudio() {
    }

    public void SaveAudio() {
        saveButton.setDisable(true);
        saveButton.setText("Saved!");
    }

    public void startRecording() {
        //Multi threading the recording
//        Task<Void> task = new Task<Void>() {
//            @Override
//            public Void call() throws Exception {
//                ProcessBuilder voiceRec = new ProcessBuilder("ffmpeg","-f","alsa","-ac","1","-ar","44100","-i","default","-t","5",namePass.getName()+"-voice.wav");
//                voiceRec.directory(new File("/"+System.getProperty("user.dir")+"/Creations"));
//                voiceRec.start();
//                return null;
//            }
//        };
        //Multi threaded the buttons to disable/enable each one accordingly after 5 seconds
        Task<Void> timer = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(5000);
                Platform.runLater(new Runnable() {
                    public void run() {
                        saveButton.setDisable(false);
                        listenButton.setDisable(false);
                        compareButton.setDisable(false);
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
//        if(_mediaPlayer!=null) {
//            _mediaPlayer.stop();
//            _mediaPlayer= null;
//        }
        recordButton.setDisable(true);
        recordButton.setText("Recording...");
    }


    public void listenToAudio() {

    }

    public void goBackButton() {
        Main.loadPlayPage();
        recordButton.setDisable(false);
        recordButton.setText("Record");
        saveButton.setText("Save");
        saveButton.setDisable(true);
        listenButton.setDisable(true);
        compareButton.setDisable(true);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
    }
}
