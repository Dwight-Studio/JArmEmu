/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public interface InstructionExecutor<A,B,C,D> {

    /**
     * Execution de l'instruction
     *
     * @param stateContainer Le conteneur d'état sur lequel effectuer l'exécution
     * @param forceExecution ignore les erreurs d'exécution non bloquantes
     * @param updateFlags    Doit-on mettre à jour les flags
     * @param dataMode       Type de donnée (Byte, HalfWord, Word) si applicable
     * @param arg1           Le premier argument
     * @param arg2           Le deuxième argument
     * @param arg3           Le troisième argument
     * @param arg4           Le quatrième argument
     */
    void execute(StateContainer stateContainer,
                 boolean forceExecution, boolean updateFlags,
                 DataMode dataMode,
                 UpdateMode updateMode,
                 A arg1, B arg2, C arg3, D arg4);
}
