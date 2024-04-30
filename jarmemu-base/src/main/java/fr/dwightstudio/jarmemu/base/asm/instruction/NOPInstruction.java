package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.*;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class NOPInstruction extends ParsedInstruction<Object, Object, Object, Object> {

    MOVInstruction movInstruction;

    public NOPInstruction(InstructionModifier modifier, ParsedArgument<Object> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(modifier,  arg1, arg2, arg3, arg4);
    }

    public NOPInstruction(InstructionModifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier,  arg1, arg2, arg3, arg4);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        super.contextualize(stateContainer);
        RegisterArgument r0 = new RegisterArgument("R0");
        r0.contextualize(stateContainer);
        ImmediateOrRegisterArgument r0bis = new ImmediateOrRegisterArgument("R0");
        r0bis.contextualize(stateContainer);
        ShiftArgument noShift = new ShiftArgument(null);
        noShift.contextualize(stateContainer);
        this.movInstruction = new MOVInstruction(modifier,  r0, r0bis, noShift, arg4);
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Object>> getParsedArg1Class() {
        return NullArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Object arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {
        movInstruction.execute(stateContainer, ignoreExceptions);
    }

    @Override
    protected void verify(StateContainer stateContainer, Object arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
