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

package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.Contextualized;
import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class ParsedInstruction<A, B, C, D> extends ParsedObject implements Contextualized {

    private static final Logger logger = Logger.getLogger(ParsedInstruction.class.getSimpleName());

    private boolean usingWorkingRegister = false;

    protected final Modifier modifier;
    protected final ParsedArgument<A> arg1;
    protected final ParsedArgument<B> arg2;
    protected final ParsedArgument<C> arg3;
    protected final ParsedArgument<D> arg4;

    public ParsedInstruction(@NotNull Modifier modifier, ParsedArgument<A> arg1, ParsedArgument<B> arg2, ParsedArgument<C> arg3, ParsedArgument<D> arg4) {
        this.modifier = modifier;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
    }

    public ParsedInstruction(@NotNull Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        try {
            this.modifier = modifier;

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
                if (isWorkingRegisterCompatible()) {
                    arg4 = arg3;
                    arg3 = arg2;
                    arg2 = arg1;

                    arg1Temp = getParsedArg1Class().getDeclaredConstructor(String.class).newInstance(arg1);
                    arg2Temp = getParsedArg2Class().getDeclaredConstructor(String.class).newInstance(arg2);
                    arg3Temp = getParsedArg3Class().getDeclaredConstructor(String.class).newInstance(arg3);
                    arg4Temp = getParsedArg4Class().getDeclaredConstructor(String.class).newInstance(arg4);

                    usingWorkingRegister = true;
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
            if (exception.getTargetException() instanceof ASMException ex) throw ex;
            throw new RuntimeException(exception.getTargetException());
        }
    }

    public abstract @NotNull Class<? extends ParsedArgument<A>> getParsedArg1Class();

    public abstract @NotNull Class<? extends ParsedArgument<B>> getParsedArg2Class();

    public abstract @NotNull Class<? extends ParsedArgument<C>> getParsedArg3Class();

    public abstract @NotNull Class<? extends ParsedArgument<D>> getParsedArg4Class();

    public abstract @NotNull Set<Class<? extends Enum<? extends ModifierParameter>>> getModifierParameterClasses();

    /**
     * Execute the instruction on the state container
     *
     * @param stateContainer the current state container
     * @param forceExecution true if you want to force execution (ignore execution exception)
     * @throws ExecutionASMException when the execution generate an exception
     */
    public final void execute(StateContainer stateContainer, boolean forceExecution) throws ExecutionASMException {
        if (modifier.condition().eval(stateContainer)) {
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
     * @return true if the instruction modifies the PC (i.e. perform a jump)
     * @implNote if true, prevents the emulator from automatically incrementing the PC (the implement should handle it)
     */
    public abstract boolean doModifyPC();

    /**
     * @return true if the instruction can use a working register
     * @implNote if true and when the instruction lacks an argument, the emulator will try to duplicate the first one
     */
    public abstract boolean isWorkingRegisterCompatible();

    public boolean isUsingWorkingRegister() {
        return usingWorkingRegister;
    }

    /**
     * Contextualize the instruction in the initial state container (with the constants)
     *
     * @param stateContainer the state container used to contextualize
     */
    public void contextualize(StateContainer stateContainer) throws ASMException {
        try {
            if (arg1 != null) arg1.contextualize(stateContainer);
            if (arg1 != null) arg2.contextualize(stateContainer);
            if (arg1 != null) arg3.contextualize(stateContainer);
            if (arg1 != null) arg4.contextualize(stateContainer);
        } catch (ASMException exception) {
            throw exception.with(getLineNumber()).with(this).with(getFile());
        }
    }

    /**
     * @param stateContainer the state container used to contextualize
     * @param pos
     * @return a 32 bit value representing the instruction in program memory
     * @apiNote this value is for education purpose, it is not actually used by the emulator
     */
    public abstract int getMemoryCode(StateContainer stateContainer, int pos) throws ExecutionASMException;

    protected abstract void execute(StateContainer stateContainer, boolean ignoreExceptions, A arg1, B arg2, C arg3, D arg4) throws ExecutionASMException;

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

    public final void verify(Supplier<StateContainer> stateSupplier, A arg1, B arg2, C arg3, D arg4) throws ASMException {
        this.verify(stateSupplier.get(), arg1, arg2, arg3, arg4);
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

        if (!(pInst.modifier.equals(this.modifier))) {
            if (VERBOSE) {
                logger.info("Difference: Modifier");
                logger.info(this.modifier.toString());
                logger.info(pInst.modifier.toString());
            }
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
