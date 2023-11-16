package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.IllegalDataWritingASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ShiftParser;

public class STRExecutor implements InstructionExecutor<Register, AddressParser.UpdatableInteger, Integer, ShiftParser.ShiftFunction> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, AddressParser.UpdatableInteger arg2, Integer arg3, ShiftParser.ShiftFunction arg4) {
        int i1 = arg4.apply(arg3);
        int address = arg2.toInt() + i1;

        if (!forceExecution) {
            int dataLength;

            switch (dataMode) {
                case HALF_WORD -> dataLength = 2;
                case BYTE -> dataLength = 1;
                case null, default -> dataLength = 4;
            }

            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
            if (address < stateContainer.getLastAddressROData() && address >= stateContainer.getSymbolsAddress()) throw new IllegalDataWritingASMException();
        }

        switch (dataMode){
            case null -> stateContainer.memory.putWord(address, arg1.getData());
            case HALF_WORD -> stateContainer.memory.putHalf(address, (short) arg1.getData());
            case BYTE -> stateContainer.memory.putByte(address, (byte) arg1.getData());
        }

        arg2.update();
    }
}
