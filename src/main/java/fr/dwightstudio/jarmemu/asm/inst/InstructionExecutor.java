package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;

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

    default void updateFlags(StateContainer stateContainer, boolean updateFlags, Register arg1, Register arg2, int i1){
        if (updateFlags){
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            stateContainer.cpsr.setC(MathUtils.hasCarry(arg2.getData(), i1));
            stateContainer.cpsr.setV(MathUtils.hasOverflow(arg2.getData(), i1));
        }
    }
}
