/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
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
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;

import java.util.logging.Logger;

public class SimulationMenuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    public SimulationMenuController(JArmEmuApplication application) {
        super(application);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSimulate() {
        if (getController().menuSimulate.isDisable()) return;

        getController().toolSimulate.setDisable(true);
        getController().menuSimulate.setDisable(true);
        getEditorController().clearNotifs();

        getEditorController().addNotification(
                JArmEmuApplication.formatMessage("%notification.parsing.title"),
                JArmEmuApplication.formatMessage("%notification.parsing.message"),
                Styles.ACCENT
        );

        getExecutionWorker().revive();
        getExecutionWorker().prepare();
    }

    /**
     * Méthode de rappel si la préparation de la simulation s'est effectué avec succès
     * @param errors les erreurs rencontrées lors de l'analyse du code
     */
    public void launchSimulation(ASMException[] errors) {
        getEditorController().clearNotifs();

        if (errors == null || errors.length == 0) {
            if (getCodeInterpreter().getInstructionCount() == 0) {
                getController().toolSimulate.setDisable(false);
                getController().menuSimulate.setDisable(false);
                getEditorController().addNotification(
                        JArmEmuApplication.formatMessage("%notification.noInstruction.title"),
                        JArmEmuApplication.formatMessage("%notification.noInstruction.message"),
                        Styles.DANGER
                );
            } else {
                getEditorController().clearAllLineMarkings();
                getEditorController().markForward(getCodeInterpreter().getCurrentLine());
                getController().memoryDetailsAddressField.setDisable(false);
                getController().memoryOverviewAddressField.setDisable(false);
                getEditorController().onLaunch();
                getController().toolStepInto.setDisable(false);
                getController().menuStepInto.setDisable(false);
                getController().toolStepOver.setDisable(false);
                getController().menuStepOver.setDisable(false);
                getController().toolContinue.setDisable(false);
                getController().menuContinue.setDisable(false);
                getController().toolPause.setDisable(true);
                getController().menuPause.setDisable(true);
                getController().toolStop.setDisable(false);
                getController().menuStop.setDisable(false);
                getController().toolRestart.setDisable(false);
                getController().menuRestart.setDisable(false);
                getController().settingsRegex.setDisable(true);
                getController().settingsLegacy.setDisable(true);
                getController().settingsStackAddress.setDisable(true);
                getController().settingsSymbolsAddress.setDisable(true);
                getController().settingsSimInterval.setDisable(true);

                getController().stackPane.setDisable(false);
                getController().registersPane.setDisable(false);
                getController().memoryDetailsPane.setDisable(false);
                getController().memoryOverviewPane.setDisable(false);
                getController().labelsPane.setDisable(false);
                getController().symbolsPane.setDisable(false);
                getEditorController().getFileEditors().forEach(FileEditor::closeFindAndReplace);
                getController().findAndReplace.setDisable(true);

                getExecutionWorker().restart();

                application.status.set(Status.SIMULATING);
            }
        } else {
            getController().toolSimulate.setDisable(false);
            getController().menuSimulate.setDisable(false);
            for (ASMException error : errors) {
                getEditorController().addError(error);
            }
        }
    }

