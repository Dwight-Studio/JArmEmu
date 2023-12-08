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

import fr.dwightstudio.jarmemu.sim.exceptions.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeInterpreter {
    private final Logger logger = Logger.getLogger(getClass().getName());

    // ParsedObjects
    protected CodePreparator codePreparator;

    // Simulation
    protected StateContainer stateContainer;
    protected boolean jumped;

    public CodeInterpreter() {
        this.stateContainer = new StateContainer();
    }

    /**
     * Charge des instructions parsées dans l'exécuteur
     *
     * @param sourceParser le parseur de source utilisé
     */
    public SyntaxASMException[] load(SourceParser sourceParser, int stackAddress, int symbolAddress, List<SourceScanner> fileSources) {
        this.codePreparator = new CodePreparator(stackAddress, symbolAddress);
        return this.codePreparator.load(sourceParser, fileSources);
    }

    /**
     * Execute le code se trouvant sur la ligne courante
     *
     * @param forceExecution ignore les erreurs d'exécution non bloquantes
     */
    public synchronized void executeCurrentLine(boolean forceExecution) throws ExecutionASMException {

        // Remise à zéro des drapeaux de ligne des parseurs
        AddressParser.reset(this.stateContainer);


        ExecutionASMException executionException = null;
        List<ParsedInstruction> instructions = codePreparator.getInstructionMemory();

        if (getPC().getData() % 4 == 0 && getPC().getData() >= 0 && (getPC().getData() / 4) < instructions.size()) {
            ParsedInstruction instruction = instructions.get(getPC().getData() / 4);

            try {
                instruction.execute(stateContainer, forceExecution);
            } catch (ExecutionASMException exception) {
                executionException = exception;
            }

            if (forceExecution || executionException == null) {
                if (instruction.getInstruction().doModifyPC()) {
                    jumped = true;
                } else {
                    getPC().add(4);
                    jumped = false;
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
        resetState();

        try {
            int fileIndex = stateContainer.getGlobal("_START");
            stateContainer.setFileIndex(fileIndex);
            stateContainer.getPC().setData(stateContainer.getLabelsInFiles().get(fileIndex).get("_START"));
            logger.warning("Setting PC to address of label '_START' positioned at " + stateContainer.getPC().getData());
        } catch (Exception e) {
            logger.warning("Can't find position of label '_START'");
            stateContainer.getPC().setData(0);
        }
    }

    /**
     * Reinitialise l'état courant
     */
    public void resetState() {
        logger.info("Resetting program state");
        stateContainer = codePreparator.processState();
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
    private Register getPC() {
        if (stateContainer == null) {
            throw new IllegalStateException("Can't get PC from null state");
        } else {
            return stateContainer.getPC();
        }
    }

    public boolean hasJumped() {
        return jumped;
    }

    public FilePos getCurrentLine() {
        FilePos pos = codePreparator.getLineNumber(getCurrentPosition());
        return pos == null ? null : pos.freeze();
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
