package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "arg"
public class ValueOrRegisterParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("#") || string.startsWith("=")) {
            return ArgumentParsers.VALUE_8.parse(stateContainer, string);
        } else {
            return ArgumentParsers.REGISTER.parse(stateContainer, string).getData();
        }
    }

    @Override
    public Integer none() {
        return null;
    }
}
