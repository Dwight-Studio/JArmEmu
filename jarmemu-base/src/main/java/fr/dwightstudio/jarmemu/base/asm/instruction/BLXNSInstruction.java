package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.LabelOrRegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class BLXNSInstruction extends ParsedInstruction<Integer, Object, Object, Object>{
    public BLXNSInstruction(InstructionModifier modifier, ParsedArgument<Integer> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) throws SyntaxASMException {
        super(modifier,  arg1, arg2, arg3, arg4);
        throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.wrongVersion", "BLXNS"));
    }

    public BLXNSInstruction(InstructionModifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier,  arg1, arg2, arg3, arg4);
        throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.wrongVersion", "BLXNS"));
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Integer>> getParsedArg1Class() {
        return LabelOrRegisterArgument.class;
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
        return true;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Integer arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {

    }

    @Override
    protected void verify(StateContainer stateContainer, Integer arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
