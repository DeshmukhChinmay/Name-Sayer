package controllers;

import main.Main;

public class listMenuController {

    //Returns to the main menu
    public void backButtonPressed(){
        Main.loadMainPage();
    }

    //Goes to the play menu
    public void playButtonPressed(){
    Main.loadPlayPage();
    }
}