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

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;

public class RotatedImmediateOrRegisterArgument extends ParsedArgument<Integer> {

    private boolean immediate;
    private RotatedImmediateArgument immediateArgument;
    private RegisterArgument registerArgument;

    public RotatedImmediateOrRegisterArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            immediate = originalString.startsWith("#") || originalString.startsWith("=") || originalString.startsWith("*");

            if (immediate) {
                immediateArgument = new RotatedImmediateArgument(originalString);
            } else {
                registerArgument = new RegisterArgument(originalString);
            }
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.contextualize(stateContainer);
            } else {
                registerArgument.contextualize(stateContainer);
            }
        }
    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        if (originalString != null) {
            if (immediate) {
                stateContainer.setAddressRegisterUpdateValue(immediateArgument.getValue(stateContainer));
                return immediateArgument.getValue(stateContainer);
            } else {
                stateContainer.setAddressRegisterUpdateValue(registerArgument.getValue(stateContainer).getData());
                return registerArgument.getValue(stateContainer).getData();
            }
        } else {
            return 0; // FIXME: Pas sûr de ça, est-ce que cela pose un problème si il n'y a pas d'argument?
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.verify(stateSupplier);
            } else {
                registerArgument.verify(stateSupplier);
            }

            super.verify(stateSupplier);
        }
    }
}
