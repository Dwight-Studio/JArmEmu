package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class ParsedLabel extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String name;
    private final int line;

    public ParsedLabel(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    @Override
    public AssemblyError verify(int line, Set<String> labels) {
        return null;
    }

    public void register(StateContainer stateContainer) {
        stateContainer.labels.put(name.strip().toUpperCase(), line);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedLabel label)) return false;

        if (label.name == null) {
            if (this.name != null) {
                if (VERBOSE) logger.info("Difference: Name (Null)");
                return false;
            }
        } else {
            if (!(label.name.equalsIgnoreCase(this.name))) {
                if (VERBOSE) logger.info("Difference: Name");
                return false;
            }
        }

        if (label.line != this.line) {
            if (VERBOSE) logger.info("Difference: Line");
            return false;
        }

        return true;
    }
}
