package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.controls.Popover;
import fr.dwightstudio.jarmemu.base.gui.controllers.PopupController;

import static fr.dwightstudio.jarmemu.base.gui.controllers.PopupController.*;

public class JArmEmuPopups {

    public static void build() {
        popoverMaker("%tour.firstpopup.title", "%tour.firstpopup.message", PopupController.getRegistersPane(), Popover.ArrowLocation.LEFT_CENTER);
        popoverMaker(null, "%tour.secondpopup.message", PopupController.getToolSimulate(), Popover.ArrowLocation.TOP_CENTER);
        popoverMaker("%tour.thirdpopup.title", "%tour.thirdpopup.message", PopupController.getToolStop(), Popover.ArrowLocation.LEFT_CENTER);

        addLinks();
    }

    public void tour() {
        JArmEmuApplication.getSettingsController().setTour(false);

        getPopovers().getFirst().popover().show(getPopovers().getFirst().location());
    }
}
