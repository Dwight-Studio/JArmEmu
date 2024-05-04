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

import fr.dwightstudio.jarmemu.base.asm.directive.*;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public enum Directive {
    // Consts
    SET(EquivalentDirective.class), EQU(EquivalentDirective.class), EQUIV(EquivalentDirective.class), EQV(EquivalentDirective.class), // Définir une constante
    GLOBAL(GlobalDirective.class), GLOBL(GlobalDirective.class), EXPORT(GlobalDirective.class),

    // Data
    WORD(WordDirective.class), // Donnée sur 32bits
    HALF(HalfDirective.class), // Donnée sur 16bits
    BYTE(ByteDirective.class), // Donnée sur 8bits
    SPACE(SpaceDirective.class), SKIP(SpaceDirective.class), // Vide sur nbits
    ASCII(ASCIIDirective.class), // Chaîne de caractères
    ASCIZ(ASCIIDirective.class), // Chaîne de caractère finissant par '\0'
    FILL(FillDirective.class), // Remplir n fois, un nombre de taille x, de valeur y

    // Other
    ALIGN(AlignDirective.class); // Alignement des données sur la grille des 4 bytes

    private final Class<? extends ParsedDirective> directiveClass;

    Directive(Class<? extends ParsedDirective> directiveClass) {
        this.directiveClass = directiveClass;
    }

    public ParsedDirective create(Section section, @NotNull String args) throws ASMException {
        try {
            return this.directiveClass.getDeclaredConstructor(Section.class, String.class).newInstance(section, args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof ASMException ex) throw ex;
            else throw  new RuntimeException(e);
        }
    }
}
