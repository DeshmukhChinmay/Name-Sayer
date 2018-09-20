package main;

import java.io.*;

public class BadAudioText {

    private static BadAudioText ourInstance;

    public static BadAudioText getInstance() throws  IOException{
        if (ourInstance == null) {
            ourInstance = new BadAudioText();
        }
        return ourInstance;
    }

    private BadAudioText() {
    }

    public void writeText(String textToAdd) throws IOException{
        FileWriter bad_names = new FileWriter("Bad_Recordings.txt",true);
        PrintWriter writer = new PrintWriter(bad_names);
        writer.printf("%s" +"%n",textToAdd);
        writer.close();
    }
    public void removeTextFromFile(String versionName) throws IOException {
        //Gets the files for a Temp file and Recordings
        File temp = new File("TextFile.txt");
        File main = new File("Bad_Recordings.txt");

        BufferedReader reader = new BufferedReader(new FileReader(main));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        String current;
        while ((current = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = current.trim();
            if (trimmedLine.equals(versionName)) {
                continue;
            } else {
                writer.write(current + System.getProperty("line.separator"));
            }
        }
        writer.close();
        reader.close();
        boolean successful = temp.renameTo(main);
    }
}
