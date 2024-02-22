package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public abstract class ParsedInstruction<A, B, C, D> extends ParsedObject {

    protected final boolean updateFlags;
    protected final DataMode dataMode;
    protected UpdateMode updateMode;
    protected final ParsedArgument<A> arg1;
    protected final ParsedArgument<B> arg2;
    protected final ParsedArgument<C> arg3;
    protected final ParsedArgument<D> arg4;

    public ParsedInstruction(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        try {
            this.updateFlags = updateFlags;
            this.dataMode = dataMode;
            this.updateMode = updateMode;

            if (arg1 != null) {
                this.arg1 = getParsedArg0Class().getDeclaredConstructor(String.class).newInstance(arg1);
            } else {
                this.arg1 = null;
            }

            if (arg2 != null) {
                this.arg2 = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg2);
            } else {
                this.arg2 = null;
            }

            if (arg3 != null) {
                this.arg3 = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg3);
            } else {
                this.arg3 = null;
            }

            if (arg4 != null) {
                this.arg4 = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg4);
            } else {
                this.arg4 = null;
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            throw new RuntimeException("Incorrect state: can't find ParsedArgument constructor");
        }
    }

    protected abstract Class<? extends ParsedArgument<A>> getParsedArg0Class();

    protected abstract Class<? extends ParsedArgument<B>> getParsedArg1Class();

    protected abstract Class<? extends ParsedArgument<C>> getParsedArg2Class();

    protected abstract Class<? extends ParsedArgument<D>> getParsedArg3Class();

    /**
     * Exécute l'instruction sur le conteneur d'état
     *
     * @param stateContainer le conteneur d'état courant
     * @param forceExecution pour forcer l'exécution
     * @throws ExecutionASMException en cas d'erreur
     */
    public final void execute(StateContainer stateContainer, boolean forceExecution) throws ExecutionASMException {
        this.execute(
                stateContainer,
                forceExecution,
                arg1.getValue(stateContainer),
                arg2.getValue(stateContainer),
                arg3.getValue(stateContainer),
                arg4.getValue(stateContainer)
        );
    }

    /**
     * @return vrai si l'instruction modifie le PC
     */
    public abstract boolean doModifyPC();

    /**
     * @return vrai si l'instruction possède un registre de travail (un registre utilisé comme destination et comme source)
     */
    public abstract boolean hasWorkingRegister();

    protected abstract void execute(StateContainer stateContainer, boolean forceExecution, A arg1, B arg2, C arg3, D arg4) throws ExecutionASMException;

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        if (arg1 != null) {
            arg1.verify(stateSupplier, currentLine);
        }

        if (arg2 != null) {
            arg2.verify(stateSupplier, currentLine);
        }

        if (arg3 != null) {
            arg3.verify(stateSupplier, currentLine);
        }

        if (arg4 != null) {
            arg4.verify(stateSupplier, currentLine);
        }
    }
}
