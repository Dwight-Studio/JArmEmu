package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class DirectiveExecutors {

    public static final DirectiveExecutor NOT_IMPLEMENTED = new DirectiveExecutor() {
        @Override
        public void apply(StateContainer stateContainer, String args) {
            throw new IllegalStateException("Directive not implemented");
        }

        @Override
        public int computeDataLength(String args) {
            throw new IllegalStateException("Directive not implemented");
        }
    };

}
