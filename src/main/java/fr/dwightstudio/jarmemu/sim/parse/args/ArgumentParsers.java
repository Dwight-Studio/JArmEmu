package fr.dwightstudio.jarmemu.sim.parse.args;

public class ArgumentParsers {

    public static final NullParser NULL = new NullParser();
    public static final RegisterParser REGISTER = new RegisterParser();
    public static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();
    public static final RegisterAddressParser REGISTER_ADDRESS = new RegisterAddressParser();
    public static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();
    public static final AddressParser ADDRESS = new AddressParser();

    public static final ImmParser IMM = new ImmParser();
    public static final RotatedImmParser ROTATED_IMM = new RotatedImmParser();

    public static final LabelParser LABEL = new LabelParser();
    public static final ImmOrRegisterParser IMM_OR_REGISTER = new ImmOrRegisterParser();
    public static final RotatedImmOrRegisterParser ROTATED_IMM_OR_REGISTER = new RotatedImmOrRegisterParser();
    public static final ShiftParser SHIFT = new ShiftParser();

}
