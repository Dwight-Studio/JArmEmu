package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

// Correspond à "mem", à utiliser avec Value12OrRegisterParser et ShiftParser
public class AddressParser implements ArgumentParser<AddressParser.UpdatableInteger> {
//TODO: corriger ça
    protected static HashMap<StateContainer, Integer> updateValue = new HashMap<>();

    public static void reset(StateContainer stateContainer) {
        updateValue.remove(stateContainer);
    }

    @Override
    public UpdatableInteger parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (!string.startsWith("[")) {
            throw new SyntaxASMException("Invalid address '" + string + "'");
        }

        boolean updateNow = string.endsWith("!");

        if (updateNow) string = string.substring(0, string.length() - 1);

        if (string.endsWith("]")) {
            String mem = string.substring(1, string.length() - 1);
            String[] mems = mem.split(",");

            mems = Arrays.stream(mems).map(String::strip).toArray(String[]::new);

            Register reg = ArgumentParsers.REGISTER.parse(stateContainer, mems[0]);

            if (mems.length == 1) {
                return new UpdatableInteger(reg.getData(),
                        stateContainer,
                        true,
                        updateNow,
                        reg);

            } else if (mems.length == 2) {
                if (mems[1].startsWith("#")) {
                    return new UpdatableInteger(reg.getData() + ArgumentParsers.VALUE_12.parse(stateContainer, mems[1]),
                            stateContainer,
                            false,
                            updateNow,
                            reg);
                } else {
                    return new UpdatableInteger(reg.getData() + ArgumentParsers.REGISTER.parse(stateContainer, mems[1]).getData(),
                            stateContainer,
                            false,
                            updateNow,
                            reg);
                }

            } else if (mems.length == 3) {
                ShiftParser.ShiftFunction sf = ArgumentParsers.SHIFT.parse(stateContainer, mems[2]);
                return new UpdatableInteger(reg.getData() + sf.apply(ArgumentParsers.REGISTER.parse(stateContainer, mems[1]).getData()),
                        stateContainer,
                        false,
                        updateNow,
                        reg);
            } else {
                throw new SyntaxASMException("Invalid address '" + string + "'");
            }

        } else {
            throw new SyntaxASMException("Invalid address '" + string + "'");
        }
    }

    @Override
    public AddressParser.UpdatableInteger none() {
        return null;
    }

    public static final class UpdatableInteger {

        private final int integer;
        private final StateContainer stateContainer;
        private final Register register;
        private boolean update;

        private UpdatableInteger(int integer, StateContainer stateContainer, boolean update, boolean updateNow, Register register) {
            this.integer = integer;
            this.register = register;
            this.update = update;
            this.stateContainer = stateContainer;

            if (updateNow) register.setData(integer);
        }

        public int toInt() {
            return integer;
        }

        public void update() {
            if (update && updateValue.containsKey(stateContainer)) register.setData(updateValue.get(stateContainer));
            update = false;
        }
    }
}