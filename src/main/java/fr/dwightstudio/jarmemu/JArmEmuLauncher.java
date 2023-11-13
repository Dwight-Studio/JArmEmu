package fr.dwightstudio.jarmemu;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JArmEmuLauncher {

    private static Logger logger = Logger.getLogger(JArmEmuLauncher.class.getName());

    public static void main(String[] args) throws IOException {
        setUpLogger();
        JArmEmuApplication.main(args);
    }

    public static void setUpLogger() throws IOException {
        LogManager.getLogManager().readConfiguration(JArmEmuApplication.getResourceAsStream("logging.properties"));
    }
}