    /**
     * Méthode de rappel si la préparation de la simulation a échoué
     */
    public void abortSimulation() {
        getEditorController().clearNotifs();
        getEditorController().addNotification(
                JArmEmuApplication.formatMessage("%notification.parsingError.title"),
                JArmEmuApplication.formatMessage("%notification.parsingError.message"),
                Styles.DANGER
        );
        getController().toolSimulate.setDisable(false);
        getController().menuSimulate.setDisable(false);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepInto() {
        if (getController().menuStepInto.isDisable()) return;
        getEditorController().clearNotifs();
        getExecutionWorker().stepInto();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepOver() {
        if (getController().menuStepOver.isDisable()) return;
        getEditorController().clearNotifs();
        getExecutionWorker().stepOver();
        getController().toolStepInto.setDisable(true);
        getController().menuStepInto.setDisable(true);
        getController().toolStepOver.setDisable(true);
        getController().menuStepOver.setDisable(true);
        getController().toolContinue.setDisable(true);
        getController().menuContinue.setDisable(true);
        getController().toolPause.setDisable(false);
        getController().menuPause.setDisable(false);
        getController().toolRestart.setDisable(true);
        getController().menuRestart.setDisable(true);
        getController().memoryDetailsAddressField.setDisable(true);
        getController().memoryOverviewAddressField.setDisable(true);
        getEditorController().onContinueOrStepOver();}

    /**
     * Méthode invoquée par JavaFX
     */
    public void onContinue() {
        if (getController().menuContinue.isDisable()) return;
        getEditorController().clearNotifs();
        getExecutionWorker().conti();
        getController().toolStepInto.setDisable(true);
        getController().menuStepInto.setDisable(true);
        getController().toolStepOver.setDisable(true);
        getController().menuStepOver.setDisable(true);
        getController().toolContinue.setDisable(true);
        getController().menuContinue.setDisable(true);
        getController().toolPause.setDisable(false);
        getController().menuPause.setDisable(false);
        getController().toolRestart.setDisable(true);
        getController().menuRestart.setDisable(true);
        getController().memoryDetailsAddressField.setDisable(true);
        getController().memoryOverviewAddressField.setDisable(true);
        getEditorController().onContinueOrStepOver();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onPause() {
        if (getController().menuPause.isDisable()) return;
        getExecutionWorker().pause();
        getController().toolStepInto.setDisable(false);
        getController().menuStepInto.setDisable(false);
        getController().toolStepOver.setDisable(false);
        getController().menuStepOver.setDisable(false);
        getController().toolContinue.setDisable(false);
        getController().menuContinue.setDisable(false);
        getController().toolPause.setDisable(true);
        getController().menuPause.setDisable(true);
        getController().toolRestart.setDisable(false);
        getController().menuRestart.setDisable(false);
        getController().memoryDetailsAddressField.setDisable(false);
        getController().memoryOverviewAddressField.setDisable(false);
        getEditorController().onPause();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStop() {
        if (getController().menuStop.isDisable()) return;
        getEditorController().clearNotifs();
        getExecutionWorker().pause();
        getEditorController().onStop();
        getController().toolSimulate.setDisable(false);
        getController().menuSimulate.setDisable(false);
        getController().toolStepInto.setDisable(true);
        getController().menuStepInto.setDisable(true);
        getController().toolStepOver.setDisable(true);
        getController().menuStepOver.setDisable(true);
        getController().toolContinue.setDisable(true);
        getController().menuContinue.setDisable(true);
        getController().toolPause.setDisable(true);
        getController().menuPause.setDisable(true);
        getController().toolStop.setDisable(true);
        getController().menuStop.setDisable(true);
        getController().toolRestart.setDisable(true);
        getController().menuRestart.setDisable(true);
        getController().memoryDetailsAddressField.setDisable(true);
        getController().memoryOverviewAddressField.setDisable(true);
        getController().settingsRegex.setDisable(false);
        getController().settingsLegacy.setDisable(false);
        getController().settingsStackAddress.setDisable(false);
        getController().settingsSymbolsAddress.setDisable(false);
        getController().settingsSimInterval.setDisable(false);

        getController().registersPane.setDisable(true);
        getController().memoryDetailsPane.setDisable(true);
        getController().memoryOverviewPane.setDisable(true);
        getController().labelsPane.setDisable(true);
        getController().symbolsPane.setDisable(true);
        getController().stackPane.setDisable(true);
        getController().findAndReplace.setDisable(true);

        application.status.set(Status.EDITING);
        getExecutionWorker().updateGUI();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onRestart() {
        if (getController().menuRestart.isDisable()) return;
        getEditorController().clearNotifs();
        getEditorController().clearAllLineMarkings();

        getCodeInterpreter().restart();

        getEditorController().markForward(getCodeInterpreter().getCurrentLine());
        getExecutionWorker().restart();
    }
}
