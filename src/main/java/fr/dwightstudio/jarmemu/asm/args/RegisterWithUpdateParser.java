package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;

public class RegisterWithUpdateParser implements ArgumentParser<RegisterWithUpdateParser.RegisterWithUpdate> {
    @Override
    public RegisterWithUpdateParser.RegisterWithUpdate parse(String string) {
        return null;
    }

    public static final class RegisterWithUpdate {
        public Register register;
        public boolean update;
    }
}
