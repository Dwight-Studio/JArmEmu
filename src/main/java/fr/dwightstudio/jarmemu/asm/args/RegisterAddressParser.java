package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;

// Correspond à "[reg]"
public class RegisterAddressParser implements ArgumentParser<Register> {
    @Override
    public Register parse(String string) {
        return null; // Ne sert que pour SWP
    }
}
