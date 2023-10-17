package fr.dwightstudio.jarmemu.asm.args;

public class RegisterAddressParser implements ArgumentParser {
    @Override
    public int parse(String string) {
        return 0; // Nombre sur 8 bits, ce parser ne sert que pour le 3ème arg de SWP
    }
}
