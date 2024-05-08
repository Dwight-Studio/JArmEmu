package fr.dwightstudio.jarmemu.base.sim.entity;

import fr.dwightstudio.jarmemu.base.asm.argument.ImmediateOrRegisterArgument;

import java.util.Objects;

public class RegisterOrImmediate extends Number {
    private final Integer immediate;
    private final Register register;

    public RegisterOrImmediate(int immediate) {
        this.immediate = immediate;
        this.register = null;
    }

    public RegisterOrImmediate(Register register) {
        this.immediate = null;
        this.register = register;
    }

    public boolean isRegister() {
        return register != null;
    }

    private int getValue() {
        if (isRegister()) {
            return register.getData();
        } else {
            return immediate;
        }
    }

    @Override
    public int intValue() {
        return getValue();
    }

    @Override
    public long longValue() {
        return getValue();
    }

    @Override
    public float floatValue() {
        return getValue();
    }

    @Override
    public double doubleValue() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegisterOrImmediate registerOrImmediate) {
            if (isRegister()) {
                return Objects.equals(register, registerOrImmediate.register);
            } else {
                return Objects.equals(immediate, registerOrImmediate.immediate);
            }
        } else if (obj instanceof Register reg) {
            return isRegister() && reg.equals(register);
        } else if (obj instanceof Number number) {
            return intValue() == number.intValue();
        }
        return false;
    }
}
