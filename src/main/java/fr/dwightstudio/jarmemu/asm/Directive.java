package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutor;
import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutors;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public enum Directive {
    // Consts
    SET(DirectiveExecutors.EQUIVALENT), EQU(DirectiveExecutors.EQUIVALENT), EQUIV(DirectiveExecutors.EQUIVALENT), EQV(DirectiveExecutors.EQUIVALENT), // Définir une constante
    GLOBAL(DirectiveExecutors.GLOBAL), // Inutile pour l'interpréteur

    // Data
    WORD(DirectiveExecutors.WORD), // Donnée sur 32bits
    HALF(DirectiveExecutors.HALF), // Donnée sur 16bits
    BYTE(DirectiveExecutors.BYTE), // Donnée sur 8bits
    SPACE(DirectiveExecutors.SPACE), SKIP(DirectiveExecutors.SPACE), // Vide sur nbits
    ASCII(DirectiveExecutors.ASCII), // Chaîne de caractères
    ASCIZ(DirectiveExecutors.ASCIZ), // Chaîne de caractère finissant par '\0'
    FILL(DirectiveExecutors.FILL), // Remplir n fois, un nombre de taille x, de valeur y

    // Other
    ALIGN(DirectiveExecutors.ALIGN); // Alignement des données sur la grille des 4 bytes

    private final DirectiveExecutor executor;

    Directive(DirectiveExecutor executor) {
        this.executor = executor;
    }

    /**
     * Calcul de la place en mémoire nécessaire pour cette directive
     * @param args la chaine d'arguments
     * @param currentPos la position actuelle dans la mémoire
     */
    public int computeDataLength(String args, int currentPos) throws SyntaxASMException {
        return executor.computeDataLength(args, currentPos);
    }

    /**
     * Application de la directive
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    public void apply(StateContainer stateContainer, String args, int currentPos) {
        executor.apply(stateContainer, args, currentPos);
    }
}
