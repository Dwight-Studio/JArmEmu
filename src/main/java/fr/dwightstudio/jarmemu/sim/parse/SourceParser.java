/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.SourceScanner;

import java.util.TreeMap;

public interface SourceParser {

    int DEFAULT_SOURCE_PARSER = 0;

    /**
     * @return le CodeScanner utilisé par le parseur
     */
    public SourceScanner getSourceScanner();

    /**
     * Définie le CodeScanner à utiliser par le parseur
     * @param sourceScanner le CodeScanner à utiliser
     */
    public void setSourceScanner(SourceScanner sourceScanner);

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des objets parsés non vérifiés
     */
    public TreeMap<Integer, ParsedObject> parse();

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @return un ParsedObject non vérifié
     */
    public ParsedObject parseOneLine();
}
