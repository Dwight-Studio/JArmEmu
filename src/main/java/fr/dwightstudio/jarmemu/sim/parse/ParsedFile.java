package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.SourceScanner;

import java.util.TreeMap;

public class ParsedFile{

    private final TreeMap<Integer, ParsedObject> parsedObjects;
    private SourceScanner sourceScanner;
    private String name;

    public ParsedFile() {
        this.name = "";
        this.sourceScanner = null;
        this.parsedObjects = new TreeMap<>();
    }

    public ParsedFile(String name) {
        this.name = name;
        this.sourceScanner = null;
        this.parsedObjects = new TreeMap<>();
    }

    public ParsedFile(SourceScanner sourceScanner) {
        this.name = "";
        this.sourceScanner = sourceScanner;
        this.parsedObjects = new TreeMap<>();
    }

    public ParsedFile(TreeMap<Integer, ParsedObject> parsedObjects) {
        this.name = "";
        this.sourceScanner = null;
        this.parsedObjects = parsedObjects;
    }

    public ParsedFile(String name, SourceScanner sourceScanner) {
        this.name = name;
        this.sourceScanner = sourceScanner;
        this.parsedObjects = new TreeMap<>();
    }

    public ParsedFile(String name, TreeMap<Integer, ParsedObject> parsedObjects) {
        this.name = name;
        this.sourceScanner = null;
        this.parsedObjects = parsedObjects;
    }

    public ParsedFile(SourceScanner sourceScanner, TreeMap<Integer, ParsedObject> parsedObjects) {
        this.name = "";
        this.sourceScanner = sourceScanner;
        this.parsedObjects = parsedObjects;
    }

    public TreeMap<Integer, ParsedObject> getParsedObjects() {
        return this.parsedObjects;
    }

    public SourceScanner getSourceScanner() {
        return this.sourceScanner;
    }

    public String getName() {
        return this.name;
    }

    public void setSourceScanner(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
    }

    public void setName(String name) {
        this.name = name;
    }
}
