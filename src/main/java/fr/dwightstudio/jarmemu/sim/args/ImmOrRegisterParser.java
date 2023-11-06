package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12"
public class ImmOrRegisterParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("#") || string.startsWith("=") || string.startsWith("*")) {
            return ArgumentParsers.IMM.parse(stateContainer, string);
        } else {
            return ArgumentParsers.REGISTER.parse(stateContainer, string).getData();
        }
    }

    @Override
    public Integer none() {
        return 0;
    }
}
