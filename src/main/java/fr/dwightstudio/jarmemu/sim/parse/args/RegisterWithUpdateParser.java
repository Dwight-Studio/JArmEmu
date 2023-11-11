package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.sim.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "reg!", à utiliser avec ShiftParser
public class RegisterWithUpdateParser implements ArgumentParser<RegisterWithUpdateParser.UpdatableRegister> {

    @Override
    public UpdatableRegister parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        boolean update = false;

        if (string.endsWith("!")) {
            update = true;
            string = string.substring(0, string.length()-1);
        }

        return new UpdatableRegister(ArgumentParsers.REGISTER.parse(stateContainer, string), update, stateContainer);
    }

    @Override
    public UpdatableRegister none() {
        throw new BadArgumentsASMException("missing register");
    }

    public static final class UpdatableRegister extends Register {
        private final Register register;
        private final StateContainer stateContainer;
        private boolean update;

        public UpdatableRegister(Register register, boolean update, StateContainer stateContainer) {
            this.register = register;
            this.update = update;
            this.stateContainer = stateContainer;
        }

        @Override
        public int getData() {
            return register.getData();
        }

        @Override
        public void setData(int data) throws IllegalArgumentException {
            register.setData(data);
        }

        @Override
        public boolean get(int index) throws IllegalArgumentException {
            return register.get(index);
        }

        @Override
        public void set(int index, boolean value) {
            register.set(index, value);
        }

        @Override
        public void add(int value) {
            register.add(value);
        }

        /**
         * Met à jour le registre en fonction du nombre de registres de l'argument RegisterArray
         */
        public void update(int value) {
            if (update) register.add(value);
            update = false;
        }
    }
}
