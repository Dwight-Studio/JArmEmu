package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class ParsedDirective extends ParsedObject {

    protected final Section section;
    protected final String args;
    private boolean generated;
    protected String hash;

    public ParsedDirective(Section section, @NotNull String args) {
        this.args = args;
        this.section = section;
        generated = false;
    }

    public abstract void execute() throws ASMException;

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        execute();
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(@NotNull String hash) {
        this.hash = hash;
        this.generated = true;
    }
}
