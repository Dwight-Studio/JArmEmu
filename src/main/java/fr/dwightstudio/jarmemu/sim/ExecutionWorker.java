package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.LineStatus;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutionWorker {

    private static int WAITING_PERIOD = 0;
    private static final int UPDATE_THRESHOLD = 50;

    private static final int ERROR = -1;
    private static final int IDLE = 0;
    private static final int STEP_INTO = 1;
    private static final int STEP_OVER = 2;
    private static final int CONTINUE = 3;
    private static final int UPDATE_GUI = 4;
    private static final int PREPARE = 5;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private ExecutionThead daemon = null;
    private final JArmEmuApplication application;

    public ExecutionWorker(JArmEmuApplication application) {
        this.application = application;

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

        public ExecutionThead(JArmEmuApplication application) {
            super("CodeExecutorDeamon");

            this.application = application;

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

                    int task = nextTask.get();
                    logger.info("Executing task ID" + task + "...");

                    switch (task) {
                        case STEP_INTO -> stepIntoTask();
                        case STEP_OVER -> stepOverTask();
                        case CONTINUE -> continueTask();
                        case UPDATE_GUI -> updateGUITask();
                        case PREPARE -> prepareTask();

                        case IDLE -> {}
                        default -> logger.severe("Unknown task: Invalid ID");
                    }

                }
            } catch (Exception exception) {
                Platform.runLater(() -> new ExceptionDialog(exception).show());
            }
        }

        private void step() {
            int last = application.codeInterpreter.getLastExecutedLine();
            int line = application.codeInterpreter.nextLine();

            if (application.editorManager.hasBreakPoint(line)) {
                Platform.runLater(() -> {
                    application.controller.addNotif("Breakpoint", "The program reached a breakpoint, execution is paused.", "success");
                    application.controller.onPause();
                });
                doContinue = false;
            }

            application.codeInterpreter.executeCurrentLine();

            int next = application.codeInterpreter.getNextLine();

            if (application.codeInterpreter.isAtTheEnd()) {
                Platform.runLater(() -> {
                    application.controller.addNotif("Warning", "The program reached the end of the file.", "warning");
                    application.controller.onPause();
                });
                doContinue = false;
            }

            if (shouldUpdateGUI() || !doContinue) {
                if (!shouldUpdateGUI()) application.controller.editorManager.clearLineMarking();
                Platform.runLater(() -> {
                    if (last != -1) application.controller.editorManager.markLine(last, LineStatus.NONE);
                    application.controller.editorManager.markLine(line, LineStatus.EXECUTED);
                    application.controller.editorManager.markLine(next, LineStatus.SCHEDULED);
                });
            }
        }

        private void stepIntoTask() {
            nextTask.set(IDLE);

            step();
            updateGUI();

            logger.info("Done!");
        }

        private void stepOverTask() {
            nextTask.set(IDLE);
            doContinue = true;
            while (doContinue) {
                step();
                if (shouldUpdateGUI()) updateGUI();

                doContinue = doContinue && !application.codeInterpreter.hasJumped();

                try {
                    synchronized (this) {
                        if (WAITING_PERIOD != 0) wait(WAITING_PERIOD);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }

            if (!shouldUpdateGUI()) updateGUI();
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
                        if (WAITING_PERIOD != 0) wait(WAITING_PERIOD);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }

            if (!shouldUpdateGUI()) updateGUI();
            logger.info("Done!");
        }

        private void updateGUITask() {
            nextTask.set(IDLE);

            updateGUI();

            logger.info("Done!");
        }

        private void updateGUI() {
            if (application.codeInterpreter != null) {
                if (application.codeInterpreter.stateContainer == null) logger.warning("Updating GUI on null StateContainer");
                application.controller.updateGUI(application.codeInterpreter.stateContainer);
            }
        }

        private void prepareTask() {
            nextTask.set(IDLE);
            application.sourceParser.setSourceScanner(new SourceScanner(application.editorManager.codeArea.getText()));

            try {
                application.codeInterpreter.load(application.sourceParser);
            } catch (SyntaxASMException exception) {
                Platform.runLater(() -> {
                application.controller.addNotif(exception.getTitle(), " " + exception.getMessage(), "danger");
                logger.log(Level.INFO, ExceptionUtils.getStackTrace(exception));
                });
                return;
            }

            application.codeInterpreter.resetState();
            application.codeInterpreter.restart();

            try {
                AssemblyError[] errors = application.codeInterpreter.verifyAll();
                Platform.runLater(() -> application.controller.launchSimulation(errors));
            } catch (Exception e) {
                Platform.runLater(() -> new ExceptionDialog(e).show());
            }

            logger.info("Done!");
        }

        private boolean shouldUpdateGUI() {
            return WAITING_PERIOD >= UPDATE_THRESHOLD;
        }
    }
}
