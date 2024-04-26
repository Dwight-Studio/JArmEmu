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

package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

import java.util.logging.Logger;

public class SimulationMenuController {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSimulate() {
        if (JArmEmuApplication.getController().menuSimulate.isDisable()) return;

        JArmEmuApplication.getController().toolSimulate.setDisable(true);
        JArmEmuApplication.getController().menuSimulate.setDisable(true);
        JArmEmuApplication.getEditorController().clearNotifications();

        JArmEmuApplication.getEditorController().addNotification(
                JArmEmuApplication.formatMessage("%notification.parsing.title"),
                JArmEmuApplication.formatMessage("%notification.parsing.message"),
                Styles.ACCENT
        );

        JArmEmuApplication.getExecutionWorker().revive();
        JArmEmuApplication.getExecutionWorker().prepare();
    }

    /**
     * Méthode de rappel si la préparation de la simulation s'est effectué avec succès
     * @param errors les erreurs rencontrées lors de l'analyse du code
     */
    public void launchSimulation(ASMException[] errors) {
        JArmEmuApplication.getEditorController().clearNotifications();

        if (errors == null || errors.length == 0) {
            if (JArmEmuApplication.getCodeInterpreter().getInstructionCount() == 0) {
                JArmEmuApplication.getController().toolSimulate.setDisable(false);
                JArmEmuApplication.getController().menuSimulate.setDisable(false);
                JArmEmuApplication.getEditorController().addNotification(
                        JArmEmuApplication.formatMessage("%notification.noInstruction.title"),
                        JArmEmuApplication.formatMessage("%notification.noInstruction.message"),
                        Styles.DANGER
                );
            } else {
                JArmEmuApplication.getEditorController().clearAllLineMarkings();
                JArmEmuApplication.getEditorController().markForward(JArmEmuApplication.getCodeInterpreter().getCurrentLine());
                JArmEmuApplication.getController().memoryDetailsAddressField.setDisable(false);
                JArmEmuApplication.getController().memoryOverviewAddressField.setDisable(false);
                JArmEmuApplication.getEditorController().onLaunch();
                JArmEmuApplication.getController().toolStepInto.setDisable(false);
                JArmEmuApplication.getController().menuStepInto.setDisable(false);
                JArmEmuApplication.getController().toolStepOver.setDisable(false);
                JArmEmuApplication.getController().menuStepOver.setDisable(false);
                JArmEmuApplication.getController().toolContinue.setDisable(false);
                JArmEmuApplication.getController().menuContinue.setDisable(false);
                JArmEmuApplication.getController().toolPause.setDisable(true);
                JArmEmuApplication.getController().menuPause.setDisable(true);
                JArmEmuApplication.getController().toolStop.setDisable(false);
                JArmEmuApplication.getController().menuStop.setDisable(false);
                JArmEmuApplication.getController().toolRestart.setDisable(false);
                JArmEmuApplication.getController().menuRestart.setDisable(false);
                JArmEmuApplication.getController().settingsRegex.setDisable(true);
                JArmEmuApplication.getController().settingsLegacy.setDisable(true);
                JArmEmuApplication.getController().settingsStackAddress.setDisable(true);
                JArmEmuApplication.getController().settingsSymbolsAddress.setDisable(true);
                JArmEmuApplication.getController().settingsSimInterval.setDisable(true);

                JArmEmuApplication.getController().stackPane.setDisable(false);
                JArmEmuApplication.getController().registersPane.setDisable(false);
                JArmEmuApplication.getController().memoryDetailsPane.setDisable(false);
                JArmEmuApplication.getController().memoryOverviewPane.setDisable(false);
                JArmEmuApplication.getController().labelsPane.setDisable(false);
                JArmEmuApplication.getController().symbolsPane.setDisable(false);
                JArmEmuApplication.getEditorController().getFileEditors().forEach(FileEditor::closeFindAndReplace);
                JArmEmuApplication.getController().findAndReplace.setDisable(true);

                JArmEmuApplication.getExecutionWorker().restart();

                JArmEmuApplication.getInstance().status.set(Status.SIMULATING);
            }
        } else {
            JArmEmuApplication.getController().toolSimulate.setDisable(false);
            JArmEmuApplication.getController().menuSimulate.setDisable(false);
            for (ASMException error : errors) {
                JArmEmuApplication.getEditorController().addError(error);
            }
        }
    }

