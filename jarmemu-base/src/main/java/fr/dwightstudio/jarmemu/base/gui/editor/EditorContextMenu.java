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

package fr.dwightstudio.jarmemu.base.gui.editor;

import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.controllers.FileEditor;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public class EditorContextMenu extends ContextMenu {

    FileEditor editor;
    MenuItem cut;
    MenuItem copy;
    MenuItem paste;
    MenuItem delete;
    MenuItem breakpoint;

    public EditorContextMenu(FileEditor editor) {
        this.editor = editor;

        copy = new MenuItem(JArmEmuApplication.formatMessage("%menu.edit.copy"), new FontIcon(Material2OutlinedAL.CONTENT_COPY));
        cut = new MenuItem(JArmEmuApplication.formatMessage("%menu.edit.cut"), new FontIcon(Material2OutlinedAL.CONTENT_CUT));
        paste = new MenuItem(JArmEmuApplication.formatMessage("%menu.edit.paste"), new FontIcon(Material2OutlinedAL.CONTENT_PASTE));
        delete = new MenuItem(JArmEmuApplication.formatMessage("%menu.edit.delete"), new FontIcon(Material2OutlinedAL.DELETE));
        breakpoint = new MenuItem(JArmEmuApplication.formatMessage("%menu.edit.breakpoint"), new FontIcon(Material2OutlinedMZ.STOP_CIRCLE));

        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
        breakpoint.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN));

        copy.setOnAction(this::onCopy);
        cut.setOnAction(this::onCut);
        paste.setOnAction(this::onPaste);
        delete.setOnAction(this::onDelete);
        breakpoint.setOnAction(this::onToggleBreakpoint);

        getItems().add(copy);
        getItems().add(cut);
        getItems().add(paste);
        getItems().add(delete);
        getItems().add(breakpoint);
    }

    @Override
    protected void show() {
        super.show();


        if (!editor.isMouseOverSelection()) {
            int pos = editor.getMousePosition();
            if (pos < 0) {
                int line = editor.getMouseLine();
                if (line >= 0) {
                    int column = editor.getCodeArea().getParagraphLength(line);
                    if (column >= 0) {
                        editor.getCodeArea().moveTo(line, column);
                    }
                }
            } else {
                editor.getCodeArea().moveTo(pos);
            }
        }
    }

    public void onCut(ActionEvent event) {
        ClipboardContent content = new ClipboardContent();
        content.putString(editor.getCodeArea().getSelectedText());
        Clipboard.getSystemClipboard().setContent(content);
        editor.getCodeArea().replaceSelection("");
    }

    public void onCopy(ActionEvent event) {
        ClipboardContent content = new ClipboardContent();
        content.putString(editor.getCodeArea().getSelectedText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void onPaste(ActionEvent event) {
        editor.getCodeArea().replaceSelection(Clipboard.getSystemClipboard().getString());
    }

    public void onDelete(ActionEvent event) {
        editor.getCodeArea().replaceSelection("");
    }

    public void onToggleBreakpoint(ActionEvent actionEvent) {
        editor.toggleBreakpoint(editor.getCodeArea().getCurrentParagraph());
    }
}
