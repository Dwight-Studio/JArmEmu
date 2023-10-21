package fr.dwightstudio.jarmemu.asm.args;

public interface ArgumentParser<T> {

    public abstract T parse(String string);

}
