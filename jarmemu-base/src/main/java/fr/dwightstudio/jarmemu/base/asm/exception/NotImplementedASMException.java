package fr.dwightstudio.jarmemu.base.asm.exception;

import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

public class NotImplementedASMException extends ASMException {
    @Override
    public String getTitle() {
        return JArmEmuApplication.formatMessage("%exception.notImplemented");
    }

    public NotImplementedASMException(String s) {
        super(s);
    }
}
