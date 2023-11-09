package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedObject {

    public static final boolean VERBOSE = true;

    /**
     * Vérifie la syntaxe de l'objet et renvoie les erreurs.
     *
     * @param line   le numéro de la ligne
     * @param stateSupplier un fournisseur de conteneur d'état
     * @return les erreurs détectées
     */
    public abstract SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier);

}
