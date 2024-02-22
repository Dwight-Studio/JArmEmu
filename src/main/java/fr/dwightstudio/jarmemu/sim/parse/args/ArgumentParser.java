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

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

public interface ArgumentParser<T> {

    /**
     * Analyse le texte et renvoie la valeur de l'argument.
     *
     * @param stateContainer le conteneur d'état
     * @param string         le texte à analyser
     * @return la valeur de l'argument
     * @apiNote Ce n'est pas conçu pour être appelé plusieurs fois ! (Modifie stateContainer dans certaines situations)
     */
    public T parse(@NotNull StateContainer stateContainer, @NotNull String string);

    /**
     * @return la valeur par défaut si l'argument n'est pas présent
     */
    public T none();

    /**
     * Renvoie la valeur par défaut ou met en forme l'argument
     *
     * @param i le numéro de l'argument
     * @return la valeur par défaut si l'argument n'est pas présent
     */
    public default T none(int i) {
        try {
            return none();
        } catch (SyntaxASMException exception) {
            SyntaxASMException ne = new SyntaxASMException(exception.getMessage() + " (Arg #" + i + ")");
            ne.setStackTrace(exception.getStackTrace());
            throw ne;
        }
    }
}
