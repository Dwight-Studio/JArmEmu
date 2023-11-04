module fr.dwightstudio.jarmemu {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.logging;
    requires org.jetbrains.annotations;
    requires org.apache.commons.lang3;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires exp4j;
    requires java.prefs;
    requires java.desktop;
    requires org.controlsfx.controls;

    exports fr.dwightstudio.jarmemu;
    exports fr.dwightstudio.jarmemu.gui;
    exports fr.dwightstudio.jarmemu.gui.controllers;
    exports fr.dwightstudio.jarmemu.sim;
    exports fr.dwightstudio.jarmemu.sim.obj;
    exports fr.dwightstudio.jarmemu.sim.parse;
    exports fr.dwightstudio.jarmemu.sim.parse.regex;
    exports fr.dwightstudio.jarmemu.asm;
    exports fr.dwightstudio.jarmemu.asm.exceptions;

    opens fr.dwightstudio.jarmemu to javafx.fxml;
    opens fr.dwightstudio.jarmemu.gui to javafx.fxml;
    opens fr.dwightstudio.jarmemu.gui.controllers to javafx.fxml;
}