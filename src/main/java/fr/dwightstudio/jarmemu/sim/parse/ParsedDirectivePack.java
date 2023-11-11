package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ParsedDirectivePack extends ParsedObject {

    ArrayList<ParsedObject> content;


    public ParsedDirectivePack() {
        content = new ArrayList<>();
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
        for (ParsedObject directive : content) {
            SyntaxASMException error = directive.verify(line, stateSupplier);
            if (error != null) return error;
        }

        return null;
    }

    public boolean add(ParsedObject directive) {
        if (directive instanceof ParsedDirective || directive instanceof ParsedDirectiveLabel || directive instanceof ParsedSection) {
            return content.add(directive);
        } else {
            throw new IllegalArgumentException("ParsedDirectivePack can only accept ParsedDirective or ParsedDirectiveLabel");
        }
    }

    /**
     * Vérifie le nombre d'entrées, et dés-encapsule le ParsedObject si nécessaire
     *
     * @return lui-même s'il y a plusieurs entrées, la première entrée si elle est seule ou null s'il est vide
     */
    public ParsedObject close() {
        if (content.isEmpty()) {
            return null;
        } else if (content.size() == 1) {
            return content.getFirst();
        } else {
            return this;
        }
    }

    /**
     * Application des directives
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     */
    public int apply(StateContainer stateContainer, int pos) {
        for (ParsedObject directive : content) {
            if (directive instanceof ParsedDirective dir) {
                pos = dir.apply(stateContainer, pos);
            } else if (directive instanceof ParsedDirectiveLabel label) {
                label.register(stateContainer, pos);
            }
        }

        return pos;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public boolean isGenerated() {
        boolean flag = false;

        for (ParsedObject object : content) {
            if (object instanceof ParsedDirective directive) {
                flag = flag || directive.isGenerated();
            }
        }

        return flag;
    }

    @Override
    public String toString() {
        return "Directives";
    }

    public ParsedObject[] getContent() {
        return this.content.toArray(new ParsedObject[0]);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirectivePack pack)) return false;
        return pack.content.equals(content);
    }
}
