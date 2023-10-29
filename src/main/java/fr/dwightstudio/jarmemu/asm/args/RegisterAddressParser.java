package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "[reg]"
public class RegisterAddressParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("[") && string.endsWith("]")) {
            string = string.substring(1, string.length()-1);
            return ArgumentParsers.REGISTER.parse(stateContainer, string).getData();
        } else {
            throw new SyntaxASMException("Invalid address (from register) '" + string + "'");
        }
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing address (from register)");
    }
}
