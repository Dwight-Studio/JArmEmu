package fr.dwightstudio.jarmemu;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public class JArmEmuTest {

    private static boolean alreadySetup = false;

    @BeforeAll
    public static void setUpAll() throws IOException {
        if (!alreadySetup) {
            JArmEmuLauncher.setUpLogger();
            alreadySetup = true;
        }
    }

}
