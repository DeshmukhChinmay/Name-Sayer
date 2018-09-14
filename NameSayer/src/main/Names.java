package main;

import java.util.LinkedList;

public class Names {

    private String name;
    private LinkedList<String> versions = new LinkedList<>();

    public Names(String name) {
        this.name = name;
        versions.add("Version 1");
    }

    public String getName() {
        return name;
    }

    public void addVersion(String version) {
        versions.add(version);
    }

}
