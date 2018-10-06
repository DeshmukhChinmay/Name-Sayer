package main;

import java.io.*;

public class ConcatFilesText {

    private static ConcatFilesText ourInstance;
    private String currentWorkingDir = System.getProperty("user.dir");

    public static ConcatFilesText getInstance() throws IOException {
        if (ourInstance == null) {
            ourInstance = new ConcatFilesText();
        }
        return ourInstance;
    }

    private ConcatFilesText() {

    }

    public void writeText(String textToAdd) throws IOException {
        FileWriter concatFiles = new FileWriter(currentWorkingDir + "/NameSayer/Temp/Concat/concatFiles.txt", false);
        PrintWriter writer = new PrintWriter(concatFiles);
        String[] names = textToAdd.split("%");
        for (String n: names) {
            writer.println("file '" + n + "'");
        }
        writer.println();
        writer.close();
    }

}
