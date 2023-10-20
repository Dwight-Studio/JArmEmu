package fr.dwightstudio.jarmemu.asm.args;

// Correspond à "mem"
public class AddressParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(String string) {
        return 0; // Nombre sur 8 bits
    }
}
