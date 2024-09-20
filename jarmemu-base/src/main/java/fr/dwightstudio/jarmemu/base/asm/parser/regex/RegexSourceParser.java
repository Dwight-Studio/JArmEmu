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

package fr.dwightstudio.jarmemu.base.asm.parser.regex;

import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.DeprecatedASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.NotImplementedASMException;
import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import org.jetbrains.annotations.NotNull;

public class RegexSourceParser implements SourceParser {

    protected Section currentSection;

    public RegexSourceParser() {
        currentSection = Section.NONE;
    }

    /**
     * Read the file and returns the unverified parsed objects.
     *
     * @param sourceScanner the source scanner from which read
     * @return the parsed file containing the unverified parsed objects
     */
    @Override
    public ParsedFile parse(SourceScanner sourceScanner) throws ASMException {
        ParsedFile file = new ParsedFile(sourceScanner);
        sourceScanner.goTo(-1);
        currentSection = Section.NONE;

        while (sourceScanner.hasNextLine()){
            String line = sourceScanner.nextLine();

            line = prepare(line);
            if (line.isEmpty() || Section.END.equals(currentSection)) continue;

            try {
                boolean found = DirectiveParser.parseOneLine(this, line, sourceScanner, file);
                if (!found && currentSection == Section.TEXT) ASMParser.parseOneLine(this, line, sourceScanner, file);
            } catch (NotImplementedASMException exception) {
                if (!JArmEmuApplication.getSettingsController().getIgnoreUnimplemented()) {
                    throw exception.with(file).with(sourceScanner.getLineNumber());
                }
            } catch (DeprecatedASMException exception) {
                if (!JArmEmuApplication.getSettingsController().getIgnoreDeprecated()) {
                    throw exception.with(file).with(sourceScanner.getLineNumber());
                }
            } catch (ASMException exception) {
                throw exception.with(file).with(sourceScanner.getLineNumber());
            }
        }

        return file;
    }

    /**
     * Prepare the line to parsing by cleaning it (removing comments and blanks).
     *
     * @param line the string to clean
     * @return the string without comments or trailing blanks
     */
    public static String prepare(@NotNull String line) {
        return line.split("@")[0].strip();
    }
}
