package fr.dwightstudio.jarmemu.asm.args;

public class ArgumentParsers {

    public static final NullParser NULL = new NullParser();
    public static final RegisterParser REGISTER = new RegisterParser();
    public static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();
    public static final RegisterAddressParser REGISTER_ADDRESS = new RegisterAddressParser();
    public static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();
    public static final AddressParser ADDRESS = new AddressParser();
    public static final ValueParser VALUE = new ValueParser();
    public static final ValueOrRegisterParser VALUE_OR_REGISTER = new ValueOrRegisterParser();
    public static final ShiftParser SHIFT = new ShiftParser();

}
