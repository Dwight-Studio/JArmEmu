package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.function.Function;

public enum Condition {

    AL((state) -> true),
    EQ((state) -> state.cpsr.getZ()),
    NE((state) -> !state.cpsr.getZ()),
    CS((state) -> state.cpsr.getC()),
    CC((state) -> !state.cpsr.getC()),
    MI((state) -> state.cpsr.getN()),
    PL((state) -> !state.cpsr.getN()),
    VS((state) -> state.cpsr.getV()),
    VC((state) -> !state.cpsr.getV()),
    HS((state) -> state.cpsr.getC()),
    LO((state) -> !state.cpsr.getC()),
    HI((state) -> state.cpsr.getC() && !state.cpsr.getZ()),
    LS((state) -> !state.cpsr.getC() || state.cpsr.getZ()),
    GE((state) -> state.cpsr.getN() == state.cpsr.getV()),
    LT((state) -> state.cpsr.getN() != state.cpsr.getV()),
    GT((state) -> !state.cpsr.getZ() && (state.cpsr.getN() == state.cpsr.getV())),
    LE((state) -> state.cpsr.getZ() || (state.cpsr.getN() != state.cpsr.getV()));

    private final Function<StateContainer, Boolean> tester;

    Condition(Function<StateContainer, Boolean> tester) {
        this.tester = tester;
    }

    public boolean eval(StateContainer stateContainer) {
        return tester.apply(stateContainer);
    }

}
