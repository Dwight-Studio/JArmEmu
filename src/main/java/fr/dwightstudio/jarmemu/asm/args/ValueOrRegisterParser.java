package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "arg"
public class ValueOrRegisterParser implements ArgumentParser<ValueOrRegisterParser.ValueView> {
    @Override
    public ValueOrRegisterParser.ValueView parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        return null;
    }

    public static final class ValueView {
        private Register register;
        private int shift;
        private int value;


        public ValueView(Register register, int shift) {
            this.register = register;
            this.shift = shift;
        }

        public ValueView(byte value) {
            this.register = null;
            this.value = value;
        }

        int value() {
            if (this.register == null) {
                return value;
            } else {
                return register.getData();
            }
        }
    }
}
