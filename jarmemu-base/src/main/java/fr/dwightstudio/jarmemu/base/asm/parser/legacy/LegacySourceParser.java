/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
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

package fr.dwightstudio.jarmemu.base.asm.parser.legacy;

import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.ParsedSection;
import fr.dwightstudio.jarmemu.base.asm.directive.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.Condition;
import fr.dwightstudio.jarmemu.base.asm.instruction.DataMode;
import fr.dwightstudio.jarmemu.base.asm.instruction.Instruction;
import fr.dwightstudio.jarmemu.base.asm.instruction.UpdateMode;
import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class LegacySourceParser implements SourceParser {

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected Instruction instruction;
    protected boolean updateFlags;
    protected SourceScanner sourceScanner;
    protected LegacySectionParser legacySectionParser;
    protected LegacyDirectiveParser legacyDirectiveParser;

    protected DataMode dataMode;
    protected UpdateMode updateMode;
    protected Condition conditionExec;
    protected String currentLine;
    protected String instructionString;
    protected ArrayList<String> arguments;
    protected Section section;
    protected Section currentSection;
    protected String label;

    /**
     * Création d'un parseur vide
     */
    public LegacySourceParser() {
        this.legacySectionParser = new LegacySectionParser();
        this.legacyDirectiveParser = new LegacyDirectiveParser();

        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.conditionExec = Condition.AL;
        this.currentLine = "";
        this.instructionString = "";
        this.arguments = new ArrayList<>();
        this.section = null;
        this.currentSection = Section.NONE;
        this.label = "";
    }

    /**
     * Création du lecteur de code de l'éditeur
     * @param sourceScanner le lecteur de source utilisé
     */
    public LegacySourceParser(SourceScanner sourceScanner) {
        this();

        this.sourceScanner = sourceScanner;
    }

    /**
     * Retire le commentaire de la ligne s'il y en a un
     * @param line La ligne sur laquelle on veut appliquer la fonction
     * @return La ligne modifiée ou non
     */
    public String removeComments(@NotNull String line){
        return line.split("@")[0];
    }

    /**
     * Retire les espaces blancs avant et après l'instruction
     * @param line La ligne sur laquelle on veut appliquer la fonction
     * @return La ligne modifiée ou non
     */
    public String removeBlanks(@NotNull String line){
        return line.strip();
    }

    /**
     * Retire les drapeaux S, H, B et les "update modes"
     * @param instructionString La ligne à modifier
     * @return La ligne modifiée ou non
     */
    public String removeFlags(@NotNull String instructionString){
        if (instructionString.endsWith("S") && (instructionString.length() % 2 == 0) && (!instructionString.equals("BXNS"))){
            updateFlags = true;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("H") && (!instructionString.equals("PUSH"))) {
            dataMode = DataMode.HALF_WORD;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("B") && (!instructionString.equals("SUB") && !instructionString.equals("RSB") && !instructionString.equals("B"))) {
            dataMode = DataMode.BYTE;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.length()==7 || instructionString.length()==5) {
            UpdateMode[] updateModes = UpdateMode.values();
            for (UpdateMode updatemode:updateModes) {
                if (instructionString.endsWith(updatemode.toString().toUpperCase())) {
                    updateMode = updatemode;
                    instructionString = instructionString.substring(0, instructionString.length()-2);
                }
            }
        }
        return instructionString;
    }

    /**
     * Retire les conditions d'exécution
     * @param instructionString La ligne à modifier
     * @return La ligne modifiée ou non
     */
    public String removeCondition(String instructionString){
        Condition[] conditions = Condition.values();
        for (Condition condition:conditions) {
            if (instructionString.endsWith(condition.toString().toUpperCase())){
                if (instructionString.endsWith("MLAL")) continue;
                if (instructionString.endsWith("TEQ")) continue;

                this.conditionExec = condition;
                instructionString = instructionString.substring(0, instructionString.length()-2);
            }
        }
        return instructionString;
    }

    /**
     * Méthode principale, lecture du fichier et renvoie des instructions parsées à verifier
     */
    @Override
    public ParsedFile parse(SourceScanner scanner) throws ASMException {
        currentSection = Section.NONE;
        ParsedFile file = new ParsedFile(scanner);
        sourceScanner = scanner;
        sourceScanner.goTo(-1);

        while (this.sourceScanner.hasNextLine()) {
            if (Section.END.equals(currentSection)) break;
            parseOneLine(file);
        }

        return file;
    }

    /**
     * Lecture d'une ligne
     */
    public void readOneLineASM(ParsedFile file) throws ASMException {
        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.dataMode = null;
        this.section = null;
        this.label = "";
        this.conditionExec = Condition.AL;
        this.arguments.clear();

        currentLine = this.sourceScanner.nextLine();
        currentLine = this.removeComments(currentLine);
        currentLine = this.removeBlanks(currentLine);

        if (!currentLine.isEmpty()){
            Section section = this.legacySectionParser.parseOneLine(currentLine);
            if (section != null) this.section = section;

            boolean hasDirectives = false;
            if (section == null && currentSection != Section.COMMENT) {
                hasDirectives = this.legacyDirectiveParser.parseOneLine(sourceScanner, currentLine, this, file);
            }

            if (currentLine.contains(":")){
                currentLine = currentLine.substring(currentLine.indexOf(":")+1).strip();
            }

            if (currentSection == Section.TEXT && this.section == null && !hasDirectives){
                if(!currentLine.isEmpty()){
                    String oldInstructionString = currentLine.split(" ")[0];
                    instructionString = oldInstructionString.toUpperCase();
                    int instructionLength = instructionString.length();
                    instructionString = this.removeFlags(instructionString);
                    instructionString = this.removeCondition(instructionString);

                    Instruction[] instructions = Instruction.values();
                    for (Instruction instruction:instructions) {
                        if(instruction.toString().toUpperCase().equals(instructionString)) this.instruction = instruction;
                    }

                    if (this.instruction == null) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unknownInstruction", oldInstructionString)).with(sourceScanner.getLineNumber()).with(new ParsedFile(sourceScanner));

                    if (currentLine.contains("{")) {
                        String[] split = currentLine.substring(instructionLength).split(",", 2);
                        StringBuilder argument;
                        if (split.length > 1) {
                            argument = new StringBuilder(split[1].strip());
                        } else {
                            argument =  new StringBuilder(currentLine.substring(instructionLength).split(" ", 2)[1].strip());
                        }
                        argument.deleteCharAt(0);
                        argument.deleteCharAt(argument.length() - 1);
                        ArrayList<String> argumentArray = new ArrayList<>(Arrays.asList(argument.toString().split(",")));
                        argumentArray.replaceAll(String::strip);
                        if (argumentArray.size() > 1) {
                            argument = new StringBuilder(currentLine.substring(instructionLength).split(",")[0].strip() + "," + "{");
                            for (String arg : argumentArray) {
                                arg = this.joinString(arg);
                                argument.append(arg).append(",");
                            }
                            argument.deleteCharAt(argument.length() - 1);
                            argument.append("}");
                        } else {
                            argument = new StringBuilder(argumentArray.getFirst());
                            argument.append("}");
                            argument.insert(0, "{");
                        }
                        this.arguments.addAll(Arrays.asList(argument.toString().split(",", 2)));
                        this.arguments.replaceAll(String::strip);
                    } else if (currentLine.contains("[")) {
                        StringBuilder argument = new StringBuilder(currentLine.split("\\[")[1]);
                        boolean toUpdate = argument.toString().strip().charAt(argument.toString().strip().length() - 1) == '!';
                        argument = new StringBuilder(argument.substring(0, argument.length() - 1));
                        if (argument.toString().split("]").length==2){
                            this.arguments.addAll(Arrays.asList(currentLine.substring(instructionLength).split(",")));
                            this.arguments.replaceAll(String::strip);
                            this.arguments = this.joinStringArray(this.arguments);
                        } else {
                            argument = new StringBuilder(argument.toString().split("]")[0]);
                            ArrayList<String> argumentArray = new ArrayList<>(Arrays.asList(argument.toString().split(",")));
                            argumentArray.replaceAll(String::strip);
                            argument = new StringBuilder(currentLine.substring(instructionLength).split(",")[0].strip() + "," + "[");
                            for (String arg : argumentArray) {
                                arg = this.joinString(arg);
                                argument.append(arg).append(",");
                            }
                            argument.deleteCharAt(argument.length() - 1);
                            argument.append("]");
                            if (toUpdate) argument.append("!");
                            this.arguments.addAll(Arrays.asList(argument.toString().split(",", 2)));
                            this.arguments.replaceAll(String::strip);
                        }
                    } else {
                        this.arguments.addAll(Arrays.asList(currentLine.substring(instructionLength).split(",")));
                        this.arguments.replaceAll(String::strip);
                    }

                    if (arguments.size() > 4) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unexpectedStatement", currentLine));
                }
            }
        }
    }

    /**
     * Supprime les espaces composant le String
     * @param argument Le String
     * @return Le String avec les espaces en moins
     */
    private String joinString(String argument) {
        StringBuilder newArg = new StringBuilder();
        ArrayList<String> elements = new ArrayList<>(Arrays.asList(argument.split(" ")));
        for (String ele:elements) {
            newArg.append(ele);
        }
        return String.valueOf(newArg);
    }

    /**
     * Supprime les espaces des Strings composants une ArrayList<String>
     * @param arguments Une ArrayList<String>
     * @return L'ArrayList avec les espaces en moins
     */
    private ArrayList<String> joinStringArray(ArrayList<String> arguments) {
        ArrayList<String> returnString = new ArrayList<>();
        for (String arg:arguments) {
            StringBuilder newArg = new StringBuilder();
            ArrayList<String> elements = new ArrayList<>(Arrays.asList(arg.split(" ")));
            for (String ele:elements) {
                newArg.append(ele);
            }
            returnString.add(String.valueOf(newArg));
        }
        return returnString;
    }

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @return un ParsedObject non vérifié
     */
    public void parseOneLine(ParsedFile file) throws ASMException {

        readOneLineASM(file);

        String arg1 = null;
        String arg2 = null;
        String arg3 = null;
        String arg4 = null;

        if (this.section != null) {
            this.currentSection = this.section;
            file.add(new ParsedSection(section).withLineNumber(sourceScanner.getLineNumber()));
            return;
        }

        try {
            arg1 = arguments.get(0);
            arg2 = arguments.get(1);
            arg3 = arguments.get(2);
            arg4 = arguments.get(3);
        } catch (IndexOutOfBoundsException ignored) {}

        if (instruction != null) file.add(instruction.create(conditionExec, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4).withLineNumber(sourceScanner.getLineNumber()));
    }
}
