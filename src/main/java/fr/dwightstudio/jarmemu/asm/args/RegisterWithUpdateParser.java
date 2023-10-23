package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
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

        return new UpdatableRegister(ArgumentParsers.REGISTER.parse(stateContainer, string), update);
    }

    @Override
    public UpdatableRegister none() {
        return null;
    }

    public static final class UpdatableRegister extends Register {
        private final Register register;
        private final boolean update;

        public UpdatableRegister(Register register, boolean update) {
            this.register = register;
            this.update = update;
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
         *
         * @param number le nombre de registres de l'argument RegisterArray
         */
        public void update(int number) {
            if (update) register.add(number);
        }
    }
}
