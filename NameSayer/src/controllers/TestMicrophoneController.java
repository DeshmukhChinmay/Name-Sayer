package controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import main.Main;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class TestMicrophoneController{

    @FXML
    ProgressBar micVolume;

    @FXML
    public void initialize(){
        openMicLine();
    }

    public static boolean startTest = true;
    private int sound = 0;
    private byte[] audioData;
    TargetDataLine line;

    private void openMicLine(){
        AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false);
        final int bufferByteSize = 2048;

        try {
            line = AudioSystem.getTargetDataLine(fmt);
            line.open(fmt, bufferByteSize);
        } catch(LineUnavailableException e) {
            System.err.println(e);
            return;
        }

        audioData = new byte[bufferByteSize];
        line.start();
    }

    private int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for(int i=0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for(int j=0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }

    public void testMic() {
        openMicLine();
        startTest = true;
        Task<Void> updateMicBar = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (startTest) {
                    if (line.read(audioData, 0, audioData.length) > 0) {
                        sound = calculateRMSLevel(audioData);
                        updateProgress(sound, 80);
                    }
                }
                line.close();
                return null;
            }
        };
        micVolume.progressProperty().bind(updateMicBar.progressProperty());
        new Thread(updateMicBar).start();
    }

    public void backButtonPressed(){
        startTest = false;
        micVolume.progressProperty().unbind();
        Main.loadMainPage();
    }
}