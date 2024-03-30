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

import fr.dwightstudio.jarmemu.asm.Contextualized;
import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedArgument<T> extends ParsedObject implements Contextualized {

    protected final String originalString;

    /**
     * Analyse la chaîne de caractères en entrée pour définir les paramètres internes de l'argument.
     *
     * @param originalString la chaîne de caractères
     */
    public ParsedArgument(String originalString) {
        this.originalString = originalString;
    }

    /**
     * Contextualise l'argument dans le conteneur d'état initial, après définition des constantes.
     *
     * @param stateContainer le conteneur d'état initial
     */
    public abstract void contextualize(StateContainer stateContainer) throws ASMException;

    /**
     * @param stateContainer le conteneur d'état courant
     * @return la valeur associée à l'argument pour le conteneur d'état
     */
    public abstract T getValue(StateContainer stateContainer) throws ExecutionASMException;

    public final String getOriginalString() {
        return originalString;
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        getValue(stateSupplier.get());
    }
}
