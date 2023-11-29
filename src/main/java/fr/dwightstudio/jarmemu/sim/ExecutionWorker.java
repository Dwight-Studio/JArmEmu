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

package fr.dwightstudio.jarmemu.sim;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.sim.exceptions.*;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ExecutionWorker extends AbstractJArmEmuModule {
    public static final int UPDATE_THRESHOLD = 50;

    private static final int ERROR = -1;
    private static final int IDLE = 0;
    private static final int STEP_INTO = 1;
    private static final int STEP_OVER = 2;
    private static final int CONTINUE = 3;
    private static final int UPDATE_GUI = 4;
    private static final int PREPARE = 5;
    private static final int RESTART = 6;
    private static final int UPDATE_FORMAT = 7;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private ExecutionThead daemon;

    public ExecutionWorker(JArmEmuApplication application) {
        super(application);

        this.daemon = new ExecutionThead(application);
    }

    /**
     * Execute une instruction
     */
    public void stepInto() {
        checkTask(STEP_INTO);
        this.daemon.nextTask.set(STEP_INTO);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Execute les instructions jusqu'au prochain saut
     */
    public void stepOver() {
        checkTask(STEP_OVER);
        this.daemon.nextTask.set(STEP_OVER);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Execute les instructions jusqu'à la fin du programme
     */
    public void conti() {
        checkTask(CONTINUE);
        this.daemon.nextTask.set(CONTINUE);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Met à jour le GUI
     */
    public void updateGUI() {
        checkTask(UPDATE_GUI);
        this.daemon.nextTask.set(UPDATE_GUI);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Parse le code et prépare l'exécution
     */
    public void prepare() {
        checkTask(PREPARE);
        this.daemon.nextTask.set(PREPARE);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Réinitialise les indicateurs du GUI pour le redémarrage
     */
    public void restart() {
        checkTask(RESTART);
        this.daemon.nextTask.set(RESTART);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Met à jour le GUI
     */
    public void updateFormat() {
        checkTask(UPDATE_FORMAT);
        this.daemon.nextTask.set(UPDATE_FORMAT);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Met en pause l'exécution
     */
    public void pause() {
        this.daemon.doContinue = false;
    }

    /**
     * Re-exécute le Thread si nécessaire
     */
    public void revive() {
        if (!this.daemon.isAlive()) {
            this.daemon = new ExecutionThead(application);
        }
    }

    /**
     * Tue le Thread d'exécution
     */
    public void stop() {
        this.daemon.doContinue = this.daemon.doRun = false;
        if (this.daemon.isAlive()) {
            if (!this.daemon.isInterrupted()) daemon.interrupt();
        }
        this.daemon = null;
    }

    public int getTask() {
        if (this.daemon.isAlive()) {
            return daemon.nextTask.get();
        } else {
            return ERROR;
        }
    }

    public void checkTask(int id) {
        if (!this.daemon.isAlive()) logger.warning("Adding task to a dead Worker");
        int task = this.daemon.nextTask.get();
        if (task != IDLE) logger.warning("Overriding next task (ID" + task + " with ID" + id + ")");
    }

    private static class ExecutionThead extends Thread {

        private static final int FALLBACK_UPDATE_INTERVAL = 100;
        private final Logger logger = Logger.getLogger(getClass().getName());

        private final JArmEmuApplication application;
        private final AtomicInteger nextTask = new AtomicInteger();
        private boolean doContinue = false;
        private boolean doRun = true;
        private int waitingPeriod;
        private int last;
        private int line;
        private int next;
        private long updateGUITimestamp;

        public ExecutionThead(JArmEmuApplication application) {
            super("CodeExecutorDeamon");

            this.application = application;
            updateGUITimestamp = System.currentTimeMillis();

            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            try {
                while (doRun) {
                    try {
                        synchronized (this) {
                            if (nextTask.get() == IDLE) this.wait();
                        }
                    } catch (InterruptedException exception) {
                        doRun = false;
                        break;
                    }

                    waitingPeriod = application.getSettingsController().getSimulationInterval();

                    int task = nextTask.get();
                    logger.info("Executing task ID" + task + "...");

                    switch (task) {
                        case STEP_INTO -> stepIntoTask();
                        case STEP_OVER -> stepOverTask();
                        case CONTINUE -> continueTask();
                        case UPDATE_GUI -> updateGUITask();
                        case PREPARE -> prepareTask();
                        case RESTART -> restartTask();
                        case UPDATE_FORMAT -> updateFormatTask();

                        case IDLE -> {
                        }
                        default -> logger.severe("Unknown task: Invalid ID");
                    }

                }
            } catch (Exception exception) {
                Platform.runLater(() -> {
                    new ExceptionDialog(exception).show();
                    logger.severe(ExceptionUtils.getStackTrace(exception));
                });
            }
        }

        private void step() {
            last = line;
            line = application.getCodeInterpreter().getCurrentLine();

            ExecutionASMException executionException = null;

            try {
                application.getCodeInterpreter().executeCurrentLine(false);
            } catch (MemoryAccessMisalignedASMException exception) {
                if (application.getSettingsController().getAutoBreakSetting() && application.getSettingsController().getMemoryAlignBreakSetting()) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotif("Simulator requested a breakpoint", "Misaligned memory access attempt.", Styles.DANGER);
                        application.getEditorController().addNotif("Automatic breakpoint", "You can disable automatic breakpoints in the settings.", Styles.ACCENT);
                        application.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                }
            } catch (IllegalDataWritingASMException exception) {
                if (application.getSettingsController().getAutoBreakSetting() && application.getSettingsController().getReadOnlyWritingBreakSetting()) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotif("Simulator requested a breakpoint", "Read only data overwritten.", Styles.DANGER);
                        application.getEditorController().addNotif("Automatic breakpoint", "You can disable automatic breakpoints in the settings.", Styles.ACCENT);
                        application.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                }
            } catch (ExecutionASMException exception) {
                executionException = exception;
            }

            next = application.getCodeInterpreter().getCurrentLine();

            if (application.getEditorController().currentFileEditor().hasBreakPoint(next)) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif("Breakpoint", "The program reached a breakpoint, execution is paused.", Styles.WARNING);
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (!application.getCodeInterpreter().hasNext()) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif("Finished", "The program reached the end of the file.", Styles.SUCCESS);
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (executionException instanceof StuckExecutionASMException) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif("Finished", "The program reached a catch point.", Styles.SUCCESS);
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (executionException instanceof SoftwareInterruptionASMException exception) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif("Software interrupt", "The program requested an interrupt with code " + exception.getCode() + ".", Styles.WARNING);
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (application.getSettingsController().getAutoBreakSetting()) {
                if (application.getSettingsController().getProgramAlignBreakSetting()) {
                    if (application.getCodeInterpreter().stateContainer.registers[RegisterUtils.PC.getN()].getData() % 4 != 0) {
                        Platform.runLater(() -> {
                            application.getEditorController().addNotif("Simulator requested a breakpoint", "Misaligned Program Counter.", Styles.DANGER);
                            application.getEditorController().addNotif("Automatic breakpoint", "You can disable automatic breakpoints in the settings.", Styles.ACCENT);
                            application.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }

                if (application.getSettingsController().getStackAlignBreakSetting()) {
                    if (application.getCodeInterpreter().stateContainer.registers[RegisterUtils.SP.getN()].getData() % 4 != 0) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotif("Simulator requested a breakpoint", "Misaligned Stack Pointer.", Styles.DANGER);
                        application.getEditorController().addNotif("Automatic breakpoint", "You can disable automatic breakpoints in the settings.", Styles.ACCENT);
                        application.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                    }
                }

                if (application.getSettingsController().getFunctionNestingBreakSetting()) {
                    if (application.getCodeInterpreter().stateContainer.getNestingCount() > StateContainer.MAX_NESTING_COUNT) {
                        Platform.runLater(() -> {
                            application.getEditorController().addNotif("Simulator requested a breakpoint", "Function nesting too deep (>1000 branches).", Styles.DANGER);
                            application.getEditorController().addNotif("Automatic breakpoint", "You can disable automatic breakpoints in the settings.", Styles.ACCENT);
                            application.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }
            }
        }

        private void stepIntoTask() {
            nextTask.set(IDLE);

            step();
            updateGUI();

            try {
                synchronized (this) {
                    if (waitingPeriod != 0) wait(waitingPeriod);
                }
            } catch (InterruptedException ignored) {}

            logger.info("Done!");
        }

        private void stepOverTask() {
            nextTask.set(IDLE);
            doContinue = true;
            while (doContinue) {
                step();
                if (shouldUpdateGUI()) updateGUI();

                doContinue = doContinue && !application.getCodeInterpreter().hasJumped();

                try {
                    synchronized (this) {
                        if (waitingPeriod != 0)
                            wait(waitingPeriod);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }

            if (isIntervalTooShort()) updateGUI();
            logger.info("Done!");
        }

        private void continueTask() {
            nextTask.set(IDLE);
            doContinue = true;
            while (doContinue) {
                step();
                if (shouldUpdateGUI()) updateGUI();

                try {
                    synchronized (this) {
                        if (waitingPeriod != 0) wait(waitingPeriod);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }

            if (isIntervalTooShort()) updateGUI();
            logger.info("Done!");
        }

        private void updateGUITask() {
            nextTask.set(IDLE);

            updateGUI();

            application.getMemoryController().updatePage(application.getCodeInterpreter().stateContainer);

            logger.info("Done!");
        }

        private void updateGUI() {
            if (application.getCodeInterpreter() != null) {

                application.getStackController().updateGUI(application.getCodeInterpreter().stateContainer);

                if (next != 0 && line != next || line != 0 && next != 0) {
                    if (isIntervalTooShort()) Platform.runLater(() -> application.getEditorController().clearLineMarkings());
                    Platform.runLater(() -> {
                        if (last != -1) application.getEditorController().markLine(last, LineStatus.NONE);
                        application.getEditorController().markLine(line, LineStatus.EXECUTED);
                        application.getEditorController().markLine(next, LineStatus.SCHEDULED);
                    });
                }
            }

            updateGUITimestamp = System.currentTimeMillis();
        }

        private void prepareTask() {
            nextTask.set(IDLE);
            application.getSourceParser().setSourceScanner(new SourceScanner(application.getEditorController().currentFileEditor().getCodeArea().getText()));

            synchronized (this) {
                try {
                    this.wait(50);
                } catch (InterruptedException ignored) {}
            }

            try {
                SyntaxASMException[] errors = application.getCodeInterpreter().load(application.getSourceParser(), application.getSettingsController().getStackAddress(), application.getSettingsController().getSymbolsAddress());

                if (errors.length == 0) {
                    line = next = last = 0;
                    application.getCodeInterpreter().restart();
                    application.getRegistersController().attach(application.getCodeInterpreter().stateContainer);
                    application.getMemoryController().attach(application.getCodeInterpreter().stateContainer);
                    application.getEditorController().currentFileEditor().prepareSimulation();
                    updateGUI();
                }

                Platform.runLater(() -> application.getSimulationMenuController().launchSimulation(errors));
            } catch (SyntaxASMException exception) {
                Platform.runLater(() -> Platform.runLater(() -> application.getSimulationMenuController().launchSimulation(new SyntaxASMException[]{exception})));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    new ExceptionDialog(e).show();
                    logger.severe(ExceptionUtils.getStackTrace(e));
                    application.getSimulationMenuController().abortSimulation();
                });
            }

            logger.info("Done!");
        }

        private void restartTask() {
            nextTask.set(IDLE);

            line = next = last = 0;
            updateGUI();

            application.getRegistersController().attach(application.getCodeInterpreter().stateContainer);
            application.getMemoryController().attach(application.getCodeInterpreter().stateContainer);

            logger.info("Done!");
        }

        private void updateFormatTask() {
            nextTask.set(IDLE);

            application.getRegistersController().refresh();
            application.getStackController().refresh();
            application.getMemoryController().refresh();
        }

        private boolean isIntervalTooShort() {
            return waitingPeriod < UPDATE_THRESHOLD;
        }

        private boolean shouldUpdateGUI() {
            return !isIntervalTooShort() || (System.currentTimeMillis() - updateGUITimestamp) > FALLBACK_UPDATE_INTERVAL;
        }
    }
}
