package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "mreg"
public class RegisterArrayParser implements ArgumentParser<RegisterWithUpdateParser.RegisterWithUpdate[]> {

    @Override
    public RegisterWithUpdateParser.RegisterWithUpdate[] parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        return null;
    }
}
