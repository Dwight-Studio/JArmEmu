/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
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
                getEditorController().addNotif("Simulation error", "No instructions detected (did you forget '.text'?).", Styles.DANGER);
            } else {
                getEditorController().clearLineMarking();
                getEditorController().markLine(getCodeInterpreter().getNextLine(), LineStatus.SCHEDULED);
                getController().stackTab.setDisable(false);
                getController().memoryTab.setDisable(false);
                getController().addressField.setDisable(false);
                getController().codeArea.setEditable(false);
                getController().stepInto.setDisable(false);
                getController().stepOver.setDisable(false);
                getController().conti.setDisable(false);
                getController().pause.setDisable(true);
                getController().stop.setDisable(false);
                getController().restart.setDisable(false);
                getController().settingsRegex.setDisable(true);
                getController().settingsLegacy.setDisable(true);
                getController().registersTab.setDisable(false);
                application.status = Status.SIMULATING;
                getController().tabPane.getSelectionModel().select(getController().memoryTab);
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
        getEditorController().addNotif("Parsing error", "Exceptions prevented the code from being parsed. See console for more details.", Styles.DANGER);
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
        getController().memoryTab.setDisable(true);
        getController().addressField.setDisable(true);
        getController().settingsRegex.setDisable(false);
        getController().settingsLegacy.setDisable(false);
        getController().registersTab.setDisable(true);
        getController().stackTab.setDisable(true);
        getCodeInterpreter().clearState();
        getEditorController().clearLineMarking();
        getExecutionWorker().updateGUI();
        application.status = Status.EDITING;
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onRestart() {
        getEditorController().clearNotifs();
        getEditorController().clearLineMarking();

        getCodeInterpreter().resetState(getSettingsController().getStackAddress(), getSettingsController().getSymbolsAddress());
        getCodeInterpreter().restart();

        getEditorController().markLine(getCodeInterpreter().getNextLine(), LineStatus.SCHEDULED);
        getExecutionWorker().restart();
    }
}
