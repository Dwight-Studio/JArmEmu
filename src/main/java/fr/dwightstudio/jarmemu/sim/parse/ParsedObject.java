package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;

public abstract class ParsedObject {

    public static final boolean VERBOSE = true;

    public abstract AssemblyError verify(int line);

}
