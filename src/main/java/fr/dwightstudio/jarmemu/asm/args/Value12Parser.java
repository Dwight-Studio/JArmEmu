package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12"
public class Value12Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
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
                    int value = Integer.parseInt(sign + valueString, 10);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new AssemblySyntaxException("Overflowing 12 bits value '" + string + "'");
                    return value;
                }
            } else if (string.startsWith("=")) {
                String valueString = string.substring(1);
                Byte value = stateContainer.symbols.get(valueString);
                if (value == null) {
                    throw new AssemblySyntaxException("Unknown symbol '" + valueString + "'");
                }
                return (int) value;
            } else {
                throw new AssemblySyntaxException("Invalid 12 bits value '" + string + "'");
            }
        } catch (NumberFormatException exception) {
            throw new AssemblySyntaxException("Invalid 12 bits value '" + string + "'");
        }
    }
}
