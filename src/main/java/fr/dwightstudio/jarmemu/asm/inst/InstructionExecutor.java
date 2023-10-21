package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public interface InstructionExecutor {

    /**
     * Execution de l'instruction sous condition
     * @param stateContainer Le conteneur d'état sur lequel effectuer l'exécution
     * @param condition La condition à verifier
     * @param updateFlags Doit-on mettre à jour les flags
     * @param dataMode Type de donnée (Byte, HalfWord, Word) si applicable
     * @param updateMode Mode de mise à jour
     * @param arg1 Le premier argument
     * @param arg2 Le deuxième argument
     * @param arg3 Le troisième argument
     * @param arg4 Le quatrième argument
     */
    default void conditionalExecute(StateContainer stateContainer,
                                    Condition condition,
                                    boolean updateFlags,
                                    DataMode dataMode,
                                    UpdateMode updateMode,
                                    int arg1, int arg2, int arg3, int arg4) {

        if (condition.eval(stateContainer)) this.execute(stateContainer, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);

    }

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
                 int arg1, int arg2, int arg3, int arg4);

}
