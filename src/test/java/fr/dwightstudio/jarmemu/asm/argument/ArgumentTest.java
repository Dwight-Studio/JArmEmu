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

package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.InvocationTargetException;

public class ArgumentTest<T> extends JArmEmuTest {
    private final Class<? extends ParsedArgument<T>> parsedArgumentClass;
    protected StateContainer stateContainer;

    public ArgumentTest(Class<? extends ParsedArgument<T>> parsedArgumentClass) {
        this.parsedArgumentClass = parsedArgumentClass;
    }

    @BeforeEach
    void setUp() {
        stateContainer = new StateContainer();
        stateContainer.clearAndInitFiles(1);
    }

    protected T parse(String s) throws ASMException {
            try {
                ParsedArgument<T> arg = parsedArgumentClass.getDeclaredConstructor(String.class).newInstance(s);
                arg.contextualize(stateContainer);
                arg.verify(() -> new StateContainer(stateContainer));
                return arg.getValue(stateContainer);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ASMException ex) throw ex;
                throw new RuntimeException(e.getTargetException());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
    }
}
