package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public interface DirectiveExecutor {

    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    void apply(StateContainer stateContainer, String args, int currentPos);

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param args       la chaine d'arguments
     * @param currentPos la position actuelle
     * @return la taille des données
     */
    int computeDataLength(String args, int currentPos);

}
