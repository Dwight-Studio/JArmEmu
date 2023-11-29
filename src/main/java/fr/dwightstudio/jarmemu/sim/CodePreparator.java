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
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.*;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;

import java.util.*;

public class CodePreparator {

    private final int stackAddress;
    private final int symbolsAddress;
    private final ArrayList<ParsedFile> parsedFiles;
    private ArrayList<ParsedInstruction> instructionMemory;
    private ArrayList<int[]> instructionPosition;

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
     * @return la liste des erreurs
     */
    public SyntaxASMException[] load(SourceParser sourceParser) {
        try {
            this.parsedFiles.add(sourceParser.parse());
        } catch (SyntaxASMException exception) {
            return new SyntaxASMException[] {exception};
        }

        instructionMemory = new ArrayList<>();
        instructionPosition = new ArrayList<>();

        for (int i=0; i < parsedFiles.size(); i++) {
            for (Map.Entry<Integer, ParsedObject> obj : parsedFiles.get(i).getParsedObjects().entrySet()) {
                if (obj.getValue() instanceof ParsedInstruction instruction) {
                    instructionMemory.add(instruction);
                    instructionPosition.add(new int[]{i, obj.getKey()});
                } else if (obj.getValue() instanceof  ParsedLabel parsedLabel) {
                    if (parsedLabel.getInstruction() != null) {
                        instructionMemory.add(parsedLabel.getInstruction());
                        instructionPosition.add(new int[]{i, obj.getKey()});
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
        ArrayList<SyntaxASMException> rtn = new ArrayList<>();
        StateContainer stateContainer = processState();

        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                SyntaxASMException e = inst.getValue().verify(inst.getKey(), () -> new StateContainer(stateContainer));
                if (e != null) rtn.add(e);
            }
        }


        int line = -1;
        try {
            if (stateContainer.getGlobal() != null) {
                ArgumentParsers.LABEL.parse(stateContainer, stateContainer.getGlobal());
            }
        } catch (SyntaxASMException exception) {

            for (ParsedFile file : parsedFiles) {
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
        int pos = 0;

        // Suppression des directives générées
        for (ParsedFile file : parsedFiles) {
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
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (parsedDirective.getDirective().isSectionIndifferent()) {
                            pos = Math.max(parsedDirective.apply(stateContainer, pos), pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        pos = Math.max(parsedDirectivePack.applySectionIndifferent(stateContainer, pos), pos);
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
        }


        if (pos != 0) throw new IllegalStateException("Non-data initializer taking space in memory");

        // Application de toutes les directives de RODATA
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (!parsedDirective.getDirective().isSectionIndifferent() && parsedDirective.getSection() == Section.RODATA) {
                            pos = Math.max(parsedDirective.apply(stateContainer, pos), pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        pos = Math.max(parsedDirectivePack.applySectionSensitive(stateContainer, pos, Section.RODATA), pos);
                    } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                        label.register(stateContainer, pos);
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
        }

        int generateDataPos = pos;

        // Allocation de la place pour les directives générées
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                if (inst.getValue() instanceof ParsedInstruction parsedInstruction) {
                    pos += parsedInstruction.isPseudoInstruction() ? 4 : 0;
                }
            }
        }


        stateContainer.setLastAddressROData(pos);

        // Application de toutes les directives de DATA
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (!parsedDirective.getDirective().isSectionIndifferent() && parsedDirective.getSection() == Section.DATA) {
                            pos = Math.max(parsedDirective.apply(stateContainer, pos), pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        pos = Math.max(parsedDirectivePack.applySectionSensitive(stateContainer, pos, Section.DATA), pos);
                    } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                        label.register(stateContainer, pos);
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
        }


        // Application de toutes les directives de BSS
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (!parsedDirective.getDirective().isSectionIndifferent() && parsedDirective.getSection() == Section.BSS) {
                            pos = Math.max(parsedDirective.apply(stateContainer, pos), pos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        pos = Math.max(parsedDirectivePack.applySectionSensitive(stateContainer, pos, Section.BSS), pos);
                    } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                        label.register(stateContainer, pos);
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
        }


        HashMap<Integer, ParsedObject> temp = new HashMap<>();

        // Génération des directives à l'aide des pseudo-opérations '='
        for (ParsedFile file : parsedFiles) {
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
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                try {
                    if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                        if (parsedDirective.isGenerated()) {
                            generateDataPos = parsedDirective.apply(stateContainer, generateDataPos);
                        }
                    } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                        if (parsedDirectivePack.containsGenerated()) {
                            generateDataPos = parsedDirectivePack.apply(stateContainer, generateDataPos);
                        }
                    }
                } catch (SyntaxASMException e) {
                    throw e.with(inst.getKey());
                }
            }
        }

    }

    /**
     * Remplace les instructions de la forme MOV reg1, reg2, SHIFT en SHIFT reg1, reg2
     */
    protected void replaceMovShifts(StateContainer stateContainer) {
        for (ParsedFile file : parsedFiles) {
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
        //FIXME: ne prends pas en compte les fichiers multiples
        int lastInstruction = 0;
        for (ParsedFile file : parsedFiles) {
            for (Map.Entry<Integer, ParsedObject> inst : file.getParsedObjects().entrySet()) {
                if (inst.getValue() instanceof ParsedLabel label) {
                    label.register(stateContainer, lastInstruction * 4);
                    if (label.getInstruction() != null) {
                        lastInstruction++;
                    }
                }
                if (inst.getValue() instanceof ParsedInstruction) {
                    lastInstruction++;
                }
            }
        }


        if (!stateContainer.labels.containsKey("_END")) stateContainer.labels.put("_END", lastInstruction * 4);
    }

    /**
     * @param pos la position dans la mémoire
     * @return le numéro de ligne de l'instruction
     */
    public int[] getLineNumber(int pos) {
        //FIXME: ne prends pas en compte les fichiers multiples
        if (pos % 4 != 0 || pos < 0 || pos / 4 >= instructionMemory.size()) return new int[]{-1, -1};
        int[] line = instructionPosition.get(pos / 4);

        return line == null ? new int[]{-1, -1} : line;
    }
}
