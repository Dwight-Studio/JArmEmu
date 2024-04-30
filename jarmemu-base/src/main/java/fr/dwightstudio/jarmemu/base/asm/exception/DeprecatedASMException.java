package fr.dwightstudio.jarmemu.base.asm.exception;

import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

public class DeprecatedASMException extends ASMException {
    @Override
    public String getTitle() {
        return JArmEmuApplication.formatMessage("%exception.deprecated");
    }

    public DeprecatedASMException(String s) {
        super(s);
    }
}
