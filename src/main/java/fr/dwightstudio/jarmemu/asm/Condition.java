package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.function.Function;

public enum Condition {

    AL((state) -> true),
    EQ((state) -> true),
    NE((state) -> true),
    CS((state) -> true),
    CC((state) -> true),
    MI((state) -> true),
    PL((state) -> true),
    VS((state) -> true),
    VC((state) -> true),
    HS((state) -> true),
    LO((state) -> true),
    HI((state) -> true),
    LS((state) -> true),
    GE((state) -> true),
    LT((state) -> true),
    GT((state) -> true);

    private final Function<StateContainer, Boolean> tester;

    Condition(Function<StateContainer, Boolean> tester) {
        this.tester = tester;
    }

    public boolean eval(StateContainer stateContainer) {
        return tester.apply(stateContainer);
    }

}
