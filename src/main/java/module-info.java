module fr.dwightstudio.jarmemu.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.jetbrains.annotations;
    requires java.logging;

    opens fr.dwightstudio.jarmemu.gui to javafx.fxml;
    exports fr.dwightstudio.jarmemu.gui;
}