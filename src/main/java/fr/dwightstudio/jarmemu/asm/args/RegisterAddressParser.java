package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à "[reg]"
public class RegisterAddressParser implements ArgumentParser<Register> {
    @Override
    public Register parse(StateContainer stateContainer, String string) {
        return null; // Ne sert que pour SWP
    }
}
