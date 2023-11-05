package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.Value12Parser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ByteExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        try {
            int data = Value12Parser.generalParse(stateContainer, args);
            if (Integer.numberOfLeadingZeros(data) >= 24) {
                byte b = (byte) data;
                stateContainer.memory.putByte(currentPos, b);
            } else {
                throw new SyntaxASMException("Overflowing Half value '" + args + "'");
            }
        } catch (NumberFormatException exception) {
            throw new SyntaxASMException("Invalid Half value '" + args + "'");
        }
    }

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param args       la chaine d'arguments
     * @param currentPos la position actuelle dans la mémoire
     * @return la taille des données
     */
    @Override
    public int computeDataLength(String args, int currentPos) {
        return 1;
    }
}
