package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.sim.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "arg"
public class RotatedImmOrRegisterParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("#") || string.startsWith("=") || string.startsWith("*")) {
            int rtn = ArgumentParsers.ROTATED_IMM.parse(stateContainer, string);
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
        throw new BadArgumentsASMException("missing immediate (rotated 8bits) or register");
    }
}
