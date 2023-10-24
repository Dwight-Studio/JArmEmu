package fr.dwightstudio.jarmemu.asm.args;

public class ArgumentParsers {

    public static final NullParser NULL = new NullParser();
    public static final RegisterParser REGISTER = new RegisterParser();
    public static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();
    public static final RegisterAddressParser REGISTER_ADDRESS = new RegisterAddressParser();
    public static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();
    public static final AddressParser ADDRESS = new AddressParser();

    public static final Value8Parser VALUE_8 = new Value8Parser();
    public static final Value12Parser VALUE_12 = new Value12Parser();

    public static final LabelParser LABEL = new LabelParser();
    public static final Value8OrRegisterParser VALUE_8_OR_REGISTER = new Value8OrRegisterParser();
    public static final Value12OrRegisterParser VALUE_12_OR_REGISTER = new Value12OrRegisterParser();
    public static final ShiftParser SHIFT = new ShiftParser();

}
