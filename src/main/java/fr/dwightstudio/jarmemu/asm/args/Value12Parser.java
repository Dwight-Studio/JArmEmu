package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12" TODO: Ajouter le Barrel Shifting
public class Value12Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1).strip();
                int value = generalParse(stateContainer, valueString);
                checkOverflow(value, string);
                return value;

            } else if (string.startsWith("=")) {
                throw new IllegalArgumentException("Detecting unprocessed '=' Pseudo-Op");
            } else {
                throw new SyntaxASMException("Invalid 12bits immediate value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 12bits immediate value '" + string + "' (" + exception.getMessage() + ")");
        }
    }

    /**
     * Analyse une chaine de caractères pour trouver des valeurs immédiates (sans # ou =).
     *
     * @param stateContainer le conteneur d'état sur lequel faire l'analyse (pour les constantes)
     * @param valueString la chaine à analyser
     * @return la valeur dans un entier
     */
    public int generalParse(StateContainer stateContainer, @NotNull String valueString) {
        if (valueString.startsWith("0B")) {
            valueString = valueString.substring(2).strip();

            return Integer.parseInt(valueString, 2);
        } else if (valueString.startsWith("0X")) {
            valueString = valueString.substring(2).strip();

            return Integer.parseInt(valueString, 16);
        } else if (valueString.startsWith("00")) {
            valueString = valueString.substring(2).strip();

            return Integer.parseInt(valueString, 8);
        } else {
            return stateContainer.eval(valueString, stateContainer.consts);
        }
    }

    private void checkOverflow(int value, String string) {
        boolean valid = false;

        for (int i = 0 ; i < 32 ; i += 2) {
            int original = Integer.rotateLeft(value, i);

            if (Integer.numberOfLeadingZeros(original) >= 24) {
                valid = true;
                break;
            }
        }

        if (!valid) throw new SyntaxASMException("Overflowing 12bits value '" + string + "'");
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (12bits)");
    }
}
