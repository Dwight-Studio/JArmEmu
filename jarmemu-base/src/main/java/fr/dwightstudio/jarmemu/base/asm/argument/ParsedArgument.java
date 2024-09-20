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

package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.Contextualized;
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedArgument<T> extends ParsedObject implements Contextualized {

    protected final String originalString;

    /**
     * Parse the string to populate the internal variables.
     *
     * @param originalString la chaîne de caractères
     */
    public ParsedArgument(String originalString) {
        this.originalString = originalString;
    }

    /**
     * Contextualise the argument in the supplied state container.
     *
     * @param stateContainer the state container in which contextualise the argument
     */
    public abstract void contextualize(StateContainer stateContainer) throws ASMException;

    /**
     * @param stateContainer the current state container
     * @return the associated value for this state container
     */
    public abstract T getValue(StateContainer stateContainer) throws ExecutionASMException;

    /**
     * @return the original string of the argument (as in the editor)
     */
    public final String getOriginalString() {
        return originalString;
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        getValue(stateSupplier.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass().isInstance(obj)) {
            ParsedArgument<?> arg = (ParsedArgument<?>) obj;
            if (originalString == null) {
                return arg.originalString == null;
            }
            return originalString.equalsIgnoreCase(arg.originalString);
        } else return false;
    }
}
