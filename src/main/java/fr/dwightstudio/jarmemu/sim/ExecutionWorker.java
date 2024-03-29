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

package fr.dwightstudio.jarmemu.sim;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.asm.exception.*;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ExecutionWorker extends AbstractJArmEmuModule {
    public static final int UPDATE_THRESHOLD = 10;
    public static final int FALLBACK_UPDATE_INTERVAL = 30;

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
            logger.info("Reviving Execution Thread");
            this.daemon = new ExecutionThead(application);
        }
    }

    /**
     * Tue le Thread d'exécution
     */
    public void stop() {
        logger.info("Killing Execution Thread");
        if (this.daemon.isAlive()) {
            this.daemon.doContinue = this.daemon.doRun = false;
            if (!this.daemon.isInterrupted()) daemon.interrupt();
        }

        this.daemon.doContinue = this.daemon.doRun = false;

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

    public void addStepListener(StepListener stepListener) {
        this.daemon.stepListeners.add(stepListener);
    }

    public void removeStepListener(StepListener stepListener) {
        this.daemon.stepListeners.remove(stepListener);
    }

    private static class ExecutionThead extends Thread {
        private final Logger logger = Logger.getLogger(getClass().getName());

        private final JArmEmuApplication application;
        private final AtomicInteger nextTask = new AtomicInteger();
        private boolean doContinue = false;
        private boolean doRun = true;
        private int waitingPeriod;
        private FilePos line;
        private FilePos next;
        private long updateGUITimestamp;
        private ArrayList<StepListener> stepListeners;

        public ExecutionThead(JArmEmuApplication application) {
            super("CodeExecutorDeamon");

            logger.info("Initiating Execution Thread");
            this.application = application;
            updateGUITimestamp = System.currentTimeMillis();

            stepListeners = new ArrayList<>();

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

        private void step(boolean forceExecution) {
            line = application.getCodeInterpreter().getCurrentLine();
            Platform.runLater(() -> stepListeners.forEach(stepListener -> stepListener.step(line)));

            ExecutionASMException executionException = null;

            try {
                application.getCodeInterpreter().executeCurrentLine(forceExecution);
            } catch (MemoryAccessMisalignedASMException exception) {
                if (application.getSettingsController().getAutoBreak() && application.getSettingsController().getMemoryAlignBreak()) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.memoryAccessMessage"),
                                Styles.DANGER
                        );
                        application.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                Styles.ACCENT
                        );
                        application.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                } else {
                    step(true);
                }
            } catch (IllegalDataWritingASMException exception) {
                if (application.getSettingsController().getAutoBreak() && application.getSettingsController().getReadOnlyWritingBreak()) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.readOnlyMessage"),
                                Styles.DANGER
                        );
                        application.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                Styles.ACCENT
                        );
                        application.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                } else {
                    step(true);
                }
            } catch (BreakpointASMException exception) {
                if (application.getSettingsController().getCodeBreak()) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.codeBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.codeBreakpoint.message", exception.getValue()),
                                Styles.ACCENT
                        );
                        application.getSimulationMenuController().onPause();
                    });
                    step(true);
                } else {
                    Platform.runLater(() -> application.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.ignoredCodeBreakpoint.title"),
                            JArmEmuApplication.formatMessage("%notification.ignoredCodeBreakpoint.message", exception.getValue()),
                            Styles.WARNING
                    ));
                    step(true);
                }
            } catch (ExecutionASMException exception) {
                executionException = exception;
            }

            next = application.getCodeInterpreter().getCurrentLine();
            if (doContinue && !forceExecution && application.getEditorController().hasBreakPoint(next)) {
                if (application.getSettingsController().getManualBreak()) {
                    Platform.runLater(() -> {
                        application.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.manualBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.manualBreakpoint.message"),
                                Styles.ACCENT
                        );
                        application.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                } else {
                    Platform.runLater(() -> application.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.ignoredManualBreakpoint.title"),
                            JArmEmuApplication.formatMessage("%notification.ignoredManualBreakpoint.message"),
                            Styles.WARNING
                    ));
                }
            }

            if (!application.getCodeInterpreter().hasNext()) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.eof.title"),
                            JArmEmuApplication.formatMessage("%notification.eof.message"),
                            Styles.SUCCESS
                    );
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (executionException instanceof StuckExecutionASMException) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.catchPoint.title"),
                            JArmEmuApplication.formatMessage("%notification.catchPoint.message"),
                            Styles.SUCCESS
                    );
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (executionException instanceof SoftwareInterruptionASMException exception) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.softwareInterrupt.title"),
                            JArmEmuApplication.formatMessage("%notification.softwareInterrupt.message", + exception.getCode()),
                            Styles.ACCENT
                    );
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (application.getSettingsController().getAutoBreak()) {
                if (application.getSettingsController().getProgramAlignBreak()) {
                    if (application.getCodeInterpreter().stateContainer.getPC().getData() % 4 != 0) {
                        Platform.runLater(() -> {
                            application.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.programCounterMessage"),
                                    Styles.DANGER
                            );
                            application.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                    Styles.ACCENT
                            );
                            application.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }

                if (application.getSettingsController().getStackAlignBreak()) {
                    if (application.getCodeInterpreter().stateContainer.getSP().getData() % 4 != 0) {
                        Platform.runLater(() -> {
                            application.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.stackPointerMessage"),
                                    Styles.DANGER
                            );
                            application.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                    Styles.ACCENT
                            );
                            application.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }

                if (application.getSettingsController().getFunctionNestingBreak()) {
                    if (application.getCodeInterpreter().stateContainer.getNestingCount() > StateContainer.MAX_NESTING_COUNT) {
                        Platform.runLater(() -> {
                            application.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.nestingMessage"),
                                    Styles.DANGER
                            );
                            application.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                    Styles.ACCENT
                            );
                            application.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }
            }
        }

        private void stepIntoTask() {
            nextTask.set(IDLE);

            step(false);
            updateGUI();

            try {
                synchronized (this) {
                    if (waitingPeriod != 0) wait(waitingPeriod);
                }
            } catch (InterruptedException ignored) {
            }

            logger.info("Done!");
        }

        private void stepOverTask() {
            nextTask.set(IDLE);
            doContinue = true;

            int nesting = application.getCodeInterpreter().getNestingCount();

            while (doContinue) {
                step(false);
                if (shouldUpdateGUI()) updateGUI();

                doContinue = doContinue && application.getCodeInterpreter().getNestingCount() > nesting;

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

            Platform.runLater(() -> application.getSimulationMenuController().onPause());
            logger.info("Done!");
        }

        private void continueTask() {
            nextTask.set(IDLE);
            doContinue = true;
            while (doContinue) {
                step(false);
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

            application.getMemoryDetailsController().updatePage(application.getCodeInterpreter().stateContainer);
            application.getMemoryOverviewController().updatePage(application.getCodeInterpreter().stateContainer);

            logger.info("Done!");
        }

        private void updateGUI() {
            if (application.getCodeInterpreter() != null) {

                application.getStackController().updateGUI(application.getCodeInterpreter().stateContainer);

                if (application.status.get() != Status.SIMULATING) {
                    application.getEditorController().clearAllLineMarkings();
                } else if (line != null) {
                    Platform.runLater(() -> {
                        if (isIntervalTooShort()) {
                            application.getEditorController().clearAllLineMarkings();
                        }
                        application.getEditorController().markForward(next == null ? line : next);
                    });
                }
            }

            updateGUITimestamp = System.currentTimeMillis();
        }

        private void prepareTask() {
            nextTask.set(IDLE);

            synchronized (this) {
                try {
                    this.wait(50);
                } catch (InterruptedException ignored) {
                }
            }

            try {
                ASMException[] errors1 = application.getCodeInterpreter().load(
                        application.getSourceParser(),
                        application.getEditorController().getSources()
                );

                if (errors1.length == 0) {
                    ASMException[] errors2 = application.getCodeInterpreter().initiate(
                            application.getSettingsController().getStackAddress(),
                            application.getSettingsController().getSymbolsAddress()
                    );

                    if (errors2.length == 0) {
                        line = next = null;
                        application.getCodeInterpreter().restart();
                        attachControllers();
                        application.getEditorController().prepareSimulation();
                        updateGUI();
                        Platform.runLater(() -> application.getSimulationMenuController().launchSimulation(null));
                    } else {
                        Platform.runLater(() -> application.getSimulationMenuController().launchSimulation(errors2));
                    }
                } else {
                    Platform.runLater(() -> application.getSimulationMenuController().launchSimulation(errors1));
                }
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

            line = next = null;
            updateGUI();

            attachControllers();

            logger.info("Done!");
        }

        private void updateFormatTask() {
            nextTask.set(IDLE);

            application.getRegistersController().refresh();
            application.getStackController().refresh();
            application.getMemoryDetailsController().refresh();
            application.getMemoryOverviewController().refresh();
            application.getSymbolsController().refresh();
            application.getLabelsController().refresh();
        }

        private boolean isIntervalTooShort() {
            return waitingPeriod < UPDATE_THRESHOLD;
        }

        private boolean shouldUpdateGUI() {
            return !isIntervalTooShort() || (System.currentTimeMillis() - updateGUITimestamp) > FALLBACK_UPDATE_INTERVAL;
        }

        private void attachControllers() {
            application.getRegistersController().attach(application.getCodeInterpreter().stateContainer);
            application.getMemoryDetailsController().attach(application.getCodeInterpreter().stateContainer);
            application.getMemoryOverviewController().attach(application.getCodeInterpreter().getStateContainer());
            application.getSymbolsController().attach(application.getCodeInterpreter().stateContainer);
            application.getLabelsController().attach(application.getCodeInterpreter().stateContainer);
        }
    }
}
