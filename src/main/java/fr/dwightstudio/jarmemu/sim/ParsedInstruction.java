package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.ArgumentParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ParsedInstruction {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Instruction instruction;
    private final String[] args;

    public ParsedInstruction(Instruction instruction, String arg1, String arg2, String arg3, String arg4) {
        this.instruction = instruction;
        this.args = new String[]{arg1, arg2, arg3, arg4};
    }

    public AssemblyError verify(int line) {
        StateContainer stateContainer = new StateContainer();
        try {
            execute(stateContainer);
            return null;
        } catch (AssemblySyntaxException exception) {
            return new AssemblyError(line, exception);
        } finally {
            AddressParser.reset(stateContainer);
            RegisterWithUpdateParser.reset(stateContainer);
        }
    }

    public void execute(StateContainer stateContainer) {
        ArgumentParser[] argParsers = instruction.getArgParsers();
        Object[] parsedArgs = new Object[4];

        try {
            for (int i = 0; i < 4; i++) {
                if (args[i] != null) {
                    parsedArgs[i] = argParsers[i].parse(stateContainer, args[i]);
                } else {
                    parsedArgs[i] = argParsers[i].none();
                }
            }
        } catch (AssemblySyntaxException exception) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(exception));
            // Erreur de syntaxe
        } catch (Exception exception) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(exception));
            // Erreur fatale
        }

    }
}
