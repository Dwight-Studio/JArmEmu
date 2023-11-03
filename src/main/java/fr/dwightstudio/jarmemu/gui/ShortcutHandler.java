package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.gui.controllers.AbstractJArmEmuModule;
import javafx.scene.input.KeyEvent;

public class ShortcutHandler extends AbstractJArmEmuModule {
    public ShortcutHandler(JArmEmuApplication application) {
        super(application);
    }

    public void handle(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case S -> getMainMenuController().onSave();
                case O -> getMainMenuController().onOpen();
                case R -> getMainMenuController().onReload();
                case N -> getMainMenuController().onNewFile();
            }
        }
    }
}
