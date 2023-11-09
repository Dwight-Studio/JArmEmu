package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.RotatedImmParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class WordExecutor implements DirectiveExecutor {
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
            String[] arg = args.split(",");

            for (String string : arg) {
                if (string.startsWith("=")) {
                    string = string.substring(1);
                    int data = stateContainer.evalWithAll(string);
                    stateContainer.memory.putWord(currentPos, data);
                } else {
                    int data = stateContainer.evalWithConsts(string.strip());
                    stateContainer.memory.putWord(currentPos, data);
                }
                currentPos += 4;
            }
        } catch (NumberFormatException exception) {
            throw new SyntaxASMException("Invalid Word value '" + args + "'");
        }
    }

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param stateContainer Le conteneur d'état sur lequel calculer
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle
     * @return la taille des données
     */
    @Override
    public int computeDataLength(StateContainer stateContainer, String args, int currentPos) {
        String[] arg = args.split(",");
        return arg.length * 4;
    }
}
