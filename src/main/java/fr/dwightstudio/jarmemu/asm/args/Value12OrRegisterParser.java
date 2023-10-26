package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "arg"
public class Value12OrRegisterParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("#") || string.startsWith("=")) {
            int rtn = ArgumentParsers.VALUE_12.parse(stateContainer, string);
            AddressParser.updateValue.put(stateContainer, rtn);
            return rtn;
        } else {
            int rtn = ArgumentParsers.REGISTER.parse(stateContainer, string).getData();
            AddressParser.updateValue.put(stateContainer, rtn);
            return rtn;
        }
    }

    @Override
    public Integer none() {
        return null;
    }
}