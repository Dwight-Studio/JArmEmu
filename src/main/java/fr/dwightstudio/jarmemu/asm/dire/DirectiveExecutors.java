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

package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class DirectiveExecutors {

    public static final DirectiveExecutor NOT_IMPLEMENTED = new DirectiveExecutor() {
        @Override
        public void apply(StateContainer stateContainer, String args, FilePos currentPos, Section section) {
            throw new IllegalStateException("Directive not implemented");
        }

        @Override
        public void computeDataLength(StateContainer stateContainer, String args, FilePos currentPos, Section section) {
            throw new IllegalStateException("Directive not implemented");
        }
    };

    // Consts
    public static final GlobalExecutor GLOBAL = new GlobalExecutor();
    public static final EquivalentExecutor EQUIVALENT = new EquivalentExecutor();

    // Data
    public static final WordExecutor WORD = new WordExecutor();
    public static final HalfExecutor HALF = new HalfExecutor();
    public static final ByteExecutor BYTE = new ByteExecutor();
    public static final SpaceExecutor SPACE = new SpaceExecutor();
    public static final ASCIIExecutor ASCII = new ASCIIExecutor();
    public static final ASCIZExecutor ASCIZ = new ASCIZExecutor();
    public static final FillExecutor FILL = new FillExecutor();

    // Other
    public static final AlignExecutor ALIGN = new AlignExecutor();

}
