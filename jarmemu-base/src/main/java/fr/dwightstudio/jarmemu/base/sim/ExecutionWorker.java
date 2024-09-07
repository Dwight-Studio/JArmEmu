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

package fr.dwightstudio.jarmemu.base.sim;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.exception.*;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.view.UpdatableWrapper;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ExecutionWorker {
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

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private static final Object LOCK = new Object();
    private ExecutionThead daemon;

    public ExecutionWorker() {
        this.daemon = new ExecutionThead();
    }

    /**
     * Execute une instruction
     */
    public void stepInto() {
        checkTask(STEP_INTO);
        this.daemon.nextTask.set(STEP_INTO);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    /**
     * Execute les instructions jusqu'au prochain saut
     */
    public void stepOver() {
        checkTask(STEP_OVER);
        this.daemon.nextTask.set(STEP_OVER);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    /**
     * Execute les instructions jusqu'à la fin du programme
     */
    public void conti() {
        checkTask(CONTINUE);
        this.daemon.nextTask.set(CONTINUE);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    /**
     * Met à jour le GUI
     */
    public void updateGUI() {
        checkTask(UPDATE_GUI);
        this.daemon.nextTask.set(UPDATE_GUI);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    /**
     * Parse le code et prépare l'exécution
     */
    public void prepare() {
        checkTask(PREPARE);
        this.daemon.nextTask.set(PREPARE);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    /**
     * Réinitialise les indicateurs du GUI pour le redémarrage
     */
    public void restart() {
        checkTask(RESTART);
        this.daemon.nextTask.set(RESTART);
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    /**
     * Met à jour le GUI
     */
    public void updateFormat() {
        checkTask(UPDATE_FORMAT);
        this.daemon.nextTask.set(UPDATE_FORMAT);
        synchronized (LOCK) {
            LOCK.notifyAll();
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
            this.daemon = new ExecutionThead();
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

    private static class ExecutionThead extends Thread {
        private final Logger logger = Logger.getLogger(getClass().getSimpleName());
        
        private final AtomicInteger nextTask = new AtomicInteger();
        private boolean doContinue = false;
        private boolean doRun = true;
        private int waitingPeriod;
        private FilePos line;
        private FilePos next;
        private long updateGUITimestamp;

        public ExecutionThead() {
            super("CodeExecutorDeamon");

            logger.info("Initiating Execution Thread");
            updateGUITimestamp = System.currentTimeMillis();

            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            try {
                while (doRun) {
                    try {
                        synchronized (ExecutionWorker.LOCK) {
                            if (nextTask.get() == IDLE) ExecutionWorker.LOCK.wait();
                        }
                    } catch (InterruptedException exception) {
                        doRun = false;
                        break;
                    }

                    waitingPeriod = JArmEmuApplication.getSettingsController().getSimulationInterval();

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
            line = JArmEmuApplication.getCodeInterpreter().getCurrentLine();
            ExecutionASMException executionException = null;

            try {
                JArmEmuApplication.getCodeInterpreter().executeCurrentLine(forceExecution);
            } catch (MemoryAccessMisalignedASMException exception) {
                if (JArmEmuApplication.getSettingsController().getAutoBreak() && JArmEmuApplication.getSettingsController().getMemoryAlignBreak()) {
                    Platform.runLater(() -> {
                        JArmEmuApplication.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.memoryAccessMessage"),
                                Styles.DANGER
                        );
                        JArmEmuApplication.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                Styles.ACCENT
                        );
                        JArmEmuApplication.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                } else {
                    step(true);
                }
            } catch (IllegalDataWritingASMException exception) {
                if (JArmEmuApplication.getSettingsController().getAutoBreak() && JArmEmuApplication.getSettingsController().getReadOnlyWritingBreak()) {
                    Platform.runLater(() -> {
                        JArmEmuApplication.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.readOnlyMessage"),
                                Styles.DANGER
                        );
                        JArmEmuApplication.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                Styles.ACCENT
                        );
                        JArmEmuApplication.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                } else {
                    step(true);
                }
            } catch (BreakpointASMException exception) {
                if (JArmEmuApplication.getSettingsController().getCodeBreak()) {
                    Platform.runLater(() -> {
                        JArmEmuApplication.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.codeBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.codeBreakpoint.message", exception.getValue()),
                                Styles.ACCENT
                        );
                        JArmEmuApplication.getSimulationMenuController().onPause();
                    });
                    step(true);
                } else {
                    Platform.runLater(() -> JArmEmuApplication.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.ignoredCodeBreakpoint.title"),
                            JArmEmuApplication.formatMessage("%notification.ignoredCodeBreakpoint.message", exception.getValue()),
                            Styles.WARNING
                    ));
                    step(true);
                }
            } catch (ExecutionASMException exception) {
                executionException = exception;
            }

            next = JArmEmuApplication.getCodeInterpreter().getCurrentLine();
            if (doContinue && !forceExecution && JArmEmuApplication.getEditorController().hasBreakPoint(next)) {
                if (JArmEmuApplication.getSettingsController().getManualBreak()) {
                    Platform.runLater(() -> {
                        JArmEmuApplication.getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.manualBreakpoint.title"),
                                JArmEmuApplication.formatMessage("%notification.manualBreakpoint.message"),
                                Styles.ACCENT
                        );
                        JArmEmuApplication.getSimulationMenuController().onPause();
                    });
                    doContinue = false;
                } else {
                    Platform.runLater(() -> JArmEmuApplication.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.ignoredManualBreakpoint.title"),
                            JArmEmuApplication.formatMessage("%notification.ignoredManualBreakpoint.message"),
                            Styles.WARNING
                    ));
                }
            }

            if (!JArmEmuApplication.getCodeInterpreter().hasNext()) {
                Platform.runLater(() -> {
                    JArmEmuApplication.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.eof.title"),
                            JArmEmuApplication.formatMessage("%notification.eof.message"),
                            Styles.SUCCESS
                    );
                    JArmEmuApplication.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (executionException instanceof StuckExecutionASMException) {
                Platform.runLater(() -> {
                    JArmEmuApplication.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.catchPoint.title"),
                            JArmEmuApplication.formatMessage("%notification.catchPoint.message"),
                            Styles.SUCCESS
                    );
                    JArmEmuApplication.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (executionException instanceof SoftwareInterruptionASMException exception) {
                Platform.runLater(() -> {
                    JArmEmuApplication.getEditorController().addNotification(
                            JArmEmuApplication.formatMessage("%notification.softwareInterrupt.title"),
                            JArmEmuApplication.formatMessage("%notification.softwareInterrupt.message", + exception.getCode()),
                            Styles.ACCENT
                    );
                    JArmEmuApplication.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            if (JArmEmuApplication.getSettingsController().getAutoBreak()) {
                if (JArmEmuApplication.getSettingsController().getProgramAlignBreak()) {
                    if (JArmEmuApplication.getCodeInterpreter().stateContainer.getPC().getData() % 4 != 0) {
                        Platform.runLater(() -> {
                            JArmEmuApplication.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.programCounterMessage"),
                                    Styles.DANGER
                            );
                            JArmEmuApplication.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                    Styles.ACCENT
                            );
                            JArmEmuApplication.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }

                if (JArmEmuApplication.getSettingsController().getStackAlignBreak()) {
                    if (JArmEmuApplication.getCodeInterpreter().stateContainer.getSP().getData() % 4 != 0) {
                        Platform.runLater(() -> {
                            JArmEmuApplication.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.stackPointerMessage"),
                                    Styles.DANGER
                            );
                            JArmEmuApplication.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                    Styles.ACCENT
                            );
                            JArmEmuApplication.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }

                if (JArmEmuApplication.getSettingsController().getFunctionNestingBreak()) {
                    if (JArmEmuApplication.getCodeInterpreter().stateContainer.getNestingCount() > StateContainer.MAX_NESTING_COUNT) {
                        Platform.runLater(() -> {
                            JArmEmuApplication.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.simulatorBreakpoint.nestingMessage"),
                                    Styles.DANGER
                            );
                            JArmEmuApplication.getEditorController().addNotification(
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.title"),
                                    JArmEmuApplication.formatMessage("%notification.autoBreakpoint.message"),
                                    Styles.ACCENT
                            );
                            JArmEmuApplication.getSimulationMenuController().onPause();
                        });
                        doContinue = false;
                    }
                }
            }
        }

        private void stepIntoTask() {
            nextTask.set(IDLE);

            UpdatableWrapper.resetUpdatables();
            step(false);
            updateGUI(true);

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

            int nesting = JArmEmuApplication.getCodeInterpreter().getNestingCount();

            while (doContinue) {
                if (shouldUpdateGUI()) UpdatableWrapper.resetUpdatables();
                step(false);
                if (shouldUpdateGUI()) updateGUI(true);

                doContinue = doContinue && JArmEmuApplication.getCodeInterpreter().getNestingCount() > nesting;

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

            if (isIntervalTooShort()) {
                UpdatableWrapper.resetUpdatables();
                updateGUI(true);
            }

            Platform.runLater(() -> JArmEmuApplication.getSimulationMenuController().onPause());
            logger.info("Done!");
        }

        private void continueTask() {
            nextTask.set(IDLE);
            doContinue = true;
            while (doContinue) {
                long m = System.currentTimeMillis();
                if (shouldUpdateGUI()) UpdatableWrapper.resetUpdatables();
                step(false);
                if (shouldUpdateGUI()) updateGUI(true);

                try {
                    synchronized (this) {
                        long p = waitingPeriod - (System.currentTimeMillis() - m);
                        if (p > 0) wait(p);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }

            if (isIntervalTooShort()) {
                UpdatableWrapper.resetUpdatables();
                updateGUI(true);
            }
            logger.info("Done!");
        }

        private void updateGUITask() {
            nextTask.set(IDLE);

            updateGUI(false);

            JArmEmuApplication.getMemoryDetailsController().updatePage(JArmEmuApplication.getCodeInterpreter().stateContainer);
            JArmEmuApplication.getMemoryOverviewController().updatePage(JArmEmuApplication.getCodeInterpreter().stateContainer);

            logger.info("Done!");
        }

        private void updateGUI(boolean updateLine) {
            if (JArmEmuApplication.getCodeInterpreter() != null) {

                JArmEmuApplication.getStackController().updateGUI(JArmEmuApplication.getCodeInterpreter().stateContainer);

                if (updateLine) {
                    if (JArmEmuApplication.getStatus() != Status.SIMULATING) {
                        JArmEmuApplication.getEditorController().clearAllLineMarkings();
                    } else if (line != null) {
                        Platform.runLater(() -> {
                            if (isIntervalTooShort()) {
                                JArmEmuApplication.getEditorController().clearAllLineMarkings();
                            }
                            JArmEmuApplication.getEditorController().markForward(next == null ? line : next);
                        });
                    }
                }
            }

            updateGUITimestamp = System.currentTimeMillis();
        }

        private void prepareTask() {
            nextTask.set(IDLE);

            synchronized (ExecutionWorker.LOCK) {
                try {
                    ExecutionWorker.LOCK.wait(50);
                } catch (InterruptedException ignored) {
                }
            }

            try {
                ASMException[] errors1 = JArmEmuApplication.getCodeInterpreter().load(
                        JArmEmuApplication.getSourceParser(),
                        JArmEmuApplication.getEditorController().getSources()
                );

                if (errors1.length == 0) {
                    ASMException[] errors2 = JArmEmuApplication.getCodeInterpreter().initiate(
                            JArmEmuApplication.getSettingsController().getStackAddress(),
                            JArmEmuApplication.getSettingsController().getProgramAddress()
                    );

                    if (errors2.length == 0) {
                        line = next = null;
                        JArmEmuApplication.getCodeInterpreter().restart();
                        attachControllers();
                        JArmEmuApplication.getEditorController().prepareSimulation();
                        updateGUI(true);
                        Platform.runLater(() -> JArmEmuApplication.getSimulationMenuController().launchSimulation(null));
                    } else {
                        Platform.runLater(() -> JArmEmuApplication.getSimulationMenuController().launchSimulation(errors2));
                    }
                } else {
                    Platform.runLater(() -> JArmEmuApplication.getSimulationMenuController().launchSimulation(errors1));
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    new ExceptionDialog(e).show();
                    logger.severe(ExceptionUtils.getStackTrace(e));
                    JArmEmuApplication.getSimulationMenuController().abortSimulation();
                });
            }

            logger.info("Done!");
        }

        private void restartTask() {
            nextTask.set(IDLE);

            line = next = null;
            updateGUI(true);

            attachControllers();

            logger.info("Done!");
        }

        private void updateFormatTask() {
            nextTask.set(IDLE);

            JArmEmuApplication.getRegistersController().refresh();
            JArmEmuApplication.getStackController().refresh();
            JArmEmuApplication.getMemoryDetailsController().refresh();
            JArmEmuApplication.getMemoryOverviewController().refresh();
            JArmEmuApplication.getSymbolsController().refresh();
            JArmEmuApplication.getLabelsController().refresh();
        }

        private boolean isIntervalTooShort() {
            return waitingPeriod < UPDATE_THRESHOLD;
        }

        private boolean shouldUpdateGUI() {
            return !isIntervalTooShort() || (System.currentTimeMillis() - updateGUITimestamp) > FALLBACK_UPDATE_INTERVAL;
        }

        private void attachControllers() {
            JArmEmuApplication.getRegistersController().attach(JArmEmuApplication.getCodeInterpreter().stateContainer);
            JArmEmuApplication.getMemoryDetailsController().attach(JArmEmuApplication.getCodeInterpreter().stateContainer);
            JArmEmuApplication.getMemoryOverviewController().attach(JArmEmuApplication.getCodeInterpreter().stateContainer);
            JArmEmuApplication.getSymbolsController().attach(JArmEmuApplication.getCodeInterpreter().stateContainer);
            JArmEmuApplication.getLabelsController().attach(JArmEmuApplication.getCodeInterpreter().stateContainer);
        }
    }
}
