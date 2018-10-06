package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import main.Audio;
import main.Names.NameVersions;
import main.SceneChanger;

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
    @FXML
    private Button backButton;


    private Thread recordingThread;
    private Thread updateThread;
    private String currentWorkingDir = System.getProperty("user.dir");
    private File fileName;
    private NameVersions nameVersion;
    private boolean stop = false;
    private boolean recording = false;

    private boolean fileSaved = false;

    //Compares user recorded audio with that of the database audio
    //User recorded audio first and then database audio plays
    public void compareToAudio() {
        Task task = Audio.getInstance().comparePracticeThenDatabase(nameVersion);//Gets the task from Audio class
        listenButton.setDisable(true);
        backButton.setDisable(true);
        compareButton.setDisable(true);
        compareButton.setText("Playing");
        task.setOnSucceeded(e -> { //Once tasks finished then it should re enable buttons
            listenButton.setDisable(false);
            backButton.setDisable(false);
            compareButton.setDisable(false);
            if (!fileSaved) {
                saveButton.setDisable(false);
            }
            compareButton.setText("Compare");
        });
        new Thread(task).start();
    }

    //Saves the audio file into the practices folder
    public void SaveAudio() throws IOException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime dateAndTime = LocalDateTime.now();
        fileName = new File(currentWorkingDir + "/NameSayer/PracticeNames/" + nameVersion.getParentName() + "_" + dateTimeFormatter.format(dateAndTime) + ".wav");
        Files.copy(new File(currentWorkingDir + "/NameSayer/Temp/tempAudio.wav").toPath(), fileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
        SceneChanger.getDatabaseMenuController().updateList();
        saveButton.setDisable(true);
        saveButton.setText("Saved!");
        fileSaved = true;
    }

    //Creates a tempAudio.wav file that contains the user recording and also creates the progress bar for 5 seconds of recording
    public void startRecording() {

        if(recording){
            recording = false;
            stop = true;
            recordButton.setDisable(true);
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().setValue(220);
        }
        else {
            stop = false;
            //Multi threading the recording
            backButton.setDisable(true);
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    ProcessBuilder voiceRec = new ProcessBuilder("ffmpeg", "-f", "alsa", "-ac", "1", "-ar", "44100", "-i", "default", "-t", "5", "tempAudio.wav");
                    voiceRec.directory(new File(currentWorkingDir + "/NameSayer/Temp/"));
                    Process process = voiceRec.start();
                    while(process.isAlive()){
                        if(stop){
                            process.destroy();
                        }
                    }

                    Platform.runLater(new Runnable() {
                        public void run() {
                            buttonLogicRecord(false);
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
            //Binds progressbar to the update thread
            progressBar.progressProperty().bind(update.progressProperty());
            //Starts all 3 tasks on new threads
             recordingThread = new Thread(task);
             updateThread = new Thread(update);
             recordingThread.start();
             updateThread.start();
            backButton.setDisable(false);
            recordButton.setText("Stop");
            recordButton.setDisable(false);
            recording = true;
        }
    }

    //Method that listens to the users recorded attempt
    public void listenToAudio() {
        //Disables the required buttons on playing
        listenButton.setText("Playing");
        listenButton.setDisable(true);
        compareButton.setDisable(true);
        backButton.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                //Linux command ffplay to play the audio
                Audio.getInstance().normalizeAndCutSilenceOfUserRecording();
                ProcessBuilder playProcess = new ProcessBuilder("ffplay", "-autoexit", "-nodisp", (currentWorkingDir + "/NameSayer/Temp/tempAudio.wav"));
                Process process = playProcess.start();
                process.waitFor();
                return null;
            }
        };
        task.setOnSucceeded(Event -> {
            //Re enabled required button after task finishes running
            listenButton.setText("Listen");
            listenButton.setDisable(false);
            compareButton.setDisable(false);
            backButton.setDisable(false);
            if (!fileSaved) {
                saveButton.setDisable(false);
            }
        });
        new Thread(task).start();
    }

    public void setNameVersion(NameVersions version) {
        this.nameVersion = version;
    }

    //When the record button is pressed it follows this logic
    public void buttonLogicRecord(boolean selector) {
        saveButton.setDisable(selector);
        listenButton.setDisable(selector);
        compareButton.setDisable(selector);
    }

    //Method for when the back button is pressed. It should delete the tempAudio.wav file and then load tha play Page
    //and set all buttons into its default state.
    public void goBackButton() {
        File tempAudioFile = new File(currentWorkingDir + "/NameSayer/Temp/tempAudio.wav");
        if (tempAudioFile.exists()) {
            tempAudioFile.delete();
        }
        SceneChanger.loadPlayPage();
        recordButton.setText("Record");
        saveButton.setText("Save");
        recordButton.setDisable(false);
        buttonLogicRecord(true);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        recording = false;
    }
}
