package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.parse.args.ShiftParser;

import java.util.Arrays;

public class AddressArgument extends ParsedArgument<AddressArgument.UpdatableInteger> {

    public AddressArgument(String originalString) {
        super(originalString);
    }

    @Override
    protected void parse(String originalString) throws SyntaxASMException {

    }

    @Override
    public AddressArgument.UpdatableInteger getValue(StateContainer stateContainer) throws ExecutionASMException {

    }

    @Override
    public AddressArgument.UpdatableInteger getNullValue() throws BadArgumentASMException {
        return null;
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
