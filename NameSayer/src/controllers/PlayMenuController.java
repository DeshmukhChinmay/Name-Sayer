package controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.BadAudioText;
import main.Main;
import main.Names.NameVersions;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class PlayMenuController implements Initializable {
    @FXML
    private ToggleButton qualityButton;
    @FXML
    private Button playButton;
    @FXML
    private ToggleButton shuffleButton;
    @FXML
    public Button nextButton;
    @FXML
    public Button prevButton;

    private ListMenuController listMenuController;
    public ListView<NameVersions> selectedListView;

    private ObservableList<NameVersions> selectedVersionList;
    private NameVersions currentSelection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prevButton.setDisable(true);//Makes it so that prevButton is always disabled
        setListMenuController(Main.getListMenuController());
        selectedVersionList = listMenuController.getSelectedVersionObjects();
        selectedListView.setCellFactory(param -> new ListCell<NameVersions>() {

            @Override
            protected void updateItem(NameVersions version, boolean empty) {
                super.updateItem(version, empty);

                if (empty || version == null || version.getVersion() == null) {
                    setText(null);
                } else {
                    setText(version.getVersion());
                }
            }

        });

        selectedListView.setItems(selectedVersionList);

        selectedListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NameVersions>() {
            @Override
            public void changed(ObservableValue<? extends NameVersions> observable, NameVersions oldValue, NameVersions newValue) {
                currentSelection = newValue;
                if (currentSelection != null) {
                    getQualityRating(currentSelection);
                }
            }
        });
    }

    public void getQualityRating(NameVersions version) {
        if (version.getBadQuality().get()) {
            qualityButton.setSelected(true);
            qualityButton.setText("Bad Quality");

        } else {
            qualityButton.setSelected(false);
            qualityButton.setText("Good Quality");
        }
    }

    public void setQualityRating() throws IOException {
        if (currentSelection == null) {
            if (qualityButton.isSelected()) {
                qualityButton.setText("Bad Quality");
            } else {
                qualityButton.setText("Good Quality");
            }
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Name Selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please Select a Name");
            errorAlert.showAndWait();
        } else {
            //Gets the name of the currently playing name
            //and sets the quality good or bad
            if (qualityButton.isSelected()) {
                qualityButton.setText("Bad Quality");
                currentSelection.getBadQuality().setValue(true);
                //Writes the current selected name to the text file
                BadAudioText.getInstance().writeText(currentSelection.getVersion());
            } else {
                qualityButton.setText("Good Quality");
                //Sets value of bad quality name object to false
                currentSelection.getBadQuality().setValue(false);
                //Removes text fr0m file
                BadAudioText.getInstance().removeTextFromFile(currentSelection.getVersion());
            }
        }
    }

    public void setListMenuController(ListMenuController listMenuController) {
        this.listMenuController = listMenuController;
    }

    public void assignCurrentSelection() {
        currentSelection = selectedVersionList.get(0);
        selectedListView.getSelectionModel().selectFirst();
    }

    //Changes scene to where the list view of all creations are shown
    public void backButtonPressed() {
        prevButton.setDisable(true);//Makes it so that prevButton is always disabled
        nextButton.setDisable(false);
        Main.loadListPage();
    }

    public void practiceButtonPressed() {
        Main.getPracticeMenuController().setNameVersion(selectedListView.getSelectionModel().getSelectedItem());
        Main.loadPracticePage();
    }

    public void playButtonPressed() {
        playButton.setText("Playing");
        playButton.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                    ProcessBuilder playProcess = new ProcessBuilder("ffplay","-autoexit","-nodisp",currentSelection.getAudioPath());
                    playProcess.start();
                return null;
            }
        };
        new Thread(task).start();
    }

    public void shuffleButtonPressed() {
        Collections.shuffle(selectedVersionList);
    }

    public void nextButtonPressed() {
        prevButton.setDisable(false);//If next button is pressed that means prev Button will be enabled
        selectedListView.getSelectionModel().selectNext();
        System.out.println(currentSelection.getVersion());
        if (currentSelection == selectedListView.getItems().get(selectedListView.getItems().size() - 1)) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    }

    public void prevButtonPressed() {
        nextButton.setDisable(false);//If prev button is pressable that means that next button will be enabled
        selectedListView.getSelectionModel().selectPrevious();
        System.out.println(currentSelection.getVersion());
        if (currentSelection == selectedListView.getItems().get(0)) {
            prevButton.setDisable(true);
        } else {
            prevButton.setDisable(false);
        }
    }

}

