package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutor;
import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutors;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public enum Directive {
    // Consts
    SET(DirectiveExecutors.EQUIVALENT, true), EQU(DirectiveExecutors.EQUIVALENT, true), EQUIV(DirectiveExecutors.EQUIVALENT, true), EQV(DirectiveExecutors.EQUIVALENT, true), // Définir une constante
    GLOBAL(DirectiveExecutors.GLOBAL, true), GLOBL(DirectiveExecutors.GLOBAL, true), // Inutile pour l'interpréteur

    // Data
    WORD(DirectiveExecutors.WORD, false), // Donnée sur 32bits
    HALF(DirectiveExecutors.HALF, false), // Donnée sur 16bits
    BYTE(DirectiveExecutors.BYTE, false), // Donnée sur 8bits
    SPACE(DirectiveExecutors.SPACE, false), SKIP(DirectiveExecutors.SPACE, false), // Vide sur nbits
    ASCII(DirectiveExecutors.ASCII, false), // Chaîne de caractères
    ASCIZ(DirectiveExecutors.ASCIZ, false), // Chaîne de caractère finissant par '\0'
    FILL(DirectiveExecutors.FILL, false), // Remplir n fois, un nombre de taille x, de valeur y

    // Other
    ALIGN(DirectiveExecutors.ALIGN, false); // Alignement des données sur la grille des 4 bytes

    private final DirectiveExecutor executor;
    private final boolean sectionIndifferent;

    Directive(DirectiveExecutor executor, boolean sectionIndifferent) {
        this.executor = executor;
        this.sectionIndifferent = sectionIndifferent;
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

    public boolean isSectionIndifferent() {
        return sectionIndifferent;
    }
}
