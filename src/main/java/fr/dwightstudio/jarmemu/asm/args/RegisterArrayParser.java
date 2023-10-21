package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "mreg"
public class RegisterArrayParser implements ArgumentParser<Register[]> {

    @Override
    public Register[] parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Faire le RegisterArrayParser
        return null;
    }
}
