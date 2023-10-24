module fr.dwightstudio.jarmemu.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.logging;
    requires org.jetbrains.annotations;
    requires org.apache.commons.lang3;

    opens fr.dwightstudio.jarmemu.gui to javafx.fxml;
    exports fr.dwightstudio.jarmemu.gui;
}