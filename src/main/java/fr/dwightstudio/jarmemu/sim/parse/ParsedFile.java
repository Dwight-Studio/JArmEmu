package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.SourceScanner;

import java.util.TreeMap;

public class ParsedFile{

    private final TreeMap<Integer, ParsedObject> parsedObjects;
    private SourceScanner sourceScanner;

    public ParsedFile(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
        this.parsedObjects = new TreeMap<>();
    }

    public ParsedFile(SourceScanner sourceScanner, TreeMap<Integer, ParsedObject> parsedObjects) {
        this.sourceScanner = sourceScanner;
        this.parsedObjects = parsedObjects;
    }

    public int getIndex() {
        return sourceScanner.getFileIndex();
    }

    public TreeMap<Integer, ParsedObject> getParsedObjects() {
        return this.parsedObjects;
    }

    public SourceScanner getSourceScanner() {
        return this.sourceScanner;
    }

    public String getName() {
        return this.sourceScanner.getName() == null ? "Unknown" : this.sourceScanner.getName();
    }
}
