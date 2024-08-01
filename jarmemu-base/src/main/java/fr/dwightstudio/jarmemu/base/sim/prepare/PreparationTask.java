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

package fr.dwightstudio.jarmemu.base.sim.prepare;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class PreparationTask<T> {

    protected final PreparationStream stream;

    protected PreparationTask(PreparationStream stream) {
        this.stream = stream;
    }

    /**
     * Contextualize each element according to filters
     *
     * @param container the state container used to contextualize
     */
    public abstract PreparationStream contextualize(StateContainer container) throws ASMException;

    /**
     * Verify each element according to filters
     *
     * @param stateSupplier a state container supplier to contextualize the tests
     */
    public abstract PreparationStream verify(Supplier<StateContainer> stateSupplier) throws ASMException;

    /**
     * Perform an operation on each element according to filters
     *
     * @param consumer the operation to perform
     */
    public abstract PreparationStream perform(Consumer<T> consumer);
}