    /**
     * Méthode de rappel si la préparation de la simulation a échoué
     */
    public void abortSimulation() {
        JArmEmuApplication.getEditorController().clearNotifications();
        JArmEmuApplication.getEditorController().addNotification(
                JArmEmuApplication.formatMessage("%notification.parsingError.title"),
                JArmEmuApplication.formatMessage("%notification.parsingError.message"),
                Styles.DANGER
        );
        JArmEmuApplication.getController().toolSimulate.setDisable(false);
        JArmEmuApplication.getController().menuSimulate.setDisable(false);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepInto() {
        if (JArmEmuApplication.getController().menuStepInto.isDisable()) return;
        JArmEmuApplication.getEditorController().clearNotifications();
        JArmEmuApplication.getExecutionWorker().stepInto();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepOver() {
        if (JArmEmuApplication.getController().menuStepOver.isDisable()) return;
        JArmEmuApplication.getEditorController().clearNotifications();
        JArmEmuApplication.getExecutionWorker().stepOver();
        JArmEmuApplication.getController().toolStepInto.setDisable(true);
        JArmEmuApplication.getController().menuStepInto.setDisable(true);
        JArmEmuApplication.getController().toolStepOver.setDisable(true);
        JArmEmuApplication.getController().menuStepOver.setDisable(true);
        JArmEmuApplication.getController().toolContinue.setDisable(true);
        JArmEmuApplication.getController().menuContinue.setDisable(true);
        JArmEmuApplication.getController().toolPause.setDisable(false);
        JArmEmuApplication.getController().menuPause.setDisable(false);
        JArmEmuApplication.getController().toolRestart.setDisable(true);
        JArmEmuApplication.getController().menuRestart.setDisable(true);
        JArmEmuApplication.getController().memoryDetailsAddressField.setDisable(true);
        JArmEmuApplication.getController().memoryOverviewAddressField.setDisable(true);
        JArmEmuApplication.getEditorController().onContinueOrStepOver();}

    /**
     * Méthode invoquée par JavaFX
     */
    public void onContinue() {
        if (JArmEmuApplication.getController().menuContinue.isDisable()) return;
        JArmEmuApplication.getEditorController().clearNotifications();
        JArmEmuApplication.getExecutionWorker().conti();
        JArmEmuApplication.getController().toolStepInto.setDisable(true);
        JArmEmuApplication.getController().menuStepInto.setDisable(true);
        JArmEmuApplication.getController().toolStepOver.setDisable(true);
        JArmEmuApplication.getController().menuStepOver.setDisable(true);
        JArmEmuApplication.getController().toolContinue.setDisable(true);
        JArmEmuApplication.getController().menuContinue.setDisable(true);
        JArmEmuApplication.getController().toolPause.setDisable(false);
        JArmEmuApplication.getController().menuPause.setDisable(false);
        JArmEmuApplication.getController().toolRestart.setDisable(true);
        JArmEmuApplication.getController().menuRestart.setDisable(true);
        JArmEmuApplication.getController().memoryDetailsAddressField.setDisable(true);
        JArmEmuApplication.getController().memoryOverviewAddressField.setDisable(true);
        JArmEmuApplication.getEditorController().onContinueOrStepOver();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onPause() {
        if (JArmEmuApplication.getController().menuPause.isDisable()) return;
        JArmEmuApplication.getExecutionWorker().pause();
        JArmEmuApplication.getController().toolStepInto.setDisable(false);
        JArmEmuApplication.getController().menuStepInto.setDisable(false);
        JArmEmuApplication.getController().toolStepOver.setDisable(false);
        JArmEmuApplication.getController().menuStepOver.setDisable(false);
        JArmEmuApplication.getController().toolContinue.setDisable(false);
        JArmEmuApplication.getController().menuContinue.setDisable(false);
        JArmEmuApplication.getController().toolPause.setDisable(true);
        JArmEmuApplication.getController().menuPause.setDisable(true);
        JArmEmuApplication.getController().toolRestart.setDisable(false);
        JArmEmuApplication.getController().menuRestart.setDisable(false);
        JArmEmuApplication.getController().memoryDetailsAddressField.setDisable(false);
        JArmEmuApplication.getController().memoryOverviewAddressField.setDisable(false);
        JArmEmuApplication.getEditorController().onPause();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStop() {
        if (JArmEmuApplication.getController().menuStop.isDisable()) return;
        JArmEmuApplication.getEditorController().clearNotifications();
        JArmEmuApplication.getExecutionWorker().pause();
        JArmEmuApplication.getEditorController().onStop();
        JArmEmuApplication.getController().toolSimulate.setDisable(false);
        JArmEmuApplication.getController().menuSimulate.setDisable(false);
        JArmEmuApplication.getController().toolStepInto.setDisable(true);
        JArmEmuApplication.getController().menuStepInto.setDisable(true);
        JArmEmuApplication.getController().toolStepOver.setDisable(true);
        JArmEmuApplication.getController().menuStepOver.setDisable(true);
        JArmEmuApplication.getController().toolContinue.setDisable(true);
        JArmEmuApplication.getController().menuContinue.setDisable(true);
        JArmEmuApplication.getController().toolPause.setDisable(true);
        JArmEmuApplication.getController().menuPause.setDisable(true);
        JArmEmuApplication.getController().toolStop.setDisable(true);
        JArmEmuApplication.getController().menuStop.setDisable(true);
        JArmEmuApplication.getController().toolRestart.setDisable(true);
        JArmEmuApplication.getController().menuRestart.setDisable(true);
        JArmEmuApplication.getController().memoryDetailsAddressField.setDisable(true);
        JArmEmuApplication.getController().memoryOverviewAddressField.setDisable(true);
        JArmEmuApplication.getController().settingsRegex.setDisable(false);
        JArmEmuApplication.getController().settingsLegacy.setDisable(false);
        JArmEmuApplication.getController().settingsStackAddress.setDisable(false);
        JArmEmuApplication.getController().settingsSymbolsAddress.setDisable(false);
        JArmEmuApplication.getController().settingsSimInterval.setDisable(false);

        JArmEmuApplication.getController().registersPane.setDisable(true);
        JArmEmuApplication.getController().memoryDetailsPane.setDisable(true);
        JArmEmuApplication.getController().memoryOverviewPane.setDisable(true);
        JArmEmuApplication.getController().labelsPane.setDisable(true);
        JArmEmuApplication.getController().symbolsPane.setDisable(true);
        JArmEmuApplication.getController().stackPane.setDisable(true);
        JArmEmuApplication.getController().findAndReplace.setDisable(true);

        JArmEmuApplication.getInstance().status.set(Status.EDITING);
        JArmEmuApplication.getExecutionWorker().updateGUI();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onRestart() {
        if (JArmEmuApplication.getController().menuRestart.isDisable()) return;
        JArmEmuApplication.getEditorController().clearNotifications();
        JArmEmuApplication.getEditorController().clearAllLineMarkings();

        JArmEmuApplication.getCodeInterpreter().restart();

        JArmEmuApplication.getEditorController().markForward(JArmEmuApplication.getCodeInterpreter().getCurrentLine());
        JArmEmuApplication.getExecutionWorker().restart();
    }
}
