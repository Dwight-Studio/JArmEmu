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

package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.directive.ParsedDirective;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.sim.prepare.PreparationStream;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class CodePreparator {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final ArrayList<ParsedFile> parsedFiles;
    private ArrayList<ParsedInstruction<?, ?, ?, ?>> instructionMemory;
    private ArrayList<FilePos> instructionPosition;

    public CodePreparator() {
        this.parsedFiles = new ArrayList<>();
        this.instructionMemory = new ArrayList<>();
        this.instructionPosition = new ArrayList<>();
    }

    /**
     * Charge un nouveau programme dans le préparateur d'état
     *
     * @param sourceParser l'analyseur de source
     * @param fileSources  les scanneurs de sources
     */
    public ASMException[] load(SourceParser sourceParser, List<SourceScanner> fileSources) {
        ArrayList<ASMException> exceptions = new ArrayList<>();

        this.parsedFiles.clear();

        for (SourceScanner source : fileSources) {
            try {
                source.goTo(0);
                this.parsedFiles.add(sourceParser.parse(source));
            } catch(ASMException exception){
                exceptions.add(exception);
            }
        }
        return exceptions.toArray(new ASMException[0]);
    }

    public ASMException[] initiate(StateContainer stateContainer) {
        ArrayList<ASMException> exceptions = new ArrayList<>();

        // Constitution de la mémoire de programme
        instructionMemory = new ArrayList<>();
        instructionPosition = new ArrayList<>();

        int lastMem = 0;

        for (ParsedFile file : parsedFiles) {
            for (ParsedObject obj : file) {
                try {
                    if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                        instructionMemory.add(ins);
                        lastMem = instructionMemory.size();
                        instructionPosition.add(new FilePos(file.getIndex(), ins.getLineNumber()).freeze());
                    } else if (obj instanceof ParsedLabel label) {
                        if (label.getSection() == Section.TEXT) {
                            label.register(stateContainer, new FilePos(file.getIndex(), lastMem).freeze());
                        }
                    }
                } catch (ASMException e) {
                    exceptions.add(e);
                }
            }
        }

        stateContainer.getCurrentFilePos().setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            try {
                new PreparationStream(file)
                        .forDirectives().isContextBuilder(true).contextualize(stateContainer)
                        .forDirectives().notInSection(Section.TEXT).registerLabels(stateContainer)
                        .resetPos(stateContainer)
                        .forDirectives().isContextBuilder(false).contextualize(stateContainer)
                        .forDirectives().inSection(Section.RODATA).execute(stateContainer)
                        .startPseudoInstructionAllocation(stateContainer)
                        .forPseudoInstructions().allocate(stateContainer)
                        .closeReadOnlyRange(stateContainer)
                        .forDirectives().inSection(Section.DATA).execute(stateContainer)
                        .forDirectives().inSection(Section.BSS).execute(stateContainer)
                        .forPseudoInstructions().generate(stateContainer)
                        .forDirectives().isGenerated(true).contextualize(stateContainer)
                        .forDirectives().isGenerated(true).execute(stateContainer)
                        .forInstructions().contextualize(stateContainer)
                        .forInstructions().verify(() -> new StateContainer(stateContainer));
            } catch (ASMException exception) {
                exceptions.add(exception);
            }
            stateContainer.getCurrentFilePos().incrementFileIndex();
        }

        // Ajout du label _START
        if (!stateContainer.getGlobals().contains("_START")) {
            stateContainer.getLabelsInFiles().getFirst().put("_START", 0);
            stateContainer.addGlobal("_START", 0);
            logger.info("Can't find label '_START', setting one at 0");
        }

        // Ajout du label _END
        if (!stateContainer.getGlobals().contains("_END")) {
            FilePos lastInstruction = new FilePos(parsedFiles.size()-1, lastMem);
            stateContainer.getLabelsInFiles().getLast().put("_END", lastInstruction.toByteValue());
            stateContainer.addGlobal("_END", parsedFiles.size() - 1);
            logger.info("Can't find label '_END', setting one at " + lastInstruction.toByteValue());
        }

        return exceptions.toArray(new ASMException[0]);
    }

    public void clearFiles() {
        this.parsedFiles.clear();
    }

    /**
     * @return une vue de la liste des instructions
     */
    public List<ParsedInstruction<?, ?, ?, ?>> getInstructionMemory() {
        return Collections.unmodifiableList(instructionMemory);
    }

    /**
     * @param pos la position dans la mémoire
     * @return le numéro de ligne de l'instruction
     */
    public @Nullable FilePos getLineNumber(int pos) {
        if (pos % 4 != 0 || pos < 0 || pos / 4 >= instructionMemory.size()) return null;
        return instructionPosition.get(pos / 4);
    }

    /**
     * @param filePos le numéro de la ligne
     * @return la position de la ligne
     */
    public int getPosition(FilePos filePos) {
        return instructionPosition.indexOf(filePos) * 4;
    }

    public ArrayList<ParsedFile> getParsedFiles() {
        return parsedFiles;
    }
}
