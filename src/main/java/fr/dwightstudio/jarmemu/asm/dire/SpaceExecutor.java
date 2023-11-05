package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class SpaceExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     */
    @Override
    public void apply(StateContainer stateContainer, String args) {
        //TODO: Faire la directive Space
        throw new IllegalStateException("Directive Space not implemented");
    }

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param args la chaine d'arguments
     * @return
     */
    @Override
    public int computeDataLength(String args) {
        return 0;
    }
}
