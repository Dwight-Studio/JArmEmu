package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArrayArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterWithUpdateArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.SequencedSetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.SequencedSet;

public class PUSHInstruction extends ParsedInstruction<Register[], Object, Object, Object> {

    STMInstruction stmInstruction;

    public PUSHInstruction(Modifier modifier, ParsedArgument<Register[]> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(modifier,  arg1, arg2, arg3, arg4);
    }

    public PUSHInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier,  arg1, arg2, arg3, arg4);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        super.contextualize(stateContainer);
        RegisterWithUpdateArgument rg = new RegisterWithUpdateArgument("SP!");
        rg.contextualize(stateContainer);
        this.stmInstruction = new STMInstruction(modifier.withUpdateMode(UpdateMode.DB), rg, arg1, arg2, arg3);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register[]>> getParsedArg1Class() {
        return RegisterArrayArgument.class;
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
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register[] arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {
        stmInstruction.execute(stateContainer, ignoreExceptions);
    }

    @Override
    protected void verify(StateContainer stateContainer, Register[] arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
