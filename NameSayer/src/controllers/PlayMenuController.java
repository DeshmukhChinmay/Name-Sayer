package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import main.Main;
import main.Names;
import main.Names.NameVersions;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PlayMenuController implements Initializable {
    @FXML
    private ToggleButton qualityButton;

    private ListMenuController listMenuController;

    public ListView<NameVersions> selectedListView;

    private ObservableList<NameVersions> selectedVersionList;
    private NameVersions currentSelection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
                getQualityRating(currentSelection);

            }
        });

    }

    public void getQualityRating(NameVersions version){
        if (version.getBadQuality().get()){
            qualityButton.setSelected(true);
            qualityButton.setText("Bad Quality");
        }
        else{
            qualityButton.setSelected(false);
            qualityButton.setText("Good Quality");

        }
    }
    public void setQualityRating(){
            //Gets the name of the currently playing name
            //and sets the quality good or bad
            if(qualityButton.isSelected()){
                qualityButton.setText("Bad Quality");
                currentSelection.getBadQuality().setValue(true);
                //Add functionality for selected file to be bad quality
            }
            else{
                qualityButton.setText("Good Quality");
                currentSelection.getBadQuality().setValue(false);
                //Add functionality for selected file to be Good quality
            }

        }
        public void setListMenuController(ListMenuController listMenuController){
            this.listMenuController = listMenuController;
        }
         //Changes scene to where the list view of all creations are shown
        public void backButtonPressed() {
            Main.loadListPage();
        }

        public void practiceButtonPressed(){
            Main.loadPracticePage();

        }
}

