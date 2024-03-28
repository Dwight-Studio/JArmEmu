package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.*;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;

public class CMNInstruction extends ParsedInstruction<Register, Integer, ShiftArgument.ShiftFunction, Object> {
    public CMNInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Integer arg2, ShiftArgument.ShiftFunction arg3, Object arg4) throws ExecutionASMException {
        int i1 = arg3.apply(arg2);

        int result = arg1.getData() + i1; // result = arg1 + (arg3 SHIFT arg2)

        stateContainer.getCPSR().setN(result < 0);
        stateContainer.getCPSR().setZ(result == 0);
        stateContainer.getCPSR().setC(MathUtils.hasCarry(arg1.getData(), i1));
        stateContainer.getCPSR().setV(MathUtils.hasOverflow(arg1.getData(), i1));
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Integer arg2, ShiftArgument.ShiftFunction arg3, Object arg4) {

    }
}
