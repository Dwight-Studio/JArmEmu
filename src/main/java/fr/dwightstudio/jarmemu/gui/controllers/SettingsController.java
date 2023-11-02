package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;

import java.util.logging.Logger;

public class SettingsController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public SettingsController(JArmEmuApplication application) {
        super(application);
    }
}
