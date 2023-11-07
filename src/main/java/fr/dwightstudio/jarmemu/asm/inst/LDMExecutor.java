package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LDMExecutor implements InstructionExecutor<RegisterWithUpdateParser.UpdatableRegister, Register[], Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, RegisterWithUpdateParser.UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) {
        int length = arg2.length;
        int value = 0;
        switch (updateMode) {
            case FD, DB -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.memory.getWord(arg1.getData() - 4 * (i + 1)));
                }
                value = - 4 * length;
            }
            case FA, IB -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.memory.getWord(arg1.getData() + 4 * (i + 1)));
                }
                value = 4 * length;
            }
            case ED, DA -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.memory.getWord(arg1.getData() - 4 * i));
                }
                value = - 4 * length;
            }
            case EA, IA -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.memory.getWord(arg1.getData() + 4 * i));
                }
                value = 4 * length;
            }
        }
        arg1.update(value);
    }
}
