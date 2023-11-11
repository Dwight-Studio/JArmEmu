package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class RegistersController extends AbstractJArmEmuModule {

    private int DATA_FORMAT;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private StringProperty[] stringProperties;

    public RegistersController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Text[] texts = new Text[]{getController().R0, getController().R1, getController().R2, getController().R3, getController().R4, getController().R5, getController().R6, getController().R7, getController().R8, getController().R9, getController().R10, getController().R11, getController().R12, getController().R13, getController().R14, getController().R15, getController().CPSR, getController().SPSR, getController().CPSRT, getController().SPSRT};
        stringProperties = new StringProperty[texts.length];

        for (int i = 0 ; i < texts.length ; i ++) {
            StringProperty property = new SimpleStringProperty();
            texts[i].textProperty().bind(property);
            property.set("-");
            stringProperties[i] = property;
        }
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        DATA_FORMAT = getSettingsController().getDataFormat();

        if (stateContainer != null) {
            for (int i = 0; i < 16; i++) {
                stringProperties[i].set(getApplication().getFormattedData(stateContainer.registers[i].getData(), DATA_FORMAT));
            }

            stringProperties[16].set(getApplication().getFormattedData(stateContainer.cpsr.getData(), DATA_FORMAT));
            stringProperties[17].set(getApplication().getFormattedData(stateContainer.spsr.getData(), DATA_FORMAT));
            stringProperties[18].set(stateContainer.cpsr.toString());
            stringProperties[19].set(stateContainer.spsr.toString());
        }
    }
}
