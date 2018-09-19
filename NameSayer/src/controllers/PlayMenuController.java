package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    public ListView<NameVersions> selectedListView;

    private LinkedList<NameVersions> selectedVersionList = new LinkedList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }

    //Changes scene to where the list view of all creations are shown
        public void backButtonPressed() {

            Main.loadListPage();
        }

        public void practiceButtonPressed(){
            Main.loadPracticePage();

        }
        public void getQualityRating(Names.NameVersions name){
            if (name.getBadQuality().get()){
                qualityButton.setSelected(true);
            }
            else{
                qualityButton.setSelected(false);
            }

        }

        public void qualityRatingSelected(){
//            Gets the name of the currently playing name
//            and sets the quality good or bad
            if(qualityButton.isSelected()){
                qualityButton.setText("Bad Quality");
                //Add functionality for selected file to be bad quality
            }
            else{
                qualityButton.setText("Good Quality");
                //Add functionality for selected file to be Good quality
            }

        }

}

