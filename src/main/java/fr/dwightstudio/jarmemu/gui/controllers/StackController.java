package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public StackController(JArmEmuApplication application) {
        super(application);
    }

}
