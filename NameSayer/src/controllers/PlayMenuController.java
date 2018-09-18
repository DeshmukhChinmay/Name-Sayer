package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import main.Main;
import main.Names;

public class PlayMenuController {
        @FXML
        private ToggleButton qualityButton;

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

