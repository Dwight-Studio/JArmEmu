package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "reg!", à utiliser avec ShiftParser
public class RegisterWithUpdateParser implements ArgumentParser<RegisterWithUpdateParser.RegisterWithUpdate> {
    @Override
    public RegisterWithUpdateParser.RegisterWithUpdate parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        RegisterWithUpdate rtn = new RegisterWithUpdate();

        if (string.endsWith("!")) {
            rtn.update = true;
            string = string.substring(0, string.length()-1);
        }

        rtn.register = ArgumentParsers.REGISTER.parse(stateContainer, string);

        // TODO: Finir le RegisterWithUpdateParser en changer la methode de rendu (sans flag, update fait à l'appel)

        return rtn;
    }

    @Override
    public RegisterWithUpdate none() {
        return null;
    }

    public static final class RegisterWithUpdate {
        public Register register = null;
        public boolean update = false;
    }
}
