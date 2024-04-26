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

import fr.dwightstudio.jarmemu.base.JArmEmuTest;
import fr.dwightstudio.jarmemu.base.asm.argument.LabelArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ShiftArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InstructionTest<A, B, C, D> extends JArmEmuTest {

    private final Class<? extends ParsedInstruction<A, B, C, D>> instructionClass;
    protected StateContainer stateContainer;
    protected StateContainer stateContainerBis;

    protected InstructionTest(Class<? extends ParsedInstruction<A, B, C, D>> instructionClass) {
        this.instructionClass = instructionClass;
    }

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
    }

    protected ShiftArgument.ShiftFunction shift() {
        try {
            return new ShiftArgument(null).getValue(stateContainer);
        } catch (ExecutionASMException | SyntaxASMException e) {
            throw new RuntimeException(e);
        }
    }

    protected ShiftArgument.ShiftFunction shift(StateContainer c, String s) {
        try {
            ShiftArgument arg = new ShiftArgument(s);
            arg.contextualize(c);
            return arg.getValue(stateContainer);
        } catch (ASMException e) {
            throw new RuntimeException(e);
        }
    }

    protected Integer label(StateContainer c, String s) {
        try {
            LabelArgument arg = new LabelArgument(s);
            arg.contextualize(c);
            return arg.getValue(stateContainer);
        } catch (ASMException e) {
            throw new RuntimeException(e);
        }
    }

    protected void execute(StateContainer container, boolean ignoreExceptions, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, A arg1, B arg2, C arg3, D arg4) throws ASMException {
        try {
            Constructor<?>[] c = instructionClass.getConstructors();
            ParsedInstruction<A, B, C, D> ins = instructionClass.getDeclaredConstructor(Condition.class,
                            boolean.class,
                            DataMode.class,
                            UpdateMode.class,
                            ParsedArgument.class,
                            ParsedArgument.class,
                            ParsedArgument.class,
                            ParsedArgument.class)
                    .newInstance(Condition.AL, updateFlags, dataMode, updateMode, null, null, null, null);
            ins.verify(() -> new StateContainer(stateContainer), arg1, arg2, arg3, arg4);
            ins.execute(container, ignoreExceptions, arg1, arg2, arg3, arg4);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ASMException ex) throw ex;
            else throw new RuntimeException(e);
        }
    }
}
