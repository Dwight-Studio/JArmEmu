package fr.dwightstudio.jarmemu.asm.args;

public class ArgumentParsers {

    public static final NullParser NULL = new NullParser();
    public static final RegisterParser REGISTER = new RegisterParser();
    public static final AddressParser ADRRESS = new AddressParser();
    public static final ValueParser VALUE = new ValueParser();
    public static final ValueOrRegisterParser VALUE_OR_REGISTER = new ValueOrRegisterParser();

}
