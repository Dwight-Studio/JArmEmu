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

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeInterpreter {
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    // ParsedObjects
    protected CodePreparator codePreparator;

    // Simulation
    protected StateContainer initialState;
    protected StateContainer stateContainer;

    public CodeInterpreter() {
        this.stateContainer = new StateContainer();
    }

    /**
     * Charge des instructions parsées dans l'exécuteur
     *
     * @param sourceParser le parseur de source utilisé
     */
    public ASMException[] load(SourceParser sourceParser, List<SourceScanner> fileSources) {
        this.codePreparator = new CodePreparator();
        return this.codePreparator.load(sourceParser, fileSources);
    }

    /**
     * Execute le code se trouvant sur la ligne courante
     *
     * @param forceExecution ignore les erreurs d'exécution non bloquantes
     */
    public synchronized void executeCurrentLine(boolean forceExecution) throws ExecutionASMException {

        // Remise à zéro des drapeaux de ligne des parseurs
        stateContainer.resetAddressRegisterUpdateValue();

        ExecutionASMException executionException = null;
        List<ParsedInstruction<?, ?, ?, ?>> instructions = codePreparator.getInstructionMemory();

        if (getPC().getData() % 4 == 0 && getPC().getData() >= 0 && (getPC().getData() / 4) < instructions.size()) {
            ParsedInstruction<?, ?, ?, ?> instruction = instructions.get(getPC().getData() / 4);

            // Mise à jour de l'indice du fichier
            stateContainer.getCurrentFilePos().setFileIndex(instruction.getFile().getIndex());

            try {
                instruction.execute(stateContainer, forceExecution);
            } catch (ExecutionASMException exception) {
                executionException = exception;
            }

            if (forceExecution || executionException == null) {
                if (!instruction.doModifyPC()) {
                    getPC().add(4);
                }
            }
        } else {
            logger.log(Level.SEVERE, "Executing non-existant instruction of position " + getPC().getData());
        }

        if (executionException != null) throw executionException;
    }

    /**
     * Revient à la première ligne
     */
    public void restart() {
        stateContainer = new StateContainer(initialState);

        try {
            int fileIndex = stateContainer.getGlobal("_START");
            stateContainer.getCurrentFilePos().setFileIndex(fileIndex);
            stateContainer.getPC().setData(stateContainer.getLabelsInFiles().get(fileIndex).get("_START"));
            logger.warning("Setting PC to address of label '_START' positioned at " + stateContainer.getPC().getData());
        } catch (Exception e) {
            logger.warning("Can't find position of label '_START'");
            stateContainer.getPC().setData(0);
        }
    }

    /**
     * Vérifie que l'instruction suivante existe.
     *
     * @return vrai s'il y a une ligne, faux sinon
     */
    public boolean hasNext() {
        return getPC().getData() / 4 < getInstructionCount();
    }

    /**
     * @return le nombre de lignes
     */
    public int getInstructionCount() {
        return codePreparator.getInstructionMemory().size();
    }

    public StateContainer getStateContainer() {
        return stateContainer;
    }

    /**
     * Initialise le conteneur d'état à l'aide du préparateur de code
     *
     * @param stackAddress l'adresse de la pile
     * @param symbolsAddress l'adresse des symboles
     * @return les erreurs
     */
    public ASMException[] initiate(int stackAddress, int symbolsAddress) {
        logger.info("Initiating state container");
        initialState = new StateContainer(stackAddress, symbolsAddress);
        return codePreparator.initiate(initialState);
    }

    /**
     * Initialise le conteneur d'état à l'aide du préparateur de code
     *
     * @return les erreurs
     */
    public ASMException[] initiate() {
        logger.info("Initiating state container");
        initialState = new StateContainer();
        return codePreparator.initiate(initialState);
    }

    private Register getPC() {
        if (stateContainer == null) {
            throw new IllegalStateException("Can't get PC from null state");
        } else {
            return stateContainer.getPC();
        }
    }

    public FilePos getCurrentLine() {
        FilePos pos = codePreparator.getLineNumber(getCurrentPosition());
        return pos == null ? null : pos.freeze();
    }

    public int getNestingCount() {
        return stateContainer.getNestingCount();
    }

    public FilePos getLineNumber(int pos) {
        return codePreparator.getLineNumber(pos);
    }

    public int getPosition(FilePos line) {
        return codePreparator.getPosition(line);
    }

    public int getCurrentPosition() {
        return getPC().getData();
    }
}
