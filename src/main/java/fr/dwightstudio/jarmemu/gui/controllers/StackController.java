package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected ArrayList<Text> stack;

    public StackController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stack = new ArrayList<>();
    }
}
