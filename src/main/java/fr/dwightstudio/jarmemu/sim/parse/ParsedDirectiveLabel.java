package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class ParsedDirectiveLabel extends ParsedObject {

    private final String name;

    public ParsedDirectiveLabel(String name) {
        this.name = name.toUpperCase();
    }

    /**
     * Vérifie la syntaxe de l'objet et renvoie les erreurs.
     *
     * @param line          le numéro de la ligne
     * @param stateSupplier un fournisseur de conteneur d'état
     * @return les erreurs détectées
     */
    @Override
    public AssemblyError verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();

        if (container.data.get(this.name) == null) {
            throw new IllegalStateException("Unable to verify directive label (incorrectly registered in the StateContainer)");
        }

        return null;
    }

    public void register(StateContainer stateContainer, int currentPos) {
        stateContainer.data.put(name.strip().toUpperCase(), currentPos + stateContainer.getSymbolsAddress());
    }
}
