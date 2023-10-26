package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import javafx.application.Platform;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.ArrayBlockingQueue;

public class ExecutionWorker {

    private static final int WAITING_PERIOD = 250;

    private Thread deamon = null;
    private JArmEmuApplication application;
    private final ArrayBlockingQueue<Runnable> queue;
    private boolean doContinue = false;
    private boolean doRun = true;

    public final Runnable stepInto = () -> {
        try {
            synchronized (application.sourceParser) {
                int line = application.sourceParser.getCurrentLine();

                Platform.runLater(() -> {
                    application.controller.editorManager.clearExecutedLines();
                    application.controller.editorManager.markLineAsExecuted(line+1);
                });

                application.codeInterpreter.nextLine();
                application.codeInterpreter.executeCurrentLine();

                Platform.runLater(() -> application.controller.updateRegisters(application.codeInterpreter.stateContainer));
            }
        } catch (Exception e) {
            Platform.runLater(() -> new ExceptionDialog(e).show());
        }
    };

    public final Runnable stepOver = () -> {
        doContinue = true;
        while (doContinue) {
            stepInto.run();
            synchronized (deamon) {
                try {
                    deamon.wait(WAITING_PERIOD);
                } catch (InterruptedException ignored) {
                }
            }
        }
    };

    public final Runnable conti = () -> {
        doContinue = true;
        while (doContinue) {
            stepInto.run();
        }
    };

    public ExecutionWorker(JArmEmuApplication application) {
        this.application = application;
        this.queue = new ArrayBlockingQueue<>(5);

        this.deamon = new Thread(() -> {
            synchronized (this.deamon) {
                while (doRun) {
                    try {
                        this.deamon.wait();
                        for (Runnable run : queue) {
                            run.run();
                        }
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
            }
        });

        deamon.setDaemon(true);
        deamon.setName("CodeExecutorDeamon");
        deamon.start();
    }

    public void execute(Runnable runnable) {
        if (queue.remainingCapacity() > 0) queue.add(runnable);
        if (this.deamon.isAlive()) synchronized (this.deamon) {this.deamon.notifyAll();}
    }

    public void pause() {
        this.doContinue = false;
    }

    public void stop() {
        this.doContinue = this.doRun = false;
        if (this.deamon.isAlive()) {
            if (!this.deamon.isInterrupted()) deamon.interrupt();
            this.deamon = null;
        }
    }
}
