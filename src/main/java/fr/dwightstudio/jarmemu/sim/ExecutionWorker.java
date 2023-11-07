package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.LineStatus;
import fr.dwightstudio.jarmemu.gui.controllers.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
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
            last = application.getCodeInterpreter().getLastExecutedLine();
            line = application.getCodeInterpreter().nextLine();

            if (application.getEditorController().hasBreakPoint(line)) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif("Breakpoint", "The program reached a breakpoint, execution is paused.", "success");
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            application.getCodeInterpreter().executeCurrentLine();

            next = application.getCodeInterpreter().getNextLine();

            if (application.getCodeInterpreter().isAtTheEnd()) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif("Warning", "The program reached the end of the file.", "warning");
                    application.getSimulationMenuController().onPause();
                });
                doContinue = false;
            }

            // TODO: Vérifier si les flags doivent être persistants
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
                        if (application.getSettingsController().getSimulationInterval() != 0)
                            wait(application.getSettingsController().getSimulationInterval());
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

            logger.info("Done!");
        }

        private void updateGUI() {
            if (application.getCodeInterpreter() != null) {
                if (application.getCodeInterpreter().stateContainer == null)
                    logger.warning("Updating GUI on null StateContainer");
                application.getMemoryController().updateGUI(application.getCodeInterpreter().stateContainer);
                application.getRegistersController().updateGUI(application.getCodeInterpreter().stateContainer);
                application.getStackController().updateGUI(application.getCodeInterpreter().stateContainer);

                if (next != 0 && line != next || line != 0 && next != 0) {
                    if (isIntervalTooShort()) application.getEditorController().clearLineMarking();
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
            application.getSourceParser().setSourceScanner(new SourceScanner(application.getEditorController().getText()));

            synchronized (this) {
                try {
                    this.wait(50);
                } catch (InterruptedException ignored) {}
            }

            try {
                application.getCodeInterpreter().load(application.getSourceParser());
            } catch (SyntaxASMException exception) {
                Platform.runLater(() -> {
                    application.getEditorController().addNotif(exception.getTitle(), " " + exception.getMessage(), "danger");
                    logger.log(Level.INFO, ExceptionUtils.getStackTrace(exception));
                    application.getSimulationMenuController().abortSimulation();
                });
                return;
            } catch (Exception e) {
                Platform.runLater(() -> {
                    new ExceptionDialog(e).show();
                    logger.severe(ExceptionUtils.getStackTrace(e));
                    application.getSimulationMenuController().abortSimulation();
                });
                return;
            }

            try {
                line = next = last = 0;
                application.getCodeInterpreter().resetState(application.getSettingsController().getStackAddress(), application.getSettingsController().getSymbolsAddress());
                application.getCodeInterpreter().restart();
                application.getEditorController().prepareSimulation();
                application.getStackController().clear();

                AssemblyError[] errors = application.getCodeInterpreter().verifyAll();
                Platform.runLater(() -> application.getSimulationMenuController().launchSimulation(errors));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    new ExceptionDialog(e).show();
                    logger.severe(ExceptionUtils.getStackTrace(e));
                    application.getSimulationMenuController().abortSimulation();
                });
                return;
            }

            updateGUI();

            logger.info("Done!");
        }

        private boolean isIntervalTooShort() {
            return waitingPeriod < UPDATE_THRESHOLD;
        }

        private boolean shouldUpdateGUI() {
            return !isIntervalTooShort() || (System.currentTimeMillis() - updateGUITimestamp) > 50;
        }
    }
}
