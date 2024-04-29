package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterWithUpdateArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import static fr.dwightstudio.jarmemu.base.asm.instruction.UpdateMode.FD;

public class PUSHInstruction extends ParsedInstruction<Register[], Object, Object, Object>{
    public PUSHInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, ParsedArgument<Register[]> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    public PUSHInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Register[]>> getParsedArg1Class() {
        return null;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg2Class() {
        return null;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
        return null;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return null;
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
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register[] arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {
        /*Register sp = stateContainer.getRegister(13);
        STMInstruction stmdb = new STMInstruction(Condition.AL, false, Datamode, UpdateMode.DB, new RegisterWithUpdateArgument.UpdatableRegister(sp, true), arg1, arg2, arg3);
        stmdb.execute(stateContainer, false, new RegisterWithUpdateArgument.UpdatableRegister(sp, true), arg1, null, null);*/
    }

    @Override
    protected void verify(StateContainer stateContainer, Register[] arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
