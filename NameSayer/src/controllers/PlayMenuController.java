package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import main.*;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class PlayMenuController implements Initializable {

    @FXML
    private ToggleButton qualityButton;
    @FXML
    public Button playButton;
    @FXML
    public Button practiceButton;
    @FXML
    public Button nextButton;
    @FXML
    public Button prevButton;

    public ListView<PlayableNames> selectedListView;
    private ObservableList<PlayableNames> selectedVersionList;

    private PlayableNames currentSelection;

    public boolean single;
    public boolean fromUpload;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Disabling the previous button when initializing the scene
        prevButton.setDisable(true);

        // Setting the cell of the selectedListView to a custom cell so that custom text is displayed
        selectedListView.setCellFactory(param -> new ListCell<PlayableNames>() {

            @Override
            protected void updateItem(PlayableNames name, boolean empty) {
                super.updateItem(name, empty);

                if (empty || name == null || name.getName() == null) {
                    setText(null);
                } else {
                    setText(name.getName());
                }
            }

        });

        selectedListView.setItems(selectedVersionList);

        // Adding a listener to the selections from selectedListView. Other buttons are disable/enabled appropriately
        // depending on the selection
        selectedListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlayableNames>() {
            @Override
            public void changed(ObservableValue<? extends PlayableNames> observable, PlayableNames oldValue, PlayableNames newValue) {
                currentSelection = newValue;
                if (fromUpload) {
                    if (currentSelection != null) {
                        playButton.setDisable(false);
                        practiceButton.setDisable(false);
                        checkLogicOfCycleButton();
                    }
                } else {
                    if (currentSelection != null) {
                        getQualityRating(currentSelection);
                        playButton.setDisable(false);
                        practiceButton.setDisable(false);
                        checkLogicOfCycleButton();
                    }
                }

            }
        });
    }

    //Sets the quality rating on the UI screen whenever a name is selected
    public void getQualityRating(PlayableNames name) {
        if (name.getBadQuality().get()) {
            qualityButton.setSelected(true);
            qualityButton.setText("Set As Good Quality");

        } else {
            qualityButton.setSelected(false);
            qualityButton.setText("Set As Bad Quality");
        }
    }

    //Sets the quality on the UI screen when the toggle button is toggled
    public void setQualityRating() throws IOException {
        if (currentSelection == null) {
            if(qualityButton.isSelected()) {
                qualityButton.setText("Set As Good Quality");
            }
            else{
                qualityButton.setText("Set As Bad Quality");

                }
            new ErrorAlerts().showError("No Names Selected","Please Select a Name");

        }
            else {
            //Gets the name of the currently playing name
            //and sets the quality good or bad
            if (qualityButton.isSelected()) {
                qualityButton.setText("Set As Good Quality");
                currentSelection.getBadQuality().setValue(true);
                SceneChanger.getListMenuController().getNameVersionsMap().get(currentSelection.getName()).getBadQuality().setValue(true);
                //Writes the current selected name to the text file
                BadAudioText.getInstance().writeText(currentSelection.getName());
            } else {
                qualityButton.setText("Set As Bad Quality");
                //Sets value of bad quality name object to false
                currentSelection.getBadQuality().setValue(false);
                SceneChanger.getListMenuController().getNameVersionsMap().get(currentSelection.getName()).getBadQuality().setValue(false);
                //Removes text fr0m file
                BadAudioText.getInstance().removeTextFromFile(currentSelection.getName());
            }
        }
    }

    //Changes scene to where the list view of all recordings are shown
    public void backButtonPressed() {
        prevButton.setDisable(true);//Makes it so that prevButton is always disabled
        nextButton.setDisable(false);
        SceneChanger.getListMenuController().searchFunction();
        single = false;
        selectedListView.getSelectionModel().clearSelection();
        if (fromUpload) {
            SceneChanger.loadUploadSearchPage();
            SceneChanger.getEnterNamesMenuController().showSearchList();
        } else {
            SceneChanger.loadListPage();

        }
    }

    //Changes to a different stage where a practice name can be recorded
    public void practiceButtonPressed() {
        SceneChanger.getPracticeMenuController().setPlayableName(selectedListView.getSelectionModel().getSelectedItem());
        SceneChanger.loadPracticePage();
    }

    // Plays the selected name from the list
    public void playButtonPressed() {
        playButton.setText("Playing");
        playButton.setDisable(true);
        prevButton.setDisable(true);
        nextButton.setDisable(true);
        Task task = Audio.getInstance().playAudio(currentSelection);
        task.setOnSucceeded(e -> {
            playButton.setDisable(false);
            playButton.setText("Play");
            checkLogicOfCycleButton();
        });
        new Thread(task).start();
    }

    //Shuffles the order of the names to be played
    public void shuffleButtonPressed() {
        Collections.shuffle(selectedVersionList);
        if (currentSelection != null) {
            checkLogicOfCycleButton();
        }
    }

    // Changing toggle buttons visibility depending on which screen the user comes from
    public void toggleQualityButtonVisibility() {
        if (fromUpload) {
            qualityButton.setVisible(false);
        } else {
            qualityButton.setVisible(true);
        }
    }

    // Selecting the next selection in the listview
    public void nextButtonPressed() {
        prevButton.setDisable(false);//If next button is pressed that means prev Button will be enabled
        selectedListView.getSelectionModel().selectNext();

        if (currentSelection == selectedListView.getItems().get(selectedListView.getItems().size() - 1)) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    }

    // Selecting the previous selection in the listview
    public void prevButtonPressed() {
        nextButton.setDisable(false);//If prev button is pressable that means that next button will be enabled
        selectedListView.getSelectionModel().selectPrevious(); //Selects the prev name in the list

        //If new selection is the first creation then it should disable prev button
        if (currentSelection == selectedListView.getItems().get(0)) {
            prevButton.setDisable(true);
        } else {
            prevButton.setDisable(false);
        }
    }

    //Checks when to disable/enable next and previous buttons
    public void checkLogicOfCycleButton() {
        if (single) {
            nextButton.setDisable(true);
            prevButton.setDisable(true);
        } else if (currentSelection == selectedListView.getItems().get(selectedListView.getItems().size() - 1)) {
            nextButton.setDisable(true);
            prevButton.setDisable(false);
        } else if (currentSelection == selectedListView.getItems().get(0)) {
            nextButton.setDisable(false);
            prevButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
            prevButton.setDisable(false);
        }
    }

    public void setFromUpload(boolean condition) {
        fromUpload = condition;
        setDisplayList();
    }

    // Setting the list to be displayed for playing
    public void setDisplayList() {
        if (fromUpload) {
            selectedVersionList = SceneChanger.getEnterNamesMenuController().getPlayableNamesObjects();
        } else {
            selectedVersionList = SceneChanger.getListMenuController().getPlayableNamesObjects();
        }
        selectedListView.setItems(selectedVersionList);
    }

}

