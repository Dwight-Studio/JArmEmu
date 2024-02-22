package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.lang.reflect.InvocationTargetException;

public abstract class ParsedInstruction<A, B, C, D> extends ParsedObject {

    private ParsedArgument<A> arg0;
    private ParsedArgument<B> arg1;
    private ParsedArgument<C> arg2;
    private ParsedArgument<D> arg3;

    public ParsedInstruction(String arg0, String arg1, String arg2, String arg3) throws BadArgumentASMException {
        try {
            if (arg0 != null) {
                this.arg0 = getParsedArg0Class().getDeclaredConstructor(String.class).newInstance(arg0);
            }

            if (arg1 != null) {
                this.arg1 = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg1);
            }

            if (arg2 != null) {
                this.arg2 = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg2);
            }

            if (arg3 != null) {
                this.arg3 = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg3);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            throw new RuntimeException("Incorrect state: can't find ParsedArgument constructor");
        }
    }

    protected abstract Class<ParsedArgument<A>> getParsedArg0Class();

    protected abstract Class<ParsedArgument<B>> getParsedArg1Class();

    protected abstract Class<ParsedArgument<C>> getParsedArg2Class();

    protected abstract Class<ParsedArgument<D>> getParsedArg3Class();

    public final void execute(StateContainer stateContainer, boolean forceExecution) throws ExecutionASMException {
        this.execute(
                stateContainer,
                forceExecution,
                arg0.getValue(stateContainer),
                arg1.getValue(stateContainer),
                arg2.getValue(stateContainer),
                arg3.getValue(stateContainer)
        );
    }

    protected abstract void execute(StateContainer stateContainer, boolean forceExecution, A arg0, B arg1, C arg2, D arg3) throws ExecutionASMException;
}
