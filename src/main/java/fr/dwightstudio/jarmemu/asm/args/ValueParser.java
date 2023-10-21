package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à "imm"
public class ValueParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(StateContainer stateContainer, String string) {
        return 0; // Nombre sur 8 bits
    }
}
