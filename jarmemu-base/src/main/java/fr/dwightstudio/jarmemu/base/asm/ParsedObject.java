/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
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

package fr.dwightstudio.jarmemu.base.asm;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedObject {

    protected static final boolean VERBOSE = true;

    private ParsedFile file;
    private int lineNumber;
    private boolean generated;

    /**
     * Effectue un test pour vérifier la capacité d'exécution
     *
     * @param stateSupplier le fournisseur d'état de base (état pré-exécution)
     * @throws ASMException lorsque une erreur est détectée
     */
    public abstract void verify(Supplier<StateContainer> stateSupplier) throws ASMException;

    public ParsedFile getFile() {
        return file;
    }

    public void setFile(ParsedFile file) {
        this.file = file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return la position dans le programme (fichier et ligne)
     */
    public FilePos getFilePos() {
        return new FilePos(file == null ? -1 : file.getIndex(), lineNumber).freeze();
    }

    /**
     * @return vrai si l'objet a été généré
     */
    public boolean isGenerated() {
        return generated;
    }

    /**
     * Définie l'objet comme généré
     */
    public void setGenerated() {
        this.generated = true;
    }
}
