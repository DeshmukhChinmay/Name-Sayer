package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import main.Main;

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
//        public void getQualityRating(Names name){
//            if (name.getBadQuality()){
//                toggleButton.setSelected(true);
//            }
//            else{
//                toggleButton.setSelected(false);
//            }
//
//        }

        public void qualityRatingSelected(){
//            Gets the name of the currently playing name
//            and sets the quality good or bad
            if(qualityButton.isSelected()){
                qualityButton.setText("Bad Quality");
            }
            else{
                qualityButton.setText("Good Quality");
            }

        }

}

