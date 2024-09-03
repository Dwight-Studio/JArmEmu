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

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.Arrays;
import java.util.function.Supplier;

public class AddressArgument extends ParsedArgument<AddressArgument.UpdatableInteger> {

    private final AddressType mode;
    private boolean updateNow;
    private boolean negative;
    private RegisterArgument addressRegisterArgument;

    private ImmediateArgument offsetImmediateArgument;
    private RegisterArgument offsetRegisterArgument;
    private ShiftArgument shiftArgument;

    public AddressArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            String string = originalString;

            if (originalString.startsWith("=")) {
                mode = AddressType.PSEUDO_INSTRUCTION;
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

                addressRegisterArgument = new RegisterArgument(mems[0]);

                if (mems.length == 1) {
                    mode = AddressType.SIMPLE_REGISTER;

                } else if (mems.length == 2) {
                    if (mems[1].startsWith("#")) {
                        offsetImmediateArgument = new ImmediateArgument(mems[1]);
                        mode = AddressType.IMMEDIATE_OFFSET;
                    } else {
                        String rgString = mems[1];

                        if (rgString.startsWith("+")) {
                            rgString = rgString.substring(1);
                            negative = false;
                        }

                        if (rgString.startsWith("-")) {
                            rgString = rgString.substring(1);
                            negative = true;
                        }

                        offsetRegisterArgument = new RegisterArgument(rgString);
                        mode = AddressType.REGISTER_OFFSET;
                    }

                } else if (mems.length == 3) {
                    shiftArgument = new ShiftArgument(mems[2]);
                    String rgString = mems[1];

                    if (rgString.startsWith("+")) {
                        rgString = rgString.substring(1);
                        negative = false;
                    }

                    if (rgString.startsWith("-")) {
                        rgString = rgString.substring(1);
                        negative = true;
                    }

                    offsetRegisterArgument = new RegisterArgument(rgString);
                    mode = AddressType.SHIFTED_REGISTER_OFFSET;
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
        if (mode != AddressType.PSEUDO_INSTRUCTION) {
            addressRegisterArgument.contextualize(stateContainer);

            if (offsetImmediateArgument != null) {
                offsetImmediateArgument.contextualize(stateContainer);
            }

            if (offsetRegisterArgument != null) {
                offsetRegisterArgument.contextualize(stateContainer);
            }

            if (shiftArgument != null) {
                shiftArgument.contextualize(stateContainer);
            }
        }
    }

    @Override
    public AddressArgument.UpdatableInteger getValue(StateContainer stateContainer) throws ExecutionASMException {
        switch (mode) {
            case PSEUDO_INSTRUCTION -> {
                return null; // On retourne null car c'est une pseudo-instruction, l'adresse est ignorée
            }

            case SIMPLE_REGISTER -> {
                return new UpdatableInteger(addressRegisterArgument.getValue(stateContainer).getData(),
                        true,
                        updateNow,
                        addressRegisterArgument.getValue(stateContainer));
            }

            case IMMEDIATE_OFFSET -> {
                return new UpdatableInteger(addressRegisterArgument.getValue(stateContainer).getData() + offsetImmediateArgument.getValue(stateContainer),
                        false,
                        updateNow,
                        addressRegisterArgument.getValue(stateContainer));
            }

            case REGISTER_OFFSET -> {
                return new UpdatableInteger((addressRegisterArgument.getValue(stateContainer).getData() + offsetRegisterArgument.getValue(stateContainer).getData()) * (negative ? -1 : 1),
                        false,
                        updateNow,
                        addressRegisterArgument.getValue(stateContainer));
            }

            case SHIFTED_REGISTER_OFFSET -> {
                return new UpdatableInteger((addressRegisterArgument.getValue(stateContainer).getData() + shiftArgument.getValue(stateContainer).apply(offsetRegisterArgument.getValue(stateContainer).getData())* (negative ? -1 : 1)),
                        false,
                        updateNow,
                        addressRegisterArgument.getValue(stateContainer));
            }

            default -> throw new RuntimeException("Invalide state: Illegal mode (" + mode + ")");
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (mode != AddressType.PSEUDO_INSTRUCTION) {
            addressRegisterArgument.verify(stateSupplier);

            if (offsetImmediateArgument != null) {
                offsetImmediateArgument.verify(stateSupplier);
            }

            if (offsetRegisterArgument != null) {
                offsetRegisterArgument.verify(stateSupplier);
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
        private final Register register;
        private boolean update;

        /**
         * @param integer   la valeur actuelle
         * @param update    vrai si on autorise la mise à jour du registre lors de l'appel de update()
         * @param updateNow vrai si on veut effectuer la mise à jour instantanément (quelque soit la valeur du paramètre précédant)
         * @param register  le registre à mettre à jour
         */
        public UpdatableInteger(int integer, boolean update, boolean updateNow, Register register) {
            this.integer = integer;
            this.register = register;
            this.update = update;

            if (updateNow) register.setData(integer);
        }

        public int toInt() {
            return integer;
        }

        public void update(int offset) {
            if (register == null) return;
            if (update) register.add(offset);
            update = false;
        }

        public boolean canUpdate() {
            return update;
        }
    }

    public boolean isPseudoInstruction() {
        return mode == AddressType.PSEUDO_INSTRUCTION;
    }

    public AddressType getMode() {
        return mode;
    }

    public boolean doesUpdateNow() {
        return updateNow;
    }

    public RegisterArgument getAddressRegisterArgument() {
        return addressRegisterArgument;
    }

    public ImmediateArgument getOffsetImmediateArgument() {
        return offsetImmediateArgument;
    }

    public RegisterArgument getOffsetRegisterArgument() {
        return offsetRegisterArgument;
    }

    public ShiftArgument getShiftArgument() {
        return shiftArgument;
    }

    public boolean isNegative()  {
        return negative;
    }
}
