package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class RegisterWithUpdateArgument extends ParsedArgument<RegisterWithUpdateArgument.UpdatableRegister> {

    boolean update;
    RegisterArgument argument;

    public RegisterWithUpdateArgument(String originalString) {
        super(originalString);
    }

    @Override
    protected void parse(String originalString) throws SyntaxASMException {
        update = false;

        String string = originalString;

        if (string.endsWith("!")) {
            update = true;
            string = string.substring(0, string.length()-1);
        }

        argument = new RegisterArgument(string);
    }

    @Override
    public UpdatableRegister getValue(StateContainer stateContainer) throws ExecutionASMException {
        return new UpdatableRegister(argument.getValue(stateContainer), update);
    }

    @Override
    public UpdatableRegister getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegister"));
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        argument.verify(stateSupplier, currentLine);

        super.verify(stateSupplier, currentLine);
    }

    public static final class UpdatableRegister extends Register {
        private final Register register;
        private boolean update;

        public UpdatableRegister(Register register, boolean update) {
            this.register = register;
            this.update = update;
        }

        @Override
        public int getData() {
            return register.getData();
        }

        @Override
        public void setData(int data) throws IllegalArgumentException {
            register.setData(data);
        }

        @Override
        public boolean get(int index) throws IllegalArgumentException {
            return register.get(index);
        }

        @Override
        public void set(int index, boolean value) {
            register.set(index, value);
        }

        @Override
        public void add(int value) {
            register.add(value);
        }

        /**
         * Met à jour le registre en fonction du nombre de registres de l'argument RegisterArray
         */
        public void update(int value) {
            if (update) register.add(value);
            update = false;
        }
    }
}
