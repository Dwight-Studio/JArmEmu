package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.HashMap;
import java.util.function.Supplier;

public class ParsedSection extends ParsedObject {

    private final Section section;

    public ParsedSection(Section section) {
        this.section = section;
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
        return null;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedSection parsedSection)) return false;

        return parsedSection.section == section;
    }
}
