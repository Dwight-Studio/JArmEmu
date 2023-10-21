package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à "mreg"
public class RegisterArrayParser implements ArgumentParser<RegisterWithUpdateParser.RegisterWithUpdate[]> {

    @Override
    public RegisterWithUpdateParser.RegisterWithUpdate[] parse(StateContainer stateContainer, String string) {
        return null;
    }
}
