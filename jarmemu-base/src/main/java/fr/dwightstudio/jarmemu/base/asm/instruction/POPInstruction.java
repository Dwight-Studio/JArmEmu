package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArrayArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterWithUpdateArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class POPInstruction extends ParsedInstruction<Register[], Object, Object, Object> {

    LDMInstruction ldmInstruction;

    public POPInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, ParsedArgument<Register[]> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    public POPInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        super.contextualize(stateContainer);
        RegisterWithUpdateArgument rg = new RegisterWithUpdateArgument("SP!");
        rg.contextualize(stateContainer);
        this.ldmInstruction = new LDMInstruction(condition, updateFlags, dataMode, UpdateMode.IA, rg, arg1, arg2, arg3);
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Register[]>> getParsedArg1Class() {
        return RegisterArrayArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg2Class() {
        return NullArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
        return NullArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
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
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register[] arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {
        ldmInstruction.execute(stateContainer, ignoreExceptions);
    }

    @Override
    protected void verify(StateContainer stateContainer, Register[] arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
