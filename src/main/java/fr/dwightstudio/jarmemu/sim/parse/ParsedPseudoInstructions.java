package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;

import java.util.HashMap;
import java.util.Set;

public class ParsedPseudoInstructions extends ParsedObject {

    @Override
    public AssemblyError verify(int line, HashMap<String, Integer> labels) {
        return null;
    }
}
