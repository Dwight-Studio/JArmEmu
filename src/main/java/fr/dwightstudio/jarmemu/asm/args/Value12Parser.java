package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12" TODO: Ajouter le Barrel Shifting
public class Value12Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Attention aux 4 bits de shift
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1);
                String sign = valueString.startsWith("-") ? "-" : "";
                if (valueString.startsWith("-")) valueString = valueString.substring(1);

                if (valueString.startsWith("0B")) {
                    valueString = valueString.substring(2);
                    int value = Integer.parseInt(sign + valueString, 2);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new SyntaxASMException("Overflowing 12 bits value '" + string + "'");
                    return value;
                } else if (valueString.startsWith("0X")) {
                    valueString = valueString.substring(2);
                    int value = Integer.parseInt(sign + valueString, 16);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new SyntaxASMException("Overflowing 12 bits value '" + string + "'");
                    return value;
                } else if (valueString.startsWith("00")) {
                    valueString = valueString.substring(2);
                    int value = Integer.parseInt(sign + valueString, 8);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new SyntaxASMException("Overflowing 12 bits value '" + string + "'");
                    return value;
                } else {
                    int value = stateContainer.eval(sign + valueString, stateContainer.consts);
                    if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21)
                        throw new SyntaxASMException("Overflowing 12 bits value '" + string + "'");
                    return value;
                }
            } else if (string.startsWith("=")) {
                String valueString = string.substring(1);
                return stateContainer.eval(valueString, stateContainer.data);
            } else {
                throw new SyntaxASMException("Invalid 12 bits value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 12 bits value '" + string + "'");
        }
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (12 bits)");
    }
}
