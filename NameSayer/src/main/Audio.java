package main;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

public class Audio {
    private static Audio ourInstance = new Audio();

    public static Audio getInstance() {
        return ourInstance;
    }

    public void playAudio(Button button, Names.NameVersions nameVersions) {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ProcessBuilder playProcess = new ProcessBuilder("ffplay","-autoexit","-nodisp",nameVersions.getAudioPath());
                Process process =  playProcess.start();
                while(process.isAlive()){
                    button.setDisable(true);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            button.setDisable(false);
            button.setText("Play");
        });
        new Thread(task).start();

    }

}
