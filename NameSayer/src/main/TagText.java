package main;

import java.io.*;

public class TagText {

    private static TagText ourInstance;

    public static TagText getInstance() throws IOException {
        if (ourInstance == null) {
            ourInstance = new TagText();
        }
        return ourInstance;
    }

    private TagText() {
    }

    // Writes the given string into the text file
    public void writeText(String versionName, String tag) throws IOException {
        FileWriter tagFile = new FileWriter("Tags_File.txt", true);
        PrintWriter writer = new PrintWriter(tagFile);
        writer.printf("%s" + "%n", versionName + "_" + tag);
        writer.close();
    }

    // Removes the given string in a text field by creating a updateNameObjects folder and moving all but that line into the new file
    // and renaming it
    public void removeTextFromFile(String versionName, String tag) throws IOException {
        //Gets the files for a Temp file and Recordings
        File temp = new File("temp_Tags.txt");
        File main = new File("Tags_File.txt");

        BufferedReader reader = new BufferedReader(new FileReader(main));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        String current;

        while ((current = reader.readLine()) != null) {
            // Trim newline when comparing with lineToRemove
            String trimmedLine = current.trim();
            if (trimmedLine.equals(versionName + "_" + tag)) {
                continue;
            } else {
                writer.write(current + System.getProperty("line.separator"));
            }
        }
        writer.close();
        reader.close();
        temp.renameTo(main);
    }
}
