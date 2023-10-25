package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm24"
public class LabelParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        string = string.substring(0, string.length()-1);
        Integer value = stateContainer.labels.get(string);
        if (value == null) throw new AssemblySyntaxException("Unknown label '" + string +"'");

        return value;
    }

    @Override
    public Integer none() {
        return null;
    }

}
