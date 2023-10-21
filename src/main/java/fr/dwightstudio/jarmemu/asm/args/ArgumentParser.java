package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;

public interface ArgumentParser<T> {

    public abstract T parse(StateContainer stateContainer, String string);

}
