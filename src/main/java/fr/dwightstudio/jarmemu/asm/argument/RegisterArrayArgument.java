package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterArrayArgument extends ParsedArgument<Register[]> {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    ArrayList<RegisterArgument> arguments;

    public RegisterArrayArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString == null) throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegisterArray"));

        if (originalString.startsWith("{") && originalString.endsWith("}")) {
            arguments = new ArrayList<>();

            String arrayString = originalString.substring(1, originalString.length()-1);

            for (String regString : arrayString.split(",")) {
                if(regString.contains("-")){
                    String[] stringArray = regString.split("-");
                    if (stringArray.length != 2) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unexpectedRegisterArray", originalString));
                    int registerFirst = Integer.parseInt(stringArray[0].strip().substring(1));
                    int registerSecond = Integer.parseInt(stringArray[1].strip().substring(1));
                    for (int i = registerFirst; i <= registerSecond; i++) {
                        arguments.add(new RegisterArgument("R" + i));
                    }
                } else {
                    arguments.add(new RegisterArgument(regString.strip()));
                }
            }
        } else {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unexpectedRegisterArray", originalString));
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        for (RegisterArgument argument : arguments) {
            argument.contextualize(stateContainer);
        }
    }

    @Override
    public Register[] getValue(StateContainer stateContainer) throws ExecutionASMException {
        ArrayList<Register> rtn = new ArrayList<>();

        for (RegisterArgument argument : arguments) {
            Register reg = argument.getValue(stateContainer);
            if (!rtn.contains(reg)) {
                rtn.add(reg);
            } else {
                logger.log(Level.WARNING, "Duplicate register in array");
            }
        }

        return rtn.toArray(new Register[0]);
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        for (RegisterArgument argument : arguments) {
            argument.verify(stateSupplier);
        }

        super.verify(stateSupplier);
    }
}
