package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.NotImplementedASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.SequencedSetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.SequencedSet;

public class CLREXInstruction extends ParsedInstruction<Object, Object, Object, Object> {
    public CLREXInstruction(Modifier modifier, ParsedArgument<Object> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
        throw new NotImplementedASMException(JArmEmuApplication.formatMessage("%exception.instruction.notImplemented", "CLREX"));
    }

    public CLREXInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
        throw new NotImplementedASMException(JArmEmuApplication.formatMessage("%exception.instruction.notImplemented", "CLREX"));
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg1Class() {
        return NullArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg2Class() {
        return NullArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
        return NullArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return NullArgument.class;
    }

    @Override
    @NotNull
    public SequencedSet<Class<? extends Enum<? extends ModifierParameter>>>getModifierParameterClasses() {
        return SequencedSetUtils.of(Condition.class);
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean isWorkingRegisterCompatible() {
        return false;
    }

    @Override
    public int getMemoryCode(StateContainer stateContainer, int pos) {
        return 0;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Object arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {

    }

    @Override
    protected void verify(StateContainer stateContainer, Object arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
