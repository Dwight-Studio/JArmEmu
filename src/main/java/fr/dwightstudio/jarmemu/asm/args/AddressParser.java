package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à "mem"
public class AddressParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(StateContainer stateContainer, String string) {
        return 0; // Nombre sur 8 bits
    }
}
