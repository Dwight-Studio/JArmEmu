/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.sim;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SourceScanner {

    private int currentInstructionValue;
    private final ArrayList<String> code;
    private final int fileIndex;
    private final String fileName;

    public SourceScanner(File file, int fileIndex) throws IOException {
        this.code = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        code.addAll(reader.lines().toList());
        this.currentInstructionValue = -1;
        this.fileIndex = fileIndex;
        fileName = file.getName();
    }

    public SourceScanner(String code, String fileName, int fileIndex) {
        this.code = new ArrayList<>(Arrays.stream(code.split("\n")).toList());
        this.currentInstructionValue = -1;
        this.fileIndex = fileIndex;
        this.fileName = fileName;
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

    /**
     * Déplace le curseur de lecture à la ligne spécifiée. Attention, invalide le comptage d'instruction !
     */
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

    public int getFileIndex() {
        return fileIndex;
    }

    public String getName() {
        return fileName;
    }
}
