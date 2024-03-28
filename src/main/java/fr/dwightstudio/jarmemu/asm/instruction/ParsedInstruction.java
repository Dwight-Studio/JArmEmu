package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class ParsedInstruction<A, B, C, D> extends ParsedObject implements Contextualized {

    private static final Logger logger = Logger.getLogger(ParsedInstruction.class.getName());

    protected final Condition condition;
    protected final boolean updateFlags;
    protected final DataMode dataMode;
    protected UpdateMode updateMode;
    protected final ParsedArgument<A> arg1;
    protected final ParsedArgument<B> arg2;
    protected final ParsedArgument<C> arg3;
    protected final ParsedArgument<D> arg4;

    public ParsedInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        try {
            this.condition = condition;
            this.updateFlags = updateFlags;
            this.dataMode = dataMode;
            this.updateMode = updateMode;

            ParsedArgument<A> arg1Temp = null;
            ParsedArgument<B> arg2Temp = null;
            ParsedArgument<C> arg3Temp = null;
            ParsedArgument<D> arg4Temp = null;

            try {
                if (arg1 != null) {
                    arg1Temp = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg1);
                }

                if (arg2 != null) {
                    arg2Temp = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg2);
                }

                if (arg3 != null) {
                    arg3Temp = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg3);
                }

                if (arg4 != null) {
                    arg4Temp = getParsedArg4Class().getDeclaredConstructor(String.class).newInstance(arg4);
                }
            } catch (InvocationTargetException exception) {
                if (hasWorkingRegister()) {
                    arg4 = arg3;
                    arg3 = arg2;
                    arg2 = arg1;

                    if (arg1 != null) {
                        arg1Temp = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg1);
                    }

                    if (arg1 != null) {
                        arg2Temp = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg2);
                    }

                    if (arg3 != null) {
                        arg3Temp = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg3);
                    }

                    if (arg4 != null) {
                        arg4Temp = getParsedArg4Class().getDeclaredConstructor(String.class).newInstance(arg4);
                    }
                } else {
                    if (exception.getCause() instanceof ASMException ex) throw ex;
                    throw new RuntimeException(exception.getTargetException());
                }
            }

            this.arg1 = arg1Temp;
            this.arg2 = arg2Temp;
            this.arg3 = arg3Temp;
            this.arg4 = arg4Temp;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            throw new RuntimeException("Incorrect state: can't find ParsedArgument constructor");
        }
    }

    protected abstract Class<? extends ParsedArgument<A>> getParsedArg1Class();

    protected abstract Class<? extends ParsedArgument<B>> getParsedArg2Class();

    protected abstract Class<? extends ParsedArgument<C>> getParsedArg3Class();

    protected abstract Class<? extends ParsedArgument<D>> getParsedArg4Class();

    /**
     * Exécute l'instruction sur le conteneur d'état
     *
     * @param stateContainer le conteneur d'état courant
     * @param forceExecution pour forcer l'exécution
     * @throws ExecutionASMException en cas d'erreur
     */
    public final void execute(StateContainer stateContainer, boolean forceExecution) throws ExecutionASMException {
        if (condition.eval(stateContainer)) {
                this.execute(
                        stateContainer,
                        forceExecution,
                        arg1 != null ? arg1.getValue(stateContainer) : null,
                        arg2 != null ? arg2.getValue(stateContainer) : null,
                        arg3 != null ? arg3.getValue(stateContainer) : null,
                        arg4 != null ? arg4.getValue(stateContainer) : null
                );
        }
    }

    /**
     * @return vrai si l'instruction modifie le PC
     */
    public abstract boolean doModifyPC();

    /**
     * @return vrai si l'instruction possède un registre de travail (un registre utilisé comme destination et comme source)
     */
    public abstract boolean hasWorkingRegister();

    /**
     * Contextualise l'instruction dans le conteneur d'état initial, après définition des constantes.
     *
     * @param stateContainer le conteneur d'état initial
     */
    public void contextualize(StateContainer stateContainer) throws ASMException {
        if (arg1 != null) {
            arg1.contextualize(stateContainer);
        }

        if (arg2 != null) {
            arg2.contextualize(stateContainer);
        }

        if (arg3 != null) {
            arg3.contextualize(stateContainer);
        }

        if (arg4 != null) {
            arg4.contextualize(stateContainer);
        }
    }

    protected abstract void execute(StateContainer stateContainer, boolean forceExecution, A arg1, B arg2, C arg3, D arg4) throws ExecutionASMException;

    protected abstract void verify(StateContainer stateContainer, A arg1, B arg2, C arg3, D arg4) throws ASMException;

    @Override
    public final void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        try {
            if (arg1 != null) {
                arg1.verify(stateSupplier);
            }

            if (arg2 != null) {
                arg2.verify(stateSupplier);
            }

            if (arg3 != null) {
                arg3.verify(stateSupplier);
            }

            if (arg4 != null) {
                arg4.verify(stateSupplier);
            }

            StateContainer stateContainer = stateSupplier.get();
            this.verify(stateContainer,
                    arg1 != null ? arg1.getValue(stateContainer) : null,
                    arg2 != null ? arg2.getValue(stateContainer) : null,
                    arg3 != null ? arg3.getValue(stateContainer) : null,
                    arg4 != null ? arg4.getValue(stateContainer) : null
            );
        } catch (ASMException exception) {
            throw exception.with(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedInstruction<?, ?, ?, ?> pInst)) return false;

        if (!(pInst.updateFlags == this.updateFlags)) {
            if (ParsedObject.VERBOSE) logger.info("Difference: Flags");
            return false;
        }

        if (pInst.dataMode == null) {
            if (!(this.dataMode == null)) {
                if (VERBOSE) logger.info("Difference: DataMode (Null)");
                return false;
            }
        } else {
            if (!(pInst.dataMode.equals(this.dataMode))) {
                if (VERBOSE) logger.info("Difference: DataMode");
                return false;
            }
        }

        if (pInst.updateMode == null) {
            if (!(this.updateMode == null)) {
                if (VERBOSE) logger.info("Difference: UpdateMode (Null)");
                return false;
            }
        } else {
            if (!(pInst.updateMode.equals(this.updateMode))) {
                if (VERBOSE) logger.info("Difference: UpdateMode");
                return false;
            }
        }

        if (!(pInst.condition == this.condition)) {
            if (VERBOSE) logger.info("Difference: Condition");
            return false;
        }

        if (!(pInst.arg1.equals(arg1))) {
            if (VERBOSE) logger.info("Difference: Arg1");
        }

        if (!(pInst.arg2.equals(arg2))) {
            if (VERBOSE) logger.info("Difference: Arg2");
        }

        if (!(pInst.arg3.equals(arg3))) {
            if (VERBOSE) logger.info("Difference: Arg3");
        }

        if (!(pInst.arg4.equals(arg4))) {
            if (VERBOSE) logger.info("Difference: Arg4");
        }

        return true;
    }

    public ParsedInstruction<A, B, C, D> withLineNumber(int lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }
}
