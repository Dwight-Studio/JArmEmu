package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import javafx.application.Platform;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionWorker {

    private static final int WAITING_PERIOD = 50;
    private static final int IDLE = 0;
    private static final int STEP_INTO = 1;
    private static final int STEP_OVER = 2;
    private static final int CONTINUE = 3;

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
        this.daemon.currentTask.set(STEP_INTO);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Execute les instructions jusqu'au prochain saut
     */
    public void stepOver() {
        this.daemon.currentTask.set(STEP_OVER);
        synchronized (this.daemon) {
            this.daemon.notifyAll();
        }
    }

    /**
     * Execute les instructions jusqu'au prochain saut
     */
    public void conti() {
        this.daemon.currentTask.set(CONTINUE);
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

    private static class ExecutionThead extends Thread {

        private final JArmEmuApplication application;
        private final AtomicInteger currentTask = new AtomicInteger();
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
                            this.wait();
                        }
                    } catch (InterruptedException exception) {
                        doRun = false;
                        break;
                    }

                    switch (currentTask.get()) {
                        case STEP_INTO -> stepInto();

                        case STEP_OVER -> stepOver();

                        case CONTINUE -> conti();

                        case IDLE -> {}
                    }

                }
            } catch (Exception exception) {
                Platform.runLater(() -> new ExceptionDialog(exception).show());
            }
        }

        private void stepInto() {
            int line = application.codeInterpreter.nextLine();

            Platform.runLater(() -> {
                application.controller.editorManager.clearExecutedLines();
                application.controller.editorManager.markLineAsExecuted(line);
                application.controller.clearNotifs();
            });

            if (application.editorManager.hasBreakPoint(line)) {
                Platform.runLater(() -> {
                    application.controller.addNotif("Breakpoint", "The program reached a breakpoint, execution is paused.", "success");
                    application.controller.onPause();
                });
            }

            application.codeInterpreter.executeCurrentLine();

            if (application.codeInterpreter.isAtTheEnd()) {
                Platform.runLater(() -> {
                    application.controller.addNotif("Warning", "The program reached the end of the file.", "warning");
                    application.controller.onPause();
                });
            }

            Platform.runLater(() -> application.controller.updateRegisters(application.codeInterpreter.stateContainer));
            currentTask.set(IDLE);
        }

        private void stepOver() {
            doContinue = true;
            while (doContinue) {
                stepInto();
                try {
                    synchronized (this) {
                        wait(WAITING_PERIOD);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }
            currentTask.set(IDLE);
        }

        private void conti() {
            doContinue = true;
            while (doContinue) {
                stepInto();
                try {
                    synchronized (this) {
                        wait(WAITING_PERIOD);
                    }
                } catch (InterruptedException ignored) {
                    doContinue = false;
                    break;
                }
            }
            currentTask.set(IDLE);
        }
    }
}
