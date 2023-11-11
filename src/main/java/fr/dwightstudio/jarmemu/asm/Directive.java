package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutor;
import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutors;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public enum Directive {
    // Consts
    SET(DirectiveExecutors.EQUIVALENT, false), EQU(DirectiveExecutors.EQUIVALENT, false), EQUIV(DirectiveExecutors.EQUIVALENT, false), EQV(DirectiveExecutors.EQUIVALENT, false), // Définir une constante
    GLOBAL(DirectiveExecutors.GLOBAL, false), // Inutile pour l'interpréteur

    // Data
    WORD(DirectiveExecutors.WORD, true), // Donnée sur 32bits
    HALF(DirectiveExecutors.HALF, true), // Donnée sur 16bits
    BYTE(DirectiveExecutors.BYTE, true), // Donnée sur 8bits
    SPACE(DirectiveExecutors.SPACE, false), SKIP(DirectiveExecutors.SPACE, false), // Vide sur nbits
    ASCII(DirectiveExecutors.ASCII, true), // Chaîne de caractères
    ASCIZ(DirectiveExecutors.ASCIZ, true), // Chaîne de caractère finissant par '\0'
    FILL(DirectiveExecutors.FILL, false), // Remplir n fois, un nombre de taille x, de valeur y

    // Other
    ALIGN(DirectiveExecutors.ALIGN, false); // Alignement des données sur la grille des 4 bytes

    private final DirectiveExecutor executor;
    private final boolean dataInitializer;

    Directive(DirectiveExecutor executor, boolean dataInitializer) {
        this.executor = executor;
        this.dataInitializer = dataInitializer;
    }

    /**
     * Calcul de la place en mémoire nécessaire pour cette directive
     *
     * @param stateContainer Le conteneur d'état sur lequel calculer
     * @param args la chaine d'arguments
     * @param currentPos la position actuelle dans la mémoire
     */
    public int computeDataLength(StateContainer stateContainer, String args, int currentPos) throws SyntaxASMException {
        return executor.computeDataLength(stateContainer, args, currentPos);
    }

    /**
     * Application de la directive
     *
     * 
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        executor.apply(stateContainer, args, currentPos);
    }

    public boolean isDataInitializer() {
        return dataInitializer;
    }
}
