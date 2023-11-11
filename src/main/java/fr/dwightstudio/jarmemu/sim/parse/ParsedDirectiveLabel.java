package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedDirectiveLabel extends ParsedObject {

    private final String name;
    private final Logger logger = Logger.getLogger(getClass().getName());

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
    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();

        if (container.data.get(this.name) == null) {
            throw new IllegalStateException("Unable to verify directive label (incorrectly registered in the StateContainer)");
        }

        return null;
    }

    public void register(StateContainer stateContainer, int currentPos) {
        stateContainer.data.put(name.strip().toUpperCase(), currentPos + stateContainer.getSymbolsAddress());
    }

    @Override
    public String toString() {
        return "DirectiveLabel";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirectiveLabel label)) return false;

        if (label.name == null) {
            if (this.name != null) {
                if (VERBOSE) logger.info("Difference: Name (Null)");
                return false;
            }
        } else {
            if (!(label.name.equalsIgnoreCase(this.name))) {
                if (VERBOSE) logger.info("Difference: Name");
                return false;
            }
        }

        return true;
    }
}
