package controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Main;
import main.SceneChanger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SettingsMenuController {

    @FXML
    ProgressBar micVolume;
    @FXML
    Button testButton;

    private static boolean startTest = true;
    private int sound = 0;
    private byte[] audioData;
    private TargetDataLine line;

    //Opens a mic line to enable the line to be read
    private void openMicLine() {
        micVolume.progressProperty().setValue(0);
        AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false);
        final int bufferByteSize = 2048;
        try {
            line = AudioSystem.getTargetDataLine(fmt);
            line.open(fmt, bufferByteSize);
        } catch (LineUnavailableException e) {
            System.err.println(e);
            return;
        }

        audioData = new byte[bufferByteSize];
        line.start();
    }

    //This code was found from https://stackoverflow.com/questions/3899585/microphone-level-in-java
    private int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for (int j = 0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }

    //Opens the mic line and then continuously reads the input until the window is exited and then also displays it in a
    //progressbar
    public void testMic() {
        testButton.setDisable(true);
        openMicLine();
        startTest = true;
        Task<Void> updateMicBar = new Task<Void>() {
            @Override
            public Void call() {
                while (startTest) {
                    if (line.read(audioData, 0, audioData.length) > 0) {
                        sound = calculateRMSLevel(audioData);
                        updateProgress(sound, 110);
                    }
                }
                line.close();
                return null;
            }
        };
        micVolume.progressProperty().bind(updateMicBar.progressProperty());
        new Thread(updateMicBar).start();
    }

    //When back button is pressed disables the while loop to read the mic input and changes scene back to Main menu
    public void backButtonPressed() {
        testButton.setDisable(false);
        startTest = false;
        micVolume.progressProperty().unbind();
        //resets property
        micVolume.progressProperty().setValue(0);
        SceneChanger.loadMainPage();
    }
    //Allows user to choose another database to add to the current one
    public void onAddDatabase() {
        DirectoryChooser databaseChooser = new DirectoryChooser(); //Creates a new stage and opens a directory chooser window
        File databaseDirectory = databaseChooser.showDialog(new Stage());

        if (databaseDirectory != null) {
            Main.setDatabaseFolder(databaseDirectory.getAbsoluteFile());
            try {
                SceneChanger.getListMenuController().reinitialiseAll(); //initialises the new database folder
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                errorAlert.setTitle("Failed to initialise new DatabaseFolder");
                errorAlert.setHeaderText(null);
                errorAlert.setHeaderText("Please select new Database Folder");
                errorAlert.showAndWait();
            }
        }
    }
    //Opens the github wiki page when help button is pressed
    public void onHelpButtonPressed() {
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/DeshmukhChinmay/NameSayer/wiki").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
