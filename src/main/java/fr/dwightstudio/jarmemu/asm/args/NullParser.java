package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à pas d'argument
public class NullParser implements ArgumentParser<Object> {
    @Override
    public Object parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        throw new IllegalStateException("Parsing a Null Argument");
    }
}
