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

}
