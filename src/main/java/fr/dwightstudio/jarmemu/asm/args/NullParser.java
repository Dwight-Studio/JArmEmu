package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à pas d'argument
public class NullParser implements ArgumentParser<Object> {
    @Override
    public Object parse(StateContainer stateContainer, String string) {
        throw new IllegalStateException("Parsing a Null Argument");
    }
}
