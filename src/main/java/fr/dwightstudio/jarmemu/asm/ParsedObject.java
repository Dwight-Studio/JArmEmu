package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedObject {

    /**
     * Effectue un test pour vérifier la capacité d'exécution
     *
     * @param stateSupplier le fournisseur d'état de base (état pré-exécution)
     * @param currentLine la ligne ligne actuelle de l'objet
     * @throws ASMException lorsque une erreur est détectée
     */
    public abstract void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException;
}
