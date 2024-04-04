/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    private static final Logger logger = Logger.getLogger(ParsedInstruction.class.getSimpleName());

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

            ParsedArgument<A> arg1Temp;
            ParsedArgument<B> arg2Temp;
            ParsedArgument<C> arg3Temp;
            ParsedArgument<D> arg4Temp;

            try {
                arg1Temp = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg1);
                arg2Temp = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg2);
                arg3Temp = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg3);
                arg4Temp = getParsedArg4Class().getDeclaredConstructor(String.class).newInstance(arg4);
            } catch (InvocationTargetException exception) {
                if (hasWorkingRegister()) {
                    arg4 = arg3;
                    arg3 = arg2;
                    arg2 = arg1;

                    arg1Temp = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg1);
                    arg2Temp = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg2);
                    arg3Temp = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg3);
                    arg4Temp = getParsedArg4Class().getDeclaredConstructor(String.class).newInstance(arg4);
                } else {
                    if (exception.getCause() instanceof ASMException ex) throw ex;
                    throw new RuntimeException(exception.getTargetException());
                }
            }

            this.arg1 = arg1Temp;
            this.arg2 = arg2Temp;
            this.arg3 = arg3Temp;
            this.arg4 = arg4Temp;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
            RuntimeException e = new RuntimeException("Incorrect state: can't find ParsedArgument constructor (" + exception + ")");
            e.setStackTrace(exception.getStackTrace());
            throw e;
        } catch (InvocationTargetException exception) {
            if (exception.getCause() instanceof ASMException ex) throw ex;
            throw new RuntimeException(exception.getTargetException());
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
                        arg1.getValue(stateContainer),
                        arg2.getValue(stateContainer),
                        arg3.getValue(stateContainer),
                        arg4.getValue(stateContainer)
                );
        } else {
            if (doModifyPC()) stateContainer.getPC().add(4);
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
        try {
            arg1.contextualize(stateContainer);
            arg2.contextualize(stateContainer);
            arg3.contextualize(stateContainer);
            arg4.contextualize(stateContainer);
        } catch (ASMException exception) {
            throw exception.with(getLineNumber()).with(this).with(getFile());
        }
    }

    protected abstract void execute(StateContainer stateContainer, boolean forceExecution, A arg1, B arg2, C arg3, D arg4) throws ExecutionASMException;

    protected abstract void verify(StateContainer stateContainer, A arg1, B arg2, C arg3, D arg4) throws ASMException;

    @Override
    public final void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        try {
            arg1.verify(stateSupplier);
            arg2.verify(stateSupplier);
            arg3.verify(stateSupplier);
            arg4.verify(stateSupplier);

            StateContainer stateContainer = stateSupplier.get();
            this.verify(stateContainer,
                    arg1.getValue(stateContainer),
                    arg2.getValue(stateContainer),
                    arg3.getValue(stateContainer),
                    arg4.getValue(stateContainer)
            );
        } catch (ASMException exception) {
            throw exception.with(getLineNumber()).with(this).with(getFile());
        }
    }

    public ParsedInstruction<A, B, C, D> withLineNumber(int lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }

    public ParsedInstruction<A, B, C, D> withFile(ParsedFile parsedFile) {
        setFile(parsedFile);
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " at " + getFilePos();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedInstruction<?, ?, ?, ?> pInst)) return false;

        if (!(this.getClass().isInstance(pInst))) return false;

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
            return false;
        }

        if (!(pInst.arg2.equals(arg2))) {
            if (VERBOSE) logger.info("Difference: Arg2");
            return false;
        }

        if (!(pInst.arg3.equals(arg3))) {
            if (VERBOSE) logger.info("Difference: Arg3");
            return false;
        }

        if (!(pInst.arg4.equals(arg4))) {
            if (VERBOSE) logger.info("Difference: Arg4");
            return false;
        }

        return true;
    }
}
