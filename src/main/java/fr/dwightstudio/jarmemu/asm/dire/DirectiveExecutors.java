package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class DirectiveExecutors {

    public static final DirectiveExecutor NOT_IMPLEMENTED = new DirectiveExecutor() {
        @Override
        public void apply(StateContainer stateContainer, String args, int currentPos) {
            throw new IllegalStateException("Directive not implemented");
        }

        @Override
        public int computeDataLength(String args, int currentPos) {
            throw new IllegalStateException("Directive not implemented");
        }
    };

    // Consts
    public static final GlobalExecutor GLOBAL = new GlobalExecutor();
    public static final EquivalentExecutor EQUIVALENT = new EquivalentExecutor();

    // Data
    public static final WordExecutor WORD = new WordExecutor();
    public static final HalfExecutor HALF = new HalfExecutor();
    public static final ByteExecutor BYTE = new ByteExecutor();
    public static final SpaceExecutor SPACE = new SpaceExecutor();
    public static final ASCIIExecutor ASCII = new ASCIIExecutor();
    public static final ASCIZExecutor ASCIZ = new ASCIZExecutor();
    public static final FillExecutor FILL = new FillExecutor();

    // Other
    public static final AlignExecutor ALIGN = new AlignExecutor();

}
