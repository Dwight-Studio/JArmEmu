package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.RotatedImmParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class AlignExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        if (!args.isEmpty()) {
            RotatedImmParser.generalParse(stateContainer, args);
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
        int d = 4;
        if (!args.isEmpty()) {
            d = RotatedImmParser.generalParse(stateContainer, args);
        }
        return (d - (currentPos % d)) % d;
    }
}
