package fr.dwightstudio.jarmemu.sim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SourceScanner {

    private int currentInstructionValue;
    private final ArrayList<String> code;

    public SourceScanner(File file) throws FileNotFoundException {
        this.code = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) code.add(scanner.nextLine());
        this.currentInstructionValue = -1;
    }

    public SourceScanner(String code) {
        this.code = new ArrayList<>(Arrays.stream(code.split("\n")).toList());
        this.currentInstructionValue = -1;
    }

    public String nextLine() {
        this.currentInstructionValue++;
        return this.code.get(this.currentInstructionValue);
    }

    public boolean hasNextLine() {
        return this.currentInstructionValue < this.code.size() - 1;
    }

    public void goTo(int lineNb) {
        this.currentInstructionValue = lineNb;
    }

    public String goToValue(int lineNb) {
        this.currentInstructionValue = lineNb;
        return this.code.get(this.currentInstructionValue);
    }

    public int getCurrentInstructionValue() {
        return this.currentInstructionValue;
    }

    public String exportCode() {
        return String.join("\n",code.toArray(String[]::new));
    }

    public void exportCodeToFile(File savePath) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(savePath);
        String last = "";
        for (String string : code) {
            printWriter.println(string);
            last = string;
        }
        if (!last.strip().equals("")) printWriter.println("");
        printWriter.close();
    }
}
