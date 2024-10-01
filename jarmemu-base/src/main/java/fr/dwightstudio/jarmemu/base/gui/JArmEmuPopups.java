package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.controls.Popover;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.gui.controllers.PopupController;

import static fr.dwightstudio.jarmemu.base.gui.controllers.PopupController.*;

public class JArmEmuPopups {

    public static void build() {
        popoverMaker(PopupController.getMainPane(), Popover.ArrowLocation.TOP_CENTER);

        popoverMaker(PopupController.getTabPaneHeader(), Popover.ArrowLocation.TOP_CENTER);

        popoverMaker(PopupController.getToolSimulate(), Popover.ArrowLocation.TOP_CENTER, () -> {
            if (JArmEmuApplication.getStatus() == Status.SIMULATING) {
                JArmEmuApplication.getSimulationMenuController().onStop();
            }
        }, NOTHING);

        popoverMaker(PopupController.getToolContinue(), Popover.ArrowLocation.TOP_CENTER, () -> {
            if (JArmEmuApplication.getStatus() != Status.SIMULATING) {
                JArmEmuApplication.getSimulationMenuController().onSimulate();
            }
        }, NOTHING);

        addLinks();
    }

    public void tour() {
        JArmEmuApplication.getSettingsController().setTour(false);

        Popup popup = getPopovers().getFirst();
        popup.before().run();
        popup.popover().show(getPopovers().getFirst().location());
    }
}
