package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.LabelArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.SequencedSetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.SequencedSet;

public class CBNZInstruction extends ParsedInstruction<Register, Integer, Object, Object> {
    public CBNZInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<Integer> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public CBNZInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Integer>> getParsedArg2Class() {
        return LabelArgument.class;
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
        return true;
    }

    @Override
    public boolean isWorkingRegisterCompatible() {
        return false;
    }

    @Override
    public int getMemoryCode(StateContainer stateContainer) {
        return 0;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Integer arg2, Object arg3, Object arg4) throws ExecutionASMException {
        if (arg2 == stateContainer.getPC().getData()) throw new StuckExecutionASMException();
        int value = arg1.getData();
        if (value != 0) {
            stateContainer.getPC().setData(arg2);
        } else {
            stateContainer.getPC().add(4);
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Integer arg2, Object arg3, Object arg4) throws ASMException {

    }
}
