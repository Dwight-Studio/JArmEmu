package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LDRExecutor implements InstructionExecutor<Register, AddressParser.UpdatableInteger, Integer, ShiftParser.ShiftFunction> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, AddressParser.UpdatableInteger arg2, Integer arg3, ShiftParser.ShiftFunction arg4) {
        //TODO: Faire l'instruction LDR
        int i1 = arg4.apply(arg3);

        switch (dataMode){
            case null -> arg1.setData(stateContainer.memory.getWord(arg2.toInt() + i1));
            case HALF_WORD -> arg1.setData(stateContainer.memory.getHalf(arg2.toInt() + i1));
            case BYTE -> arg1.setData(stateContainer.memory.getByte(arg2.toInt() + i1));
        }

        arg2.update();
    }
}
