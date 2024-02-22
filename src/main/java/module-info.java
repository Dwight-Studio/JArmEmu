module fr.dwightstudio.jarmemu {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires java.logging;
    requires org.jetbrains.annotations;
    requires org.apache.commons.lang3;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires exp4j;
    requires java.prefs;
    requires java.desktop;
    requires org.controlsfx.controls;
    requires org.fxmisc.flowless;
    requires atlantafx.base;
    requires org.apache.commons.collections4;
    requires org.json;

    exports fr.dwightstudio.jarmemu;
    exports fr.dwightstudio.jarmemu.gui;
    exports fr.dwightstudio.jarmemu.gui.controllers;
    exports fr.dwightstudio.jarmemu.sim;
    exports fr.dwightstudio.jarmemu.sim.obj;
    exports fr.dwightstudio.jarmemu.sim.parse;
    exports fr.dwightstudio.jarmemu.asm.parser.regex;
    exports fr.dwightstudio.jarmemu.oasm;

    opens fr.dwightstudio.jarmemu to javafx.fxml;
    opens fr.dwightstudio.jarmemu.gui to javafx.fxml;
    opens fr.dwightstudio.jarmemu.gui.controllers to javafx.fxml;
    exports fr.dwightstudio.jarmemu.gui.enums;
    opens fr.dwightstudio.jarmemu.gui.enums to javafx.fxml;
    exports fr.dwightstudio.jarmemu.gui.factory;
    opens fr.dwightstudio.jarmemu.gui.factory to javafx.fxml;
    exports fr.dwightstudio.jarmemu.gui.editor;
    opens fr.dwightstudio.jarmemu.gui.editor to javafx.fxml;
    exports fr.dwightstudio.jarmemu.asm.exception;
    exports fr.dwightstudio.jarmemu.asm.parser.legacy;
    exports fr.dwightstudio.jarmemu.asm;
    exports fr.dwightstudio.jarmemu.asm.parser;
}