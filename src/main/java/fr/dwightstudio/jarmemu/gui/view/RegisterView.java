package fr.dwightstudio.jarmemu.gui.view;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import javafx.beans.property.*;

public class RegisterView {
    private final Register register;
    private final StringProperty nameProperty;
    private final IntegerProperty valueProperty;
    private final ReadOnlyStringProperty flagsProperty;

    public RegisterView(Register register, String name) {
        this.register = register;
        this.nameProperty = new ReadOnlyStringWrapper(name);
        this.valueProperty = register.getDataProperty();
        this.flagsProperty = new FlagProperty();
    }

    public ReadOnlyStringProperty getNameProperty() {
        return nameProperty;
    }

    public IntegerProperty getValueProperty() {
        return valueProperty;
    }

    public ReadOnlyStringProperty getFlagsProperty() {
        return flagsProperty;
    }

    public class FlagProperty extends ReadOnlyStringPropertyBase {

        @Override
        public Object getBean() {
            return RegisterView.class;
        }

        @Override
        public String getName() {
            return "flag";
        }

        @Override
        public String get() {
            if (register.isPSR()) {
                return register.toString();
            } else {
                return "";
            }
        }
    }

    public Register getRegister() {
        return register;
    }
}
