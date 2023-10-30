module fr.dwightstudio.jarmemu {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.logging;
    requires org.jetbrains.annotations;
    requires org.apache.commons.lang3;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires exp4j;

    opens fr.dwightstudio.jarmemu.gui to javafx.fxml;
    exports fr.dwightstudio.jarmemu.gui;
    exports fr.dwightstudio.jarmemu.sim;
    exports fr.dwightstudio.jarmemu.asm;
    exports fr.dwightstudio.jarmemu;
    opens fr.dwightstudio.jarmemu to javafx.fxml;
    exports fr.dwightstudio.jarmemu.sim.obj;
    exports fr.dwightstudio.jarmemu.asm.exceptions;
    exports fr.dwightstudio.jarmemu.sim.parse;
    exports fr.dwightstudio.jarmemu.sim.parse.regex;
}