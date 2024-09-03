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

package fr.dwightstudio.jarmemu.base.sim;

import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.ParsedLabel;
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.MOVInstruction;
import fr.dwightstudio.jarmemu.base.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.sim.prepare.PreparationStream;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class StateInitializer {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final ArrayList<ParsedFile> parsedFiles;
    private ArrayList<ParsedInstruction<?, ?, ?, ?>> instructionMemory;
    private ArrayList<FilePos> instructionPosInFile;

    public StateInitializer() {
        this.parsedFiles = new ArrayList<>();
        this.instructionMemory = new ArrayList<>();
        this.instructionPosInFile = new ArrayList<>();
    }

    /**
     * Load a new program in the state initializer
     *
     * @param sourceParser the source parser
     * @param fileSources the source scanner of each file
     */
    public ASMException[] load(SourceParser sourceParser, List<SourceScanner> fileSources) {
        ArrayList<ASMException> exceptions = new ArrayList<>();

        this.parsedFiles.clear();

        for (SourceScanner source : fileSources) {
            try {
                source.goTo(0);
                this.parsedFiles.add(sourceParser.parse(source));
            } catch (ASMException exception) {
                exceptions.add(exception);
            }
        }
        return exceptions.toArray(new ASMException[0]);
    }

    /**
     * Launch state initiation
     *
     * @param stateContainer the state container to initialize
     * @return all the thrown exceptions
     */
    public ASMException[] initiate(StateContainer stateContainer) {
        ArrayList<ASMException> exceptions = new ArrayList<>();

        // Instruction memory initialisation
        // Instruction memory is a virtual memory used to simplify emulation (it is not the program memory)
        instructionMemory = new ArrayList<>();
        instructionPosInFile = new ArrayList<>();

        stateContainer.clearAndInitFiles(parsedFiles.size());

        // Reading all instructions
        stateContainer.getCurrentMemoryPos().setFileIndex(0);
        stateContainer.resetMemoryPos();
        for (ParsedFile file : parsedFiles) {
            for (ParsedObject obj : file) {
                try {
                    if (obj instanceof MOVInstruction ins) {
                        if (ins.isShiftInstruction()) {
                            obj = ins.getShiftInstruction();
                        }
                    }
                    if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                        instructionMemory.add(ins);
                        stateContainer.getCurrentMemoryPos().incrementPos(4);
                        instructionPosInFile.add(new FilePos(file.getIndex(), ins.getLineNumber()).freeze());
                    } else if (obj instanceof ParsedLabel label) {
                        if (label.getSection() == Section.TEXT) {
                            label.register(stateContainer, stateContainer.getCurrentMemoryPos().freeze());
                        }
                    }
                } catch (ASMException e) {
                    exceptions.add(e);
                }
            }
            stateContainer.getCurrentMemoryPos().incrementFileIndex();
        }
        stateContainer.startSymbolRange();

        logger.info("Setting Data Range opening address to " + stateContainer.getSymbolAddress());

        // Program memory initialization
        // Program memory is the memory used to store instruction code (32 bit value) and data
        stateContainer.getCurrentMemoryPos().setFileIndex(0);
        try {
            new PreparationStream(parsedFiles)

                    // Building context
                    .goToSymbolRange(stateContainer)
                    .forDirectives().isContextBuilder(true).contextualize(stateContainer)
                    .forDirectives().inSection(Section.RODATA).registerLabels(stateContainer)

                    .startPseudoInstructionRange(stateContainer)
                    .forPseudoInstructions().allocate(stateContainer)

                    .startWritableDataRange(stateContainer)
                    .forDirectives().inSection(Section.DATA).registerLabels(stateContainer)
                    .forDirectives().inSection(Section.BSS).registerLabels(stateContainer)


                    // Executing directives
                    .goToSymbolRange(stateContainer)
                    .forDirectives().isContextBuilder(false).contextualize(stateContainer)
                    .forDirectives().inSection(Section.RODATA).execute(stateContainer)

                    .goToWritableDataRange(stateContainer)
                    .forDirectives().inSection(Section.DATA).execute(stateContainer)
                    .forDirectives().inSection(Section.BSS).execute(stateContainer)


                    // Preparation and execution of pseudo instructions
                    .forPseudoInstructions().generate(stateContainer)

                    .goToPseudoInstructionRange(stateContainer)
                    .forDirectives().isGenerated(true).contextualize(stateContainer)
                    .forDirectives().isGenerated(true).execute(stateContainer)


                    // Preparation and verification of instructions
                    .resetPos(stateContainer)
                    .forInstructions().contextualize(stateContainer)
                    .forInstructions().verify(() -> new StateContainer(stateContainer).withTestingRegister())
                    .forInstructions().write(stateContainer, this::getPosition);
        } catch (ASMException exception) {
            exceptions.add(exception);
        }

        // Ajout du label _START
        if (!stateContainer.getGlobals().contains("_START")) {
            stateContainer.getLabelsInFiles().getFirst().put("_START", 0);
            stateContainer.addGlobal("_START", 0);
            logger.info("Can't find label '_START', setting one at 0:0");
        }

        // Ajout du label _END
        if (!stateContainer.getGlobals().contains("_END")) {
            stateContainer.getLabelsInFiles().getLast().put("_END", stateContainer.getSymbolAddress());
            stateContainer.addGlobal("_END", parsedFiles.size() - 1);
            logger.info("Can't find label '_END', setting one at 0:" + stateContainer.getSymbolAddress());
        }

        return exceptions.toArray(new ASMException[0]);
    }

    /**
     * @return a view of the instruction memory
     */
    public List<ParsedInstruction<?, ?, ?, ?>> getInstructionMemory() {
        return Collections.unmodifiableList(instructionMemory);
    }

    /**
     * @param pos the address in memory
     * @return the instruction's line
     */
    public @Nullable FilePos getLineNumber(int pos) {
        if (pos % 4 != 0 || pos < 0 || pos / 4 >= instructionMemory.size()) return null;
        return instructionPosInFile.get(pos / 4);
    }

    /**
     * @param instruction the instruction to locate
     * @return the instruction's line
     */
    public @Nullable FilePos getLineNumber(ParsedInstruction<?, ?, ?, ?> instruction) {
        return getLineNumber(getPosition(instruction));
    }

    /**
     * @param filePos the line number
     * @return the address in memory
     */
    public int getPosition(FilePos filePos) {
        return instructionPosInFile.indexOf(filePos) * 4;
    }

    /**
     * @param instruction the instruction to locate
     * @return the position in memory of the instruction
     */
    public int getPosition(ParsedInstruction<?, ?, ?, ?> instruction) {
        int i = instructionMemory.indexOf(instruction);
        return i == -1 ? i : i * 4;
    }

    public ArrayList<ParsedFile> getParsedFiles() {
        return parsedFiles;
    }

    public void clearFiles() {
        this.parsedFiles.clear();
    }
}
