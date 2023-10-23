package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "mem", à utiliser avec ValueOrRegisterParser et ShiftParser
public class AddressParser implements ArgumentParser<Integer> {

    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Faire l'AddressParser
        return 0;
    }

    public static final class UpdatableInteger {

        private final int integer;

        private final Register register;
        private boolean update;

        private UpdatableInteger(int integer, boolean update, Register register) {
            this.integer = integer;
            this.register = register;
        }

        public int toInt() {
            return integer;
        }
    }

    @Override
    public Integer none() {
        return null;
    }
}