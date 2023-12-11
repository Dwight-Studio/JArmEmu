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

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.*;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class CodePreparator {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final int stackAddress;
    private final int symbolsAddress;
    private final ArrayList<ParsedFile> parsedFiles;
    private ArrayList<ParsedInstruction> instructionMemory;
    private ArrayList<FilePos> instructionPosition;

    public CodePreparator(int stackAddress, int symbolsAddress) {
        this.stackAddress = stackAddress;
        this.symbolsAddress = symbolsAddress;
        this.parsedFiles = new ArrayList<>();
        this.instructionMemory = new ArrayList<>();
        this.instructionPosition = new ArrayList<>();
    }

    /**
     * Charge un nouveau programme dans le préparateur d'état
     *
     * @param sourceParser l'analyseur de source
     * @param fileSources les scanneurs de sources
     * @return la liste des erreurs
     */
    public SyntaxASMException[] load(SourceParser sourceParser, List<SourceScanner> fileSources) {
        ArrayList<SyntaxASMException> exceptions = new ArrayList<>();

        this.parsedFiles.clear();

        for (SourceScanner source : fileSources) {
            try {
                source.goTo(0);
                sourceParser.setSource(source);
                this.parsedFiles.add(sourceParser.parse());
            } catch(SyntaxASMException exception){
                exceptions.add(exception);
            }
        }

        if (!exceptions.isEmpty()) return exceptions.toArray(new SyntaxASMException[0]);

        instructionMemory = new ArrayList<>();
        instructionPosition = new ArrayList<>();

        for (ParsedFile parsedFile : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> obj : parsedFile.getParsedObjects().entrySet()) {
                if (obj.getValue() instanceof ParsedInstruction instruction) {
                    instructionMemory.add(instruction);
                    instructionPosition.add(new FilePos(parsedFile.getIndex(), obj.getKey()).freeze());
                } else if (obj.getValue() instanceof  ParsedLabel parsedLabel) {
                    if (parsedLabel.getInstruction() != null) {
                        instructionMemory.add(parsedLabel.getInstruction());
                        instructionPosition.add(new FilePos(parsedFile.getIndex(), obj.getKey()).freeze());
                    }
                }
            }
        }
        return verifyAll();
    }

    public void clearFiles() {
        this.parsedFiles.clear();
    }

    /**
     * @return une vue de la liste des instructions
     */
    public List<ParsedInstruction> getInstructionMemory() {
        return Collections.unmodifiableList(instructionMemory);
    }

    /**
     * Vérifie toutes les ParsedInstructions et ParsedDirective
     *
     * @return les erreurs si il y en a
     * @apiNote Doit être appelé après un resetState
     */
    public SyntaxASMException[] verifyAll() {
        logger.info("Verifying all instructions");
        ArrayList<SyntaxASMException> rtn = new ArrayList<>();
        StateContainer stateContainer = processState();


        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                SyntaxASMException e = inst.getValue().verify(inst.getKey(), () -> new StateContainer(stateContainer));
                if (e != null) rtn.add(e.with(inst.getKey()).with(file));
            }
        }


        int line = -1;
        try {
            if (!stateContainer.getGlobals().isEmpty()) {
                for (String global : stateContainer.getGlobals())
                    try {
                        stateContainer.evalWithAll(global);
                    } catch (SyntaxASMException e) {
                        ArgumentParsers.LABEL.parse(stateContainer, global);
                    }
            }
        } catch (SyntaxASMException exception) {

            for (ParsedFile file : parsedFiles) {
                stateContainer.setFileIndex(file.getIndex());
                for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                    if (inst.getValue() instanceof ParsedDirective directive) {
                        if (directive.getDirective() == Directive.GLOBAL) {
                            line = Math.max(line, inst.getKey());
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack pack) {
                        for (ParsedObject ignored : pack.getContent()) {
                            if (inst.getValue() instanceof ParsedDirective directive) {
                                if (directive.getDirective() == Directive.GLOBAL) {
                                    line = Math.max(line, inst.getKey());
                                }
                            }
                        }
                    }
                }
            }

            rtn.add(exception.with(line));
        }

        return rtn.toArray(SyntaxASMException[]::new);
    }

    /**
     * Crée le conteneur d'état d'origine.
     *
     * @return un conteneur d'état initialisé
     */
    public StateContainer processState() {
        StateContainer stateContainer = new StateContainer(stackAddress, symbolsAddress);

        stateContainer.clearAndInitFiles(parsedFiles.size());

        replaceMovShifts(stateContainer);
        applyDirectives(stateContainer);
        registerLabels(stateContainer);

        return stateContainer;
    }

    /**
     * Applique toutes les directives, remplace les constantes, etc...
     */
    private void applyDirectives(StateContainer stateContainer) {
        int off = -1;
        FilePos pos = new FilePos(0, symbolsAddress);

        // Suppression des directives générées
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            file.getParsedObjects().replaceAll((k, v) -> {
                if (v instanceof ParsedDirective directive) {
                    if (directive.isGenerated()) return null;
                } else if (v instanceof ParsedDirectivePack pack) {
                    if (pack.containsGenerated()) return null;
                }
                return v;
            });
        }

        // Application de toutes les directives qui n'initialisent pas de données
        pos.setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (parsedDirective.getDirective().isSectionIndifferent()) {
                            parsedDirective.apply(stateContainer, pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        parsedDirectivePack.applySectionIndifferent(stateContainer, pos);
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
            pos.incrementFileIndex();
        }


        if (pos.getPos() != symbolsAddress) throw new IllegalStateException("Non-data initializer taking space in memory");

        // Application de toutes les directives de RODATA
        pos.setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (!parsedDirective.getDirective().isSectionIndifferent() && parsedDirective.getSection() == Section.RODATA) {
                            parsedDirective.apply(stateContainer, pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        parsedDirectivePack.applySectionSensitive(stateContainer, pos, Section.RODATA);
                    } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                        label.register(stateContainer, pos.getPos());
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
            pos.incrementFileIndex();
        }

        FilePos generateDataPos = pos.clone();

        // Allocation de la place pour les directives générées
        pos.setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                if (inst.getValue() instanceof ParsedInstruction parsedInstruction) {
                    pos.incrementPos(parsedInstruction.isPseudoInstruction() ? 4 : 0);
                }
            }
            pos.incrementFileIndex();
        }


        stateContainer.setLastAddressROData(pos.getPos());

        // Application de toutes les directives de DATA
        pos.setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (!parsedDirective.getDirective().isSectionIndifferent() && parsedDirective.getSection() == Section.DATA) {
                            parsedDirective.apply(stateContainer, pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        parsedDirectivePack.applySectionSensitive(stateContainer, pos, Section.DATA);
                    } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                        label.register(stateContainer, pos.getPos());
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
            pos.incrementFileIndex();
        }


        // Application de toutes les directives de BSS
        pos.setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (!parsedDirective.getDirective().isSectionIndifferent() && parsedDirective.getSection() == Section.BSS) {
                            parsedDirective.apply(stateContainer, pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        parsedDirectivePack.applySectionSensitive(stateContainer, pos, Section.BSS);
                    } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                        label.register(stateContainer, pos.getPos());
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
            pos.incrementFileIndex();
        }

        // Génération des directives à l'aide des pseudo-opérations '='
        for (ParsedFile file : parsedFiles) {
            HashMap<Integer, ParsedObject> temp = new HashMap<>();
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                if (inst.getValue() instanceof ParsedInstruction parsedInstruction) {
                    ParsedDirectivePack pack = parsedInstruction.convertValueToDirective(stateContainer);
                    if (!pack.isEmpty()) {
                        temp.put(off, pack.close());
                        off--;
                    }
                } else if (inst.getValue() instanceof ParsedLabel parsedLabel) {
                    if (parsedLabel.getInstruction() != null) {
                        ParsedDirectivePack pack = parsedLabel.getInstruction().convertValueToDirective(stateContainer);
                        if (!pack.isEmpty()) {
                            temp.put(off, pack.close());
                            off--;
                        }
                    }
                }
            }
            file.getParsedObjects().putAll(temp);
        }

        // Application des directives générée
        generateDataPos.setFileIndex(0);
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (parsedDirective.isGenerated()) {
                            parsedDirective.apply(stateContainer, generateDataPos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        if (parsedDirectivePack.containsGenerated()) {
                            parsedDirectivePack.apply(stateContainer, generateDataPos);
                        }
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
            generateDataPos.incrementFileIndex();
        }

    }

    /**
     * Remplace les instructions de la forme MOV reg1, reg2, SHIFT en SHIFT reg1, reg2
     */
    protected void replaceMovShifts(StateContainer stateContainer) {
        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                if (inst.getValue() instanceof ParsedInstruction parsedInstruction) {
                    inst.setValue(parsedInstruction.convertMovToShift(stateContainer));
                } else if (inst.getValue() instanceof ParsedLabel parsedLabel) {
                    if (parsedLabel.getInstruction() != null) {
                        parsedLabel.setInstruction(parsedLabel.getInstruction().convertMovToShift(stateContainer));
                    }
                }
            }
        }
    }

    /**
     * Enregistre les labels dans le conteneur d'états
     */
    public void registerLabels(StateContainer stateContainer) {
        FilePos lastInstruction = new FilePos(0,0);

        for (ParsedFile file : parsedFiles) {
            stateContainer.setFileIndex(file.getIndex());
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                if (inst.getValue() instanceof ParsedLabel label) {
                    label.register(stateContainer, lastInstruction.freeze());
                    if (label.getInstruction() != null) {
                        lastInstruction.incrementPos();
                    }
                }
                if (inst.getValue() instanceof ParsedInstruction) {
                    lastInstruction.incrementPos();
                }
            }
        }

        // Ajout des labels _START et _END

        if (!stateContainer.getGlobals().contains("_START")) {
            stateContainer.getLabelsInFiles().getFirst().put("_START", 0);
            stateContainer.addGlobal("_START", 0);
            logger.info("Can't find label '_START', setting one at 0");
        }

        if (!stateContainer.getGlobals().contains("_END")) {
            stateContainer.getLabelsInFiles().getLast().put("_END", lastInstruction.toByteValue());
            stateContainer.addGlobal("_END", parsedFiles.size() - 1);
            logger.info("Can't find label '_END', setting one at " + lastInstruction.toByteValue());
        }
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
