package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutor;
import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutors;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public enum Directive {
    // Consts
    SET(DirectiveExecutors.NOT_IMPLEMENTED), EQU(DirectiveExecutors.NOT_IMPLEMENTED), EQUIV(DirectiveExecutors.NOT_IMPLEMENTED), EQV(DirectiveExecutors.NOT_IMPLEMENTED), // Définir une constante
    GLOBAL(DirectiveExecutors.NOT_IMPLEMENTED), // Inutile pour l'interpréteur

    // Data
    WORD(DirectiveExecutors.NOT_IMPLEMENTED), // Donnée sur 32bits
    HALF(DirectiveExecutors.NOT_IMPLEMENTED), // Donnée sur 16bits
    BYTE(DirectiveExecutors.NOT_IMPLEMENTED), // Donnée sur 8bits
    SPACE(DirectiveExecutors.NOT_IMPLEMENTED), // Vide sur nbits
    ASCII(DirectiveExecutors.NOT_IMPLEMENTED), // Chaîne de caractères
    ASCIZ(DirectiveExecutors.NOT_IMPLEMENTED), // Chaîne de caractère finissant par '\0'
    FILL(DirectiveExecutors.NOT_IMPLEMENTED), // Remplir n fois, un nombre de taille x, de valeur y

    // Other
    ALIGN(DirectiveExecutors.NOT_IMPLEMENTED); // Alignement des données sur la grille des 4 bytes

    private final DirectiveExecutor executor;

    Directive(DirectiveExecutor executor) {
        this.executor = executor;
    }

    /**
     * Calcul de la place en mémoire nécessaire pour cette directive
     * @param args la chaine d'arguments
     */
    public int computeDataLength(String args) throws SyntaxASMException {
        return executor.computeDataLength(args);
    }

    /**
     * Application de la directive
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args la chaine d'arguments
     */
    public void apply(StateContainer stateContainer, String args) {
        executor.apply(stateContainer, args);
    }
}
