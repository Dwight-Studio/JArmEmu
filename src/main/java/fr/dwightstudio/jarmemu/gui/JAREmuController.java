package fr.dwightstudio.jarmemu.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class JAREmuController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}