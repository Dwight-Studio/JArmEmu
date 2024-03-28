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
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;

import java.util.logging.Logger;

public class SimulationMenuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public SimulationMenuController(JArmEmuApplication application) {
        super(application);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSimulate() {
        getController().simulate.setDisable(true);
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
                getController().simulate.setDisable(false);
                getEditorController().addNotification(
                        JArmEmuApplication.formatMessage("%notification.noInstruction.title"),
                        JArmEmuApplication.formatMessage("%notification.noInstruction.message"),
                        Styles.DANGER
                );
            } else {
                getEditorController().clearAllLineMarkings();
                getEditorController().markForward(getCodeInterpreter().getCurrentLine());
                getController().memoryDetailsAddressField.setDisable(false);
                getEditorController().onLaunch();
                getController().stepInto.setDisable(false);
                getController().stepOver.setDisable(false);
                getController().conti.setDisable(false);
                getController().pause.setDisable(true);
                getController().stop.setDisable(false);
                getController().restart.setDisable(false);
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

                application.status.set(Status.SIMULATING);
            }
        } else {
            getController().simulate.setDisable(false);
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
        getController().stepInto.setDisable(true);
        getController().stepOver.setDisable(true);
        getController().conti.setDisable(true);
        getController().pause.setDisable(false);
        getController().restart.setDisable(true);
        getController().memoryDetailsAddressField.setDisable(true);
        getEditorController().onContinueOrStepOver();}

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
        getController().memoryDetailsAddressField.setDisable(true);
        getEditorController().onContinueOrStepOver();
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
        getController().memoryDetailsAddressField.setDisable(false);
        getEditorController().onPause();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStop() {
        getEditorController().clearNotifs();
        getExecutionWorker().pause();
        getEditorController().onStop();
        getController().simulate.setDisable(false);
        getController().stepInto.setDisable(true);
        getController().stepOver.setDisable(true);
        getController().conti.setDisable(true);
        getController().pause.setDisable(true);
        getController().stop.setDisable(true);
        getController().restart.setDisable(true);
        getController().memoryDetailsAddressField.setDisable(true);
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

        application.status.set(Status.EDITING);
        getExecutionWorker().updateGUI();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onRestart() {
        getEditorController().clearNotifs();
        getEditorController().clearAllLineMarkings();

        getCodeInterpreter().restart();

        getEditorController().markForward(getCodeInterpreter().getCurrentLine());
        getExecutionWorker().restart();
    }
}
