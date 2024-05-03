package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.IgnoredArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class DBGInstruction extends ParsedInstruction<String, Object, Object, Object> {

    NOPInstruction nopInstruction;

    //Fixme: Should put up a notif saying that the instruction acts an NOP

    public DBGInstruction(InstructionModifier modifier, ParsedArgument<String> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) throws SyntaxASMException {
        super(modifier, arg1, arg2, arg3, arg4);
        //throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.debug"));
    }

    public DBGInstruction(InstructionModifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
        //throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.debug"));
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        super.contextualize(stateContainer);
        this.nopInstruction = new NOPInstruction(modifier, arg2, arg2, arg3, arg4);
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<String>> getParsedArg1Class() {
        return IgnoredArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, String arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {

    }

    @Override
    protected void verify(StateContainer stateContainer, String arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
