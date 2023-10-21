package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à "reg!", à utiliser avec ShiftParser
public class RegisterWithUpdateParser implements ArgumentParser<RegisterWithUpdateParser.RegisterWithUpdate> {
    @Override
    public RegisterWithUpdateParser.RegisterWithUpdate parse(StateContainer stateContainer, String string) {

        return null;
    }

    public static final class RegisterWithUpdate {
        public Register register;
        public boolean update;
    }
}
