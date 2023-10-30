package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;

import java.util.Set;

public abstract class ParsedObject {

    public static final boolean VERBOSE = true;

    /**
     * Vérifie la syntaxe de l'objet et renvoie les erreurs.
     *
     * @param line le numéro de la ligne
     * @param labels les étiquettes enregistrées
     * @return les erreurs détectées
     */
    public abstract AssemblyError verify(int line, Set<String> labels);

}
