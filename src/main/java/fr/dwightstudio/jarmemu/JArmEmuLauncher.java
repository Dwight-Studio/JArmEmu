package fr.dwightstudio.jarmemu;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;

import java.io.IOException;
import java.util.logging.LogManager;

public class JArmEmuLauncher {
    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(JArmEmuLauncher.class.getResourceAsStream("logging.properties"));
        JArmEmuApplication.main(args);
    }
}
