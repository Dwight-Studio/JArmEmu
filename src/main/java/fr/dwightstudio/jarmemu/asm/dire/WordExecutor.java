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
                int data = RotatedImmParser.generalParse(stateContainer, string.strip());
                stateContainer.memory.putWord(currentPos, data);
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
        int rtn = 0;
        String[] arg = args.split(",");

        for (String string : arg) {
            try {
                int data = RotatedImmParser.generalParse(stateContainer, string.strip());
                rtn += 4;
            } catch (Exception ignored) {}
        }

        return rtn;
    }
}
