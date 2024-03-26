package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.*;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

//FIXME: Ajouter la transcription en Shift

/*
    public ParsedInstruction convertMovToShift(StateContainer stateContainer) {
        if (this.instruction == OInstruction.MOV) {
            ArgumentParser[] argParsers = instruction.getArgParsers();
            if (originalArgs[2] == null) return this;
            if (argParsers[2].parse(stateContainer, originalArgs[2].toUpperCase()) instanceof ShiftParser.ShiftFunction){
                try {
                    return new ParsedInstruction(OInstruction.valueOf(originalArgs[2].substring(0, 3).toUpperCase()), this.condition, this.updateFlags, this.dataMode, this.updateMode, originalArgs[0], originalArgs[1], originalArgs[2].substring(3), null, fileIndex);
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                }
            }
        }
        return this;
    }
 */

public class MOVInstruction extends ParsedInstruction<Register, Integer, ShiftArgument.ShiftFunction, Object> {
    public MOVInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg2Class() {
        return RotatedImmediateOrRegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<ShiftArgument.ShiftFunction>> getParsedArg3Class() {
        return ShiftArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return NullArgument.class;
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Integer arg2, ShiftArgument.ShiftFunction arg3, Object arg4) throws ASMException {
        int i1 = arg3.apply(arg2);

        arg1.setData(i1); // arg1 = (arg3 SHIFT arg2)

        if(updateFlags){
            stateContainer.getCPSR().setN(arg1.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
        }
    }
}
