package fr.dwightstudio.jarmemu.asm.args;

// Correspond à pas d'argument
public class NullParser implements ArgumentParser<Object> {
    @Override
    public Object parse(String string) {
        throw new IllegalStateException("Parsing a Null Argument");
    }
}
