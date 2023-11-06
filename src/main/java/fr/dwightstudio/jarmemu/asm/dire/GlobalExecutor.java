package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class GlobalExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        if (args.matches("[A-Za-z_0-9]+")) {
            stateContainer.globals.add(args);
        } else {
            throw new SyntaxASMException("Invalid argument '" + args + "'");
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
        return 0;
    }
}
