package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public interface InstructionExecutor<A,B,C,D> {

    /**
     * Execution de l'instruction
     * @param stateContainer Le conteneur d'état sur lequel effectuer l'exécution
     * @param updateFlags Doit-on mettre à jour les flags
     * @param dataMode Type de donnée (Byte, HalfWord, Word) si applicable
     * @param arg1 Le premier argument
     * @param arg2 Le deuxième argument
     * @param arg3 Le troisième argument
     * @param arg4 Le quatrième argument
     */
    void execute(StateContainer stateContainer,
                 boolean updateFlags,
                 DataMode dataMode,
                 UpdateMode updateMode,
                 A arg1, B arg2, C arg3, D arg4);

}
