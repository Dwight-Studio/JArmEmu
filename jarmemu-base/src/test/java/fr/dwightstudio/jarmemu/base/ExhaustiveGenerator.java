package fr.dwightstudio.jarmemu.base;

import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.util.ModifierUtils;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ExhaustiveGenerator {

    private static Logger logger = Logger.getLogger(ExhaustiveGenerator.class.getSimpleName());


    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(ExhaustiveGenerator.class.getResourceAsStream("/logging.properties"));



        try (FileWriter fileWriter = new FileWriter("./jarmemu-base/src/test/resources/exhaustive.s", false)) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                generateAllInstructions(bufferedWriter::append);
            }
        }
    }

    public static void generateAllInstructions(AppendableString buffer) throws Exception {
        int count = 0;
        final int total = Instruction.values().length - 1;

        buffer.append(".set N, 88\n");
        buffer.append(".text\n");
        buffer.append("label:\n");

        for (Instruction instruction : Instruction.values()) {
            logger.info("%3d%% - Generating for %s".formatted(Math.floorDiv(count * 100, total), instruction));
            count++;

            buffer.append(generateInstruction(buffer, instruction));
        }
    }

    public static String generateInstruction(AppendableString buffer, Instruction instruction) throws Exception {
        if (!instruction.isValid()) return "";

        String argType1 = instruction.getArgumentType(0);
        String argType2 = instruction.getArgumentType(1);
        String argType3 = instruction.getArgumentType(2);
        String argType4 = instruction.getArgumentType(3);

        ModifierUtils.PossibleModifierIterator it = new ModifierUtils.PossibleModifierIterator(instruction.getModifierParameterClasses());
        while (it.hasNext()) {
            Modifier modifier = it.next();

            if (!argType1.equals("NullArgument")) {
                for (String arg1 : possibleArgument(argType1)) {
                    if (!argType2.equals("NullArgument")) {
                        for (String arg2 : possibleArgument(argType2)) {
                            if (!argType3.equals("NullArgument")) {
                                for (String arg3 : possibleArgument(argType3)) {
                                    if (!argType4.equals("NullArgument")) {
                                        for (String arg4 : possibleArgument(argType4)) {
                                            addInstruction(buffer, instruction, modifier, arg1, arg2, arg3, arg4);
                                        }
                                    } else {
                                        addInstruction(buffer, instruction, modifier, arg1, arg2, arg3, null);
                                    }
                                }
                            } else {
                                addInstruction(buffer, instruction, modifier, arg1, arg2, null, null);
                            }
                        }
                    } else {
                        addInstruction(buffer, instruction, modifier, arg1, null, null, null);
                    }
                }
            } else {
                addInstruction(buffer, instruction, modifier, null, null, null, null);
            }

            if (instruction.hasWorkingRegister()) {
                if (!argType2.equals("NullArgument")) {
                    for (String arg1 : possibleArgument(argType2)) {
                        if (!argType3.equals("NullArgument")) {
                            for (String arg2 : possibleArgument(argType3)) {
                                if (!argType4.equals("NullArgument")) {
                                    for (String arg3 : possibleArgument(argType4)) {
                                        addInstruction(buffer, instruction, modifier, arg1, arg2, arg3, null);
                                    }
                                } else {
                                    addInstruction(buffer, instruction, modifier, arg1, arg2, null, null);
                                }
                            }
                        } else {
                            addInstruction(buffer, instruction, modifier, arg1, null, null, null);
                        }
                    }
                } else {
                    addInstruction(buffer, instruction, modifier, null, null, null, null);
                }
            }
        }

        return buffer.toString();
    }

    private static void addInstruction(AppendableString buffer, Instruction instruction, Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws Exception {
        buffer.append(instruction.name());
        buffer.append(modifier.toString());
        buffer.append(" ");

        if (arg1 != null) {
            buffer.append(arg1);
            if (arg2 != null) {
                buffer.append(", ");
                buffer.append(arg2);
                if (arg3 != null) {
                    buffer.append(", ");
                    buffer.append(arg3);
                    if (arg4 != null) {
                        buffer.append(", ");
                        buffer.append(arg4);
                    }
                }
            }
        }

        buffer.append("\n");
    }

    private static String[] possibleArgument(String argumentType) {
        List<String> list = new ArrayList<>();

        switch (argumentType) {
            case "RegisterArgument" -> {
                for (RegisterUtils value : RegisterUtils.values()) {
                    if (!value.isSpecial()) list.add(value.name());
                }
            }

            case "RegisterWithUpdateArgument" -> {
                for (RegisterUtils value : RegisterUtils.values()) {
                    if (!value.isSpecial()) {
                        list.add(value.name());
                        list.add(value.name() + "!");
                    }
                }
            }

            case "ImmediateArgument", "RotatedImmediateArgument" -> addImmediate(list, "#");

            case "ImmediateOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> {
                for (RegisterUtils value : RegisterUtils.values()) {
                    if (!value.isSpecial()) list.add(value.name());
                }

                addImmediate(list, "#");
            }

            case "ShiftArgument" -> {
                /*
                addImmediate(list, "LSL #");
                addImmediate(list, "LSR #");
                 */
                addImmediate(list, "ASR #");
                addImmediate(list, "ROR #");
                addImmediate(list, "RRX #");
            }

            case "RegisterAddressArgument" -> {
                for (RegisterUtils value : RegisterUtils.values()) {
                    if (!value.isSpecial()) list.add("[" + value.name() + "]");
                }
            }

            case "RegisterArrayArgument" -> {
                for (RegisterUtils r1 : RegisterUtils.values()) {
                    if (r1.isSpecial()) continue;
                    for (RegisterUtils r2 : RegisterUtils.values()) {
                        if (r2.isSpecial()) continue;
                        if (r1.getN() < r2.getN()) {
                            list.add("{" + r1.name() + "-" + r2.name() + "}");
                        }
                        if (r1 != r2) list.add("{" + r1.name() + ", " + r2.name() + "}");
                    }
                }
            }

            case "LabelArgument" -> {
                list.add("label");
            }

            case "LabelOrRegisterArgument" -> {
                for (RegisterUtils value : RegisterUtils.values()) {
                    if (!value.isSpecial()) list.add("[" + value.name() + "]");
                }
                list.add("label");
            }

            case "AddressArgument" -> {
                addImmediate(list, "=");

                for (RegisterUtils r1 : RegisterUtils.values()) {
                    if (r1.isSpecial()) continue;
                    list.add("[" + r1.name() + "]");
                    addImmediate(list, "[" + r1.name() + ", #", "]");
                    for (RegisterUtils r2 : RegisterUtils.values()) {
                        if (r2.isSpecial()) continue;
                        list.add("[" + r1.name() + ", " + r2.name() + "]");
                        addImmediate(list, "[" + r1.name() + ", " + r2.name() + ", LSL #", "]");
                        addImmediate(list, "[" + r1.name() + ", " + r2.name() + ", LSR #", "]");
                        /*
                        addImmediate(list, "[" + r1.name() + ", " + r2.name() + ", ASR #", "]");
                        addImmediate(list, "[" + r1.name() + ", " + r2.name() + ", ROR #", "]");
                        addImmediate(list, "[" + r1.name() + ", " + r2.name() + ", RRX #", "]");
                         */
                    }
                }
            }
        }

        return list.toArray(new String[0]);
    }

    private static void addImmediate(List<String> list, String prefix) {
        addImmediate(list, prefix, "");
    }

    private static void addImmediate(List<String> list, String prefix, String suffix) {
        /*
        list.add(prefix + "N" + suffix);
        list.add(prefix + "2" + suffix);
         */
        list.add(prefix + "4/2" + suffix);
    }

    public static interface AppendableString {
        void append(String string) throws Exception;
    }
}
