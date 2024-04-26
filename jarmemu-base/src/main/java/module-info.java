module fr.dwightstudio.jarmemu.base {
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
    requires org.kohsuke.github.api;

    exports fr.dwightstudio.jarmemu.base;
    exports fr.dwightstudio.jarmemu.base.gui;
    exports fr.dwightstudio.jarmemu.base.gui.controllers;
    exports fr.dwightstudio.jarmemu.base.sim;
    exports fr.dwightstudio.jarmemu.base.sim.entity;
    exports fr.dwightstudio.jarmemu.base.asm;
    exports fr.dwightstudio.jarmemu.base.asm.parser.regex;
    //exports fr.dwightstudio.jarmemu.asm.parser.legacy;
    exports fr.dwightstudio.jarmemu.base.asm.exception;
    exports fr.dwightstudio.jarmemu.base.asm.instruction;
    exports fr.dwightstudio.jarmemu.base.asm.directive;
    exports fr.dwightstudio.jarmemu.base.asm.argument;
    exports fr.dwightstudio.jarmemu.base.asm.parser;

    opens fr.dwightstudio.jarmemu.base to javafx.fxml;
    opens fr.dwightstudio.jarmemu.base.gui to javafx.fxml;
    opens fr.dwightstudio.jarmemu.base.gui.controllers to javafx.fxml;
    exports fr.dwightstudio.jarmemu.base.gui.enums;
    opens fr.dwightstudio.jarmemu.base.gui.enums to javafx.fxml;
    exports fr.dwightstudio.jarmemu.base.gui.factory;
    opens fr.dwightstudio.jarmemu.base.gui.factory to javafx.fxml;
    exports fr.dwightstudio.jarmemu.base.gui.editor;
    opens fr.dwightstudio.jarmemu.base.gui.editor to javafx.fxml;
    exports fr.dwightstudio.jarmemu.base.sim.prepare;
}