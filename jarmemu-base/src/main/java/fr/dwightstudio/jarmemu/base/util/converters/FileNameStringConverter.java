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

package fr.dwightstudio.jarmemu.base.util.converters;

import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import javafx.util.StringConverter;

public class FileNameStringConverter extends StringConverter<Number> {


    @Override
    public String toString(Number number) {
        return JArmEmuApplication.getEditorController().getFileEditors().get((Integer) number).getFileName();
    }

    @Override
    public Number fromString(String s) {
        int rtn = -1;

        for (int i = 0 ; i < JArmEmuApplication.getEditorController().getFileEditors().size() ; i++) {
            if (JArmEmuApplication.getEditorController().getFileEditors().get(i).getFileName().equals(s)) {
                rtn = i;
                break;
            }
        }

        return rtn;
    }
}
