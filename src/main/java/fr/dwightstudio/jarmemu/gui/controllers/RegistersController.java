package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class RegistersController extends AbstractJArmEmuModule {

    protected static final String HEX_FORMAT = "%08x";
    // TODO: Ajouter le DEC avec redimensionnement des cases (modifier dans la mémoire et le stack aussi)

    private final Logger logger = Logger.getLogger(getClass().getName());
    private Text[] registers;

    public RegistersController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registers = new Text[]{getController().R0, getController().R1, getController().R2, getController().R3, getController().R4, getController().R5, getController().R6, getController().R7, getController().R8, getController().R9, getController().R10, getController().R11, getController().R12, getController().R13, getController().R14, getController().R15};
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        if (stateContainer != null) {
            for (int i = 0; i < 16; i++) {
                registers[i].setText(String.format(HEX_FORMAT, stateContainer.registers[i].getData()).toUpperCase());
            }

            getController().CPSR.setText(String.format(HEX_FORMAT, stateContainer.cpsr.getData()).toUpperCase());
            getController().CPSRT.setText(stateContainer.cpsr.toString());
            getController().SPSR.setText(String.format(HEX_FORMAT, stateContainer.spsr.getData()).toUpperCase());
            getController().SPSRT.setText(stateContainer.spsr.toString());
        }
    }
}
