package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.Arrays;
import java.util.function.Supplier;

public class AddressArgument extends ParsedArgument<AddressArgument.UpdatableInteger> {

    private int mode;
    private boolean updateNow;
    private RegisterArgument registerArgument1;

    private String symbol;
    private ImmediateArgument immediateArgument;
    private RegisterArgument registerArgument2;
    private ShiftArgument shiftArgument;

    public AddressArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        String string = originalString;

        if (originalString.startsWith("*")) {
            mode = 1;
            symbol = originalString.substring(1).strip().toUpperCase();
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
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
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

    @Override
    public AddressArgument.UpdatableInteger getValue(StateContainer stateContainer) throws ExecutionASMException {
        switch (mode) {
            case 1 -> {
                return new UpdatableInteger(stateContainer.getPseudoData().get(symbol),
                                            stateContainer,
                                            false,
                                            false,
                                            null);
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
    public AddressArgument.UpdatableInteger getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingAddress"));
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
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
}
