package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ASCIZExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        DirectiveExecutors.ASCII.apply(stateContainer, args, currentPos);
        DirectiveExecutors.BYTE.apply(stateContainer, String.valueOf((int) '\0'), currentPos + DirectiveExecutors.ASCII.computeDataLength(stateContainer, args, currentPos));
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
        return DirectiveExecutors.ASCII.computeDataLength(stateContainer, args, currentPos) + 1;
    }
}
