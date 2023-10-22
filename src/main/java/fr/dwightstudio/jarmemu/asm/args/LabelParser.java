package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm24"
public class LabelParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Faire le LabelParser
        Byte value = stateContainer.symbols.get(string.substring(string.length()-1));
        if (value == null) throw new AssemblySyntaxException("Unknown label : " + string.substring(string.length()-1));
        /*
        Je ne sais pas comment renvoyer un nombre sur 24 bits :)
         */
        return 0; // Nombre sur 24 bits
    }

    @Override
    public Integer none() {
        return null;
    }
}
