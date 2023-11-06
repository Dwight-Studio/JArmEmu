package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class SpaceExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        // Rien à faire, on veut juste laisser de la place

        try {
            Integer.parseInt(args);
        } catch (Exception e) {
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
        try {
            return Integer.parseInt(args);
        } catch (Exception e) {
            return 0;
        }
    }
}
