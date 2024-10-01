package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.controls.Popover;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.gui.controllers.PopupController;
import fr.dwightstudio.jarmemu.base.gui.factory.JArmEmuLineFactory;
import fr.dwightstudio.jarmemu.base.sim.ExecutionWorker;

import static fr.dwightstudio.jarmemu.base.gui.controllers.PopupController.*;

public class JArmEmuPopups {

    public static void build() {
        // 0
        popoverMaker(() -> null, Popover.ArrowLocation.TOP_CENTER);

        // 1
        popoverMaker(PopupController::getMainPane, Popover.ArrowLocation.TOP_CENTER);

        // 2
        popoverMaker(PopupController::getMainTabPaneHeader, Popover.ArrowLocation.TOP_CENTER);

        // 3
        popoverMaker(PopupController::getToolSimulate, Popover.ArrowLocation.TOP_CENTER, () -> {
            if (JArmEmuApplication.getStatus() == Status.SIMULATING) {
                JArmEmuApplication.getSimulationMenuController().onStop();
            }
        }, () -> {
            if (JArmEmuApplication.getStatus() != Status.SIMULATING) {
                JArmEmuApplication.getSimulationMenuController().onSimulate();
            }
            JArmEmuApplication.getSettingsController().setSimulationInterval(100);
        });

        // 4
        popoverMaker(PopupController::getToolContinue, Popover.ArrowLocation.TOP_CENTER, () -> {
            if (JArmEmuApplication.getExecutionWorker().getTask() != ExecutionWorker.Task.IDLE) {
                JArmEmuApplication.getSimulationMenuController().onPause();
            }
        }, () -> {
            if (JArmEmuApplication.getExecutionWorker().getTask() == ExecutionWorker.Task.IDLE) {
                JArmEmuApplication.getSimulationMenuController().onContinue();
            }
        });

        // 5
        popoverMaker(PopupController::getLineMargin, Popover.ArrowLocation.LEFT_CENTER);

        // 6
        popoverMaker(PopupController::getRegistersPane, Popover.ArrowLocation.LEFT_CENTER);

        // 7
        popoverMaker(PopupController::getStackPane, Popover.ArrowLocation.BOTTOM_CENTER, () -> {
            PopupController.getLeftTabPane().getSelectionModel().select(0);
        }, NOTHING);

        // 8
        popoverMaker(PopupController::getLineMargin, Popover.ArrowLocation.LEFT_CENTER, NOTHING, () -> {
            JArmEmuLineFactory lineFactory = JArmEmuApplication.getEditorController().currentFileEditor().getLineFactory();
            if (!lineFactory.hasBreakpoint(11)) {
                lineFactory.onToggleBreakpoint(11);
            }
        });

        // 9
        popoverMaker(PopupController::getLabelPane, Popover.ArrowLocation.BOTTOM_CENTER, () -> {
            if (JArmEmuApplication.getExecutionWorker().getTask() != ExecutionWorker.Task.IDLE) {
                JArmEmuApplication.getSimulationMenuController().onPause();
            }
            PopupController.getLeftTabPane().getSelectionModel().select(2);
        }, NOTHING);

        // 10
        popoverMaker(PopupController::getRightTabPane, Popover.ArrowLocation.RIGHT_CENTER);

        // 11
        popoverMaker(PopupController::getRightTabPane, Popover.ArrowLocation.RIGHT_CENTER);

        // 12
        popoverMaker(PopupController::getToolStop, Popover.ArrowLocation.TOP_CENTER, NOTHING, () -> {
            JArmEmuApplication.getSettingsController().setSimulationInterval(30);
            JArmEmuApplication.getSimulationMenuController().onStop();
        });

        // 13
        popoverMaker(PopupController::getMenu, Popover.ArrowLocation.TOP_CENTER);

        // 14
        popoverMaker(PopupController::getRightTabPaneHeader, Popover.ArrowLocation.BOTTOM_CENTER);

        addLinks();

        if (JArmEmuApplication.getSettingsController().getTour()) {
            JArmEmuApplication.getPopUps().tour();
        }
    }

    public void tour() {
        JArmEmuApplication.getSettingsController().setTour(false);

        JArmEmuApplication.getSimulationMenuController().onStop();
        JArmEmuApplication.getEditorController().closeAll();
        JArmEmuApplication.getEditorController().open("source.s", ".global _start\n" +
                ".text\n" +
                "_start:\n" +
                "\t@ Beginning of the program\n" +
                "\tmov r0, #2\n" +
                "\tmov r1, #0\n" +
                "\t\n" +
                "loop1:\n" +
                "\tloop2:\n" +
                "\t\tadd r0, r0\n" +
                "\t\tsub r0, #1\n" +
                "\t\tcmp r0, #2048\n" +
                "\t\tbls loop1\n" +
                "\tmov r0, #2\n" +
                "\tadd r1, #1\n" +
                "\tpush {r1}\n" +
                "\tcmp r1, #256\n" +
                "\tbls loop1\n" +
                "\n" +
                "_end:\n" +
                "\tbal _end");

        begin();
    }
}
