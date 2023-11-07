package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Correspond à "mreg"
public class RegisterArrayParser implements ArgumentParser<Register[]> {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public Register[] parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("{") && string.endsWith("}")) {
            String arrayString = string.substring(1, string.length()-1);
            ArrayList<Register> rtn = new ArrayList<>();

            if(string.contains("-")){
                String[] stringArray = string.split("-");
                if (stringArray.length!=2) throw new SyntaxASMException("Unexpected value '" + string + "' (expected a Register Array)");
                int registerFirst = stringArray[0].strip().charAt(1);
                int registerSecond = stringArray[1].strip().charAt(1);
                for (int i = registerFirst; i <= registerSecond; i++) {
                    Register reg = ArgumentParsers.REGISTER.parse(stateContainer, "R" + i);
                    if (!rtn.contains(reg)) {
                        rtn.add(reg);
                    } else {
                        logger.log(Level.WARNING, "Duplicate register in array");
                    }
                }
            } else {
                for (String regString : arrayString.split(",")) {
                    Register reg = ArgumentParsers.REGISTER.parse(stateContainer, regString.strip());
                    if (!rtn.contains(reg)) {
                        rtn.add(reg);
                    } else {
                        logger.log(Level.WARNING, "Duplicate register in array");
                    }
                }
            }

            return rtn.toArray(new Register[0]);
        } else {
            throw new SyntaxASMException("Unexpected value '" + string + "' (expected a Register Array)");
        }
    }

    @Override
    public Register[] none() {
        throw new BadArgumentsASMException("missing register array");
    }
}
