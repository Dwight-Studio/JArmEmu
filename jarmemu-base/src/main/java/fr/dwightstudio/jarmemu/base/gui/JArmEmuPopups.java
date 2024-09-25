package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.controls.Popover;

public class JArmEmuPopups {

    public void tour() {
        JArmEmuApplication.getSettingsController().setTour(false);

        var pop1 = new Popover();
        pop1.setTitle("Lorem Ipsum");
        pop1.setHeaderAlwaysVisible(true);
        pop1.setDetachable(true);

        pop1.show();
    }

}
