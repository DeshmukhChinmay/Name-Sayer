# NameSayer
SOFTENG206 Assignment 3 and Project

NameSayer is an application that helps users with the pronunciation of names.

---
### Setup:
Open the terminal and cd to directory with the jar file. Make sure that there is a folder called 'names' which contains the database names in this directory. Then enter `java -jar NameSayer.jar` to execute the jar file.

---
### Instructions to Use:
1. Click on "Listen to Name(s)" to view all names in the database and then select the versions of the names you want to practice and click next. 'Clear' button can be clicked to clear the list of selected names.
2. Select the name and press "Play" to listen to the database recording. Press shuffle to randomise the play order. To select the next name in the list either click or press "Previous" or "Next".
3. If Recording is bad quality toggle the quality button and it will mark it as bad quality in "Bad_Recordings.txt" file.
4. To practice a name click on "Practice Name" and it will show a pop up window that will contain the following
```       
          1) Record your attempt
          2) Listen to attempt
          3) Compare Attempt*
          4) Save Attempt
```
5. To access all previous practices press the respective "Show Previous Practices" button on the main menu. This will then show you all the previously saved practices.
6. If you click on a practice name you can see all the different versions of the database name and play both with the respective buttons. you can also delete the user attempts with the delete button.
7.  To search for a name, click on the Upload/Search name from the main menu. You can enter a name you wish to listen to in the field that is present on the top left. If that name is in the database then it shows up in the list on the right. If part of the name is present in the database, then only the part that is present shows up in the list. You can add as many names as you want in the list. Click next when you want to play the names from the list.
8.  To add a list of names, click on the Upload/Search name from the main menu. Press Upload Text File to select a text file and upload    that. The names from the text file which are present in database are shown in the list on the right. Click next when you want to play   the names from the list.
9.  To test if your mic is working click test mic on main menu
10. An additional database folder can also be added to the application. Select the additional database folder from the settings.

<sub><sup>*compare plays recorded attempt first and then database name</sup></sub>

---
### Note:
Please do not delete any of the files created in the "NameSayer" folder when the application is running.
Please do not add any file other than a text file when uploading the names,

---
Application created by @DeshmukhChinmay and @HongShi10 as part of SE206 Project for University of Auckland
