package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ParsedDirectivePack extends ParsedObject {

    ArrayList<ParsedObject> directives;


    public ParsedDirectivePack() {
        directives = new ArrayList<>();
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
        for (ParsedObject directive : directives) {
            AssemblyError error = directive.verify(line, stateSupplier);
            if (error != null) return error;
        }

        return null;
    }

    public boolean add(ParsedObject directive) {
        if (directive instanceof ParsedDirective || directive instanceof ParsedLabel) {
            return directives.add(directive);
        } else {
            throw new IllegalArgumentException("ParsedDirectivePack can only accept ParsedDirective or ParsedLabel");
        }
    }

    /**
     * Vérifie le nombre d'entrées, et dés-encapsule le ParsedObject si nécessaire
     *
     * @return lui-même s'il y a plusieurs entrées, la première entrée si elle est seule ou null s'il est vide
     */
    public ParsedObject close() {
        if (directives.isEmpty()) {
            return null;
        } else if (directives.size() == 1) {
            return directives.get(0);
        } else {
            return this;
        }
    }

    /**
     * Application des directives
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     */
    public void apply(StateContainer stateContainer) {
        for (ParsedObject directive : directives) {
            if (directive instanceof ParsedDirective dir) {
                dir.apply(stateContainer);
            } else if (directive instanceof ParsedLabel label) {
                label.register(stateContainer);
            }
        }
    }

    /**
     * Calcul de la place en mémoire nécessaire pour ces directives
     */
    public int computeDataLength() {
        int length = 0;
        for (ParsedObject directive : directives) {
            if (directive instanceof ParsedDirective dir) {
                length += dir.computeDataLength();
            }
        }

        return length;
    }
}
