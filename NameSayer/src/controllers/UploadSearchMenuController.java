package controllers;

import main.SceneChanger;

public class UploadSearchMenuController {

    public void backButtonPressed(){
        SceneChanger.loadMainPage();
    }

    public void nextButtonPressed(){
        SceneChanger.loadPlayPage();
        SceneChanger.getPlayMenuController().setFromUpload(true);
    }
}
