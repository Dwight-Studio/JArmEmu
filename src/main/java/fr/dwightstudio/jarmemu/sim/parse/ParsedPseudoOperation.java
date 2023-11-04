package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;

import java.util.Set;

public class ParsedPseudoOperation extends ParsedObject {

    @Override
    public AssemblyError verify(int line, Set<String> labels) {
        return null;
    }
}
