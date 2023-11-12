package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.LineStatus;
import javafx.scene.control.Alert;

import java.util.logging.Logger;

public class SimulationMenuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private Alert loadingAlert;

    public SimulationMenuController(JArmEmuApplication application) {
        super(application);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSimulate() {
        getController().simulate.setDisable(true);
        getEditorController().clearNotifs();

        getEditorController().addNotif("Parsing in progress...", "Please wait, this can take up to a few seconds.", Styles.ACCENT);

        getExecutionWorker().revive();
        getExecutionWorker().prepare();
    }

    /**
     * Méthode de rappel si la préparation de la simulation s'est effectué avec succès
     * @param errors les erreurs rencontrées lors de l'analyse du code
     */
    public void launchSimulation(SyntaxASMException[] errors) {
        getEditorController().clearNotifs();

        if (errors.length == 0) {
            if (getCodeInterpreter().getInstructionCount() == 0) {
                getController().simulate.setDisable(false);
                getEditorController().addNotif("Simulation error: ", "No instructions detected (did you forget '.text'?)", Styles.DANGER);
            } else {
                getEditorController().clearLineMarking();
                getEditorController().markLine(getCodeInterpreter().getNextLine(), LineStatus.SCHEDULED);
                getController().stackGrid.setDisable(false);
                getController().memoryPage.setDisable(false);
                getController().addressField.setDisable(false);
                getController().codeArea.setEditable(false);
                getController().stepInto.setDisable(false);
                getController().stepOver.setDisable(false);
                getController().conti.setDisable(false);
                getController().pause.setDisable(true);
                getController().stop.setDisable(false);
                getController().restart.setDisable(false);
                getController().settingsTab.setDisable(true);
                application.status = Status.SIMULATING;
            }
        } else {
            getController().simulate.setDisable(false);
            for (SyntaxASMException error : errors) {
                getEditorController().addError(error);
            }
        }
    }

    /**
     * Méthode de rappel si la préparation de la simulation a échoué
     */
    public void abortSimulation() {
        getEditorController().clearNotifs();
        getEditorController().addNotif("Parsing error: ", "Exceptions prevented the code from being parsed. See console for more details.", Styles.DANGER);
        getController().simulate.setDisable(false);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepInto() {
        getEditorController().clearNotifs();
        getExecutionWorker().stepInto();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepOver() {
        getEditorController().clearNotifs();
        getExecutionWorker().stepOver();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onContinue() {
        getEditorController().clearNotifs();
        getExecutionWorker().conti();
        getController().stepInto.setDisable(true);
        getController().stepOver.setDisable(true);
        getController().conti.setDisable(true);
        getController().pause.setDisable(false);
        getController().restart.setDisable(true);
        getController().stackGrid.setDisable(true);
        getController().memoryPage.setDisable(true);
        getController().addressField.setDisable(true);
        getController().codeArea.setDisable(true);
        getController().editorScroll.setDisable(true);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onPause() {
        getExecutionWorker().pause();
        getController().stepInto.setDisable(false);
        getController().stepOver.setDisable(false);
        getController().conti.setDisable(false);
        getController().pause.setDisable(true);
        getController().restart.setDisable(false);
        getController().stackGrid.setDisable(false);
        getController().memoryPage.setDisable(false);
        getController().addressField.setDisable(false);
        getController().codeArea.setDisable(false);
        getController().editorScroll.setDisable(false);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStop() {
        getEditorController().clearNotifs();
        getExecutionWorker().pause();
        getController().codeArea.setDisable(false);
        getController().codeArea.setEditable(true);
        getController().editorScroll.setDisable(false);
        getController().simulate.setDisable(false);
        getController().stepInto.setDisable(true);
        getController().stepOver.setDisable(true);
        getController().conti.setDisable(true);
        getController().pause.setDisable(true);
        getController().stop.setDisable(true);
        getController().restart.setDisable(true);
        getController().stackGrid.setDisable(true);
        getController().memoryPage.setDisable(true);
        getController().addressField.setDisable(true);
        getController().settingsTab.setDisable(false);
        getEditorController().clearLineMarking();
        application.status = Status.EDITING;
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onRestart() {
        getEditorController().clearNotifs();
        getStackController().clear();
        getEditorController().clearLineMarking();

        getCodeInterpreter().resetState(getSettingsController().getStackAddress(), getSettingsController().getSymbolsAddress());
        getCodeInterpreter().restart();

        getEditorController().markLine(getCodeInterpreter().getNextLine(), LineStatus.SCHEDULED);
        getExecutionWorker().restart();
    }
}
