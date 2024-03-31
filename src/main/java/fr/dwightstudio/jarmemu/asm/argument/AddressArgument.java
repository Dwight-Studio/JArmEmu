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
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.Arrays;
import java.util.function.Supplier;

public class AddressArgument extends ParsedArgument<AddressArgument.UpdatableInteger> {

    private final int mode;
    private boolean updateNow;
    private RegisterArgument registerArgument1;

    private ImmediateArgument immediateArgument;
    private RegisterArgument registerArgument2;
    private ShiftArgument shiftArgument;

    public AddressArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            String string = originalString;

            if (originalString.startsWith("=")) {
                mode = 1;
                return;
            } else if (!originalString.startsWith("[")) {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidAddress", originalString));
            }

            updateNow = string.endsWith("!");

            if (updateNow) string = string.substring(0, string.length() - 1);

            if (string.endsWith("]")) {
                String mem = string.substring(1, string.length() - 1);
                String[] mems = mem.split(",");

                mems = Arrays.stream(mems).map(String::strip).toArray(String[]::new);

                registerArgument1 = new RegisterArgument(mems[0]);

                if (mems.length == 1) {
                    mode = 2;

                } else if (mems.length == 2) {
                    if (mems[1].startsWith("#")) {
                        immediateArgument = new ImmediateArgument(mems[1]);
                        mode = 3;
                    } else {
                        registerArgument2 = new RegisterArgument(mems[1]);
                        mode = 4;
                    }

                } else if (mems.length == 3) {
                    shiftArgument = new ShiftArgument(mems[2]);
                    registerArgument2 = new RegisterArgument(mems[1]);
                    mode = 5;
                } else {
                    throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidAddress", originalString));
                }

            } else {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidAddress", originalString));
            }
        } else {
            throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingAddress"));
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        if (mode != 1) {
            registerArgument1.contextualize(stateContainer);

            if (immediateArgument != null) {
                immediateArgument.contextualize(stateContainer);
            }

            if (registerArgument2 != null) {
                registerArgument2.contextualize(stateContainer);
            }

            if (shiftArgument != null) {
                shiftArgument.contextualize(stateContainer);
            }
        }
    }

    @Override
    public AddressArgument.UpdatableInteger getValue(StateContainer stateContainer) throws ExecutionASMException {
        switch (mode) {
            case 1 -> {
                return null; // On retourne null car c'est une pseudo-instruction, l'adresse est ignorée
            }

            case 2 -> {
                return new UpdatableInteger(registerArgument1.getValue(stateContainer).getData(),
                                            stateContainer,
                                            true,
                                            updateNow,
                                            registerArgument1.getValue(stateContainer));
            }

            case 3 -> {
                return new UpdatableInteger(registerArgument1.getValue(stateContainer).getData() + immediateArgument.getValue(stateContainer),
                                            stateContainer,
                                            false,
                                            updateNow,
                                            registerArgument1.getValue(stateContainer));
            }

            case 4 -> {
                return new UpdatableInteger(registerArgument1.getValue(stateContainer).getData() + registerArgument2.getValue(stateContainer).getData(),
                                            stateContainer,
                                            false,
                                            updateNow,
                                            registerArgument1.getValue(stateContainer));
            }

            case 5 -> {
                return new UpdatableInteger(registerArgument1.getValue(stateContainer).getData() + shiftArgument.getValue(stateContainer).apply(registerArgument2.getValue(stateContainer).getData()),
                                            stateContainer,
                                            false,
                                            updateNow,
                                            registerArgument1.getValue(stateContainer));
            }

            default -> {
                throw new RuntimeException("Invalide state: Illegal mode (" + mode + ")");
            }
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (mode != 1) {
            registerArgument1.verify(stateSupplier);

            if (immediateArgument != null) {
                immediateArgument.verify(stateSupplier);
            }

            if (registerArgument2 != null) {
                registerArgument2.verify(stateSupplier);
            }

            if (shiftArgument != null) {
                shiftArgument.verify(stateSupplier);
            }
        }

        super.verify(stateSupplier);
    }

    /**
     * Objet utilisé pour contenir une valeur et permettre la mise à jour du registre sous-jacent si celui-ci doit être mis à jour
     */
    public static final class UpdatableInteger {

        private final int integer;
        private final StateContainer stateContainer;
        private final Register register;
        private boolean update;

        /**
         * @param integer        la valeur actuelle
         * @param stateContainer le conteneur sur lequel effectué la mise à jour le cas échéant
         * @param update         vrai si on autorise la mise à jour du registre lors de l'appel de update()
         * @param updateNow      vrai si on veut effectuer la mise à jour instantanément (quelque soit la valeur du paramètre précédant)
         * @param register       le registre à mettre à jour
         */
        public UpdatableInteger(int integer, StateContainer stateContainer, boolean update, boolean updateNow, Register register) {
            this.integer = integer;
            this.register = register;
            this.update = update;
            this.stateContainer = stateContainer;

            if (updateNow) register.setData(integer);
        }

        public int toInt() {
            return integer;
        }

        public void update() {
            if (register == null) return;
            if (update && stateContainer.hasAddressRegisterUpdate())
                register.setData(stateContainer.getAddressRegisterUpdateValue());
            update = false;
        }
    }

    public boolean isPseudoInstruction() {
        return mode == 1;
    }
}
