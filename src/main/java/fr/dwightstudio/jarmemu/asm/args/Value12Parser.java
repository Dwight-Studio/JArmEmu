package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12"
public class Value12Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Ajout du support des symbols et des calculs
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1);
                String sign = valueString.startsWith("-") ? "-" : "";
                if (valueString.startsWith("-")) valueString = valueString.substring(1);

                if (valueString.startsWith("0b")) {
                    valueString = valueString.substring(2);
                    int value = Integer.parseInt(sign + valueString, 2);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new AssemblySyntaxException("Overflowing 12 bits value '" + string + "'");
                    return value;
                } else if (valueString.startsWith("0x")) {
                    valueString = valueString.substring(2);
                    int value = Integer.parseInt(sign + valueString, 16);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new AssemblySyntaxException("Overflowing 12 bits value '" + string + "'");
                    return value;
                } else if (valueString.startsWith("00")) {
                    valueString = valueString.substring(2);
                    int value = Integer.parseInt(sign + valueString, 8);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new AssemblySyntaxException("Overflowing 12 bits value '" + string + "'");
                    return value;
                } else {
                    int value = stateContainer.eval(sign + valueString, stateContainer.consts);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new AssemblySyntaxException("Overflowing 12 bits value '" + string + "'");
                    return value;
                }
            } else if (string.startsWith("=")) {
                String valueString = string.substring(1);
                return stateContainer.eval(valueString, stateContainer.data);
            } else {
                throw new AssemblySyntaxException("Invalid 12 bits value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new AssemblySyntaxException("Invalid 12 bits value '" + string + "'");
        }
    }

    @Override
    public Integer none() {
        return null;
    }
}
