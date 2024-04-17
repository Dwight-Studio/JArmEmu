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

package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.InvocationTargetException;

public class DirectiveTest extends JArmEmuTest {

    private final Class<? extends ParsedDirective> clazz;

    protected StateContainer container;

    public DirectiveTest(Class<? extends ParsedDirective> clazz) {
        this.clazz = clazz;
    }

    @BeforeEach
    void setUp() {
        container = new StateContainer();
        container.getCurrentFilePos().setPos(0);
    }

    protected void execute(StateContainer container, Section section, String args) throws ASMException {
        try {
            ParsedDirective dir = clazz.getDeclaredConstructor(Section.class, String.class).newInstance(section, args);
            dir.contextualize(container);
            dir.verify(() -> new StateContainer(container));
            dir.execute(container);
            dir.offsetMemory(container);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ASMException ex) {
                throw ex;
            }
            throw new RuntimeException(e);
        }
    }
}
