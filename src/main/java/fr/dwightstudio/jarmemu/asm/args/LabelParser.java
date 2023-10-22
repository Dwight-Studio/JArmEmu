package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm24"
public class LabelParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Faire le LabelParser
        Byte value = stateContainer.symbols.get(string.substring(0, string.length()-1));
        if (value == null) throw new AssemblySyntaxException("Unknown label : " + string.substring(0, string.length()-1));
        int result = value& 0xFFFFFF;
        System.out.println(Integer.toBinaryString(result));
        /*
        Je ne sais pas comment renvoyer un nombre sur 24 bits :)
         */
        return result; // Nombre sur 24 bits
    }

    @Override
    public Integer none() {
        return null;
    }

}
