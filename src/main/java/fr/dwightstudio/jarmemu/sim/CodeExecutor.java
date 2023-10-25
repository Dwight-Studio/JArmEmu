package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import javafx.application.Platform;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.concurrent.ArrayBlockingQueue;

public class CodeExecutor {

    private static final int WAITING_PERIOD = 250;

    private Thread deamon;
    private JArmEmuApplication application;
    private final ArrayBlockingQueue<Runnable> queue;
    private boolean doContinue = false;
    private boolean doRun = true;

    public final Runnable stepInto = () -> {
        try {
            synchronized (application.sourceInterpreter) {
                int line = application.sourceInterpreter.getCurrentLine();

                Platform.runLater(() -> application.controller.editorManager.markLineAsExecuted(line));

                application.sourceInterpreter.readOneLine();
                application.sourceInterpreter.executeCurrentLine();

                Platform.runLater(() -> application.controller.updateRegisters(application.sourceInterpreter.stateContainer));
            }
        } catch (Exception e) {
            Platform.runLater(() -> new ExceptionDialog(e).show());
        }
    };

    public final Runnable stepOver = () -> {
        doContinue = true;
        while (doContinue) {
            stepInto.run();
        }
    };

    public final Runnable conti = () -> {
        doContinue = true;
        while (doContinue) {
            stepInto.run();
        }
    };

    public CodeExecutor(JArmEmuApplication application) {
        this.application = application;
        this.queue = new ArrayBlockingQueue<>(5);


        this.deamon = new Thread(() -> {
            while (doRun) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                    break;
                }

                for (Runnable run : queue) {
                    run.run();
                }
            }
        });

        deamon.setDaemon(true);
        deamon.setName("CodeExecutorDeamon");
    }

    public void execute(Runnable runnable) {
        if (queue.remainingCapacity() > 0) queue.add(runnable);
        if (this.deamon.isAlive()) this.deamon.notifyAll();
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
