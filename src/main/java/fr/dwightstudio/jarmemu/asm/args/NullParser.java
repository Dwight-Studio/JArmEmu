package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à pas d'argument
public class NullParser implements ArgumentParser<Object> {
    @Override
    public Object parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        throw new AssemblySyntaxException("Unexpected argument '" + string + "'");
    }

    @Override
    public Object none() {
        return null;
    }
}
