package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Correspond à "mreg"
public class RegisterArrayParser implements ArgumentParser<Register[]> {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public Register[] parse(@NotNull StateContainer stateContainer, @NotNull String string) {

        if (string.startsWith("{") && string.endsWith("}")) {
            String arrayString = string.substring(1, string.length()-1);
            ArrayList<Register> rtn = new ArrayList<>();

            for (String regString : arrayString.split(",")) {
                Register reg = ArgumentParsers.REGISTER.parse(stateContainer, regString);
                if (!rtn.contains(reg)) {
                    rtn.add(reg);
                } else {
                    logger.log(Level.WARNING, "Duplicate register in array");
                }
            }

            RegisterWithUpdateParser.updateValue.put(stateContainer, rtn.size());

            return rtn.toArray(new Register[0]);
        } else {
            throw new AssemblySyntaxException("Unexpected value '" + string + "' (expected a Register Array)");
        }
    }

    @Override
    public Register[] none() {
        return null;
    }
}
