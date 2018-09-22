package main;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.File;

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

    public Task comparePracticeThenDatabase(Names.NameVersions databaseName){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ProcessBuilder playPracticeProcess = new ProcessBuilder("ffplay","-autoexit","-nodisp","tempAudio.wav");
                playPracticeProcess.directory(new File("./NameSayer/Temp/"));
                Process process =  playPracticeProcess.start();
                process.waitFor();
                ProcessBuilder playDatabaseName = new ProcessBuilder("ffplay","-autoexit","-nodisp",databaseName.getAudioPath());
                Process secondProcess =  playDatabaseName.start();
                secondProcess.waitFor();
                return null;
            }
        };
        return task;
    }


}
