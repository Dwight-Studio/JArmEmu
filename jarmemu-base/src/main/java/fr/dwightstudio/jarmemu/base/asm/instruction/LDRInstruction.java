/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.argument.*;
import fr.dwightstudio.jarmemu.base.asm.directive.WordDirective;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.*;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LDRInstruction extends ParsedInstruction<Register, AddressArgument.UpdatableInteger, RegisterOrImmediate, ShiftFunction> implements PseudoInstruction {
    private static final Pattern PSEUDO_INS_PATTERN = Pattern.compile("=(?<VALUE>[^\n\\[\\]{}]+)");

    private WordDirective dir;

    public LDRInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public LDRInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<AddressArgument.UpdatableInteger> arg2, ParsedArgument<RegisterOrImmediate> arg3, ParsedArgument<ShiftFunction> arg4) {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<AddressArgument.UpdatableInteger>> getParsedArg2Class() {
        return AddressArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<RegisterOrImmediate>> getParsedArg3Class() {
        return PostOffsetArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<ShiftFunction>> getParsedArg4Class() {
        return ShiftArgument.class;
    }

    @Override
    public @NotNull Set<Class<? extends Enum<? extends ModifierParameter>>> getModifierParameterClasses() {
        return Set.of(UpdateFlags.class, DataMode.class, Condition.class);
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean isWorkingRegisterCompatible() {
        return false;
    }

    @Override
    public int getMemoryCode(StateContainer stateContainer, int pos) {
        if (this.modifier.dataMode() == DataMode.H || this.modifier.doUpdateFlags()) {
            return InstructionCodeUtils.singleMemoryAccessSHB(stateContainer, this, false);
        } else {
            return InstructionCodeUtils.singleMemoryAccess(stateContainer, this, false, dir, pos);
        }
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, AddressArgument.UpdatableInteger arg2, RegisterOrImmediate arg3, ShiftFunction arg4) throws ExecutionASMException {
        int i1 = arg4.apply(arg3);

        int address = isPseudoInstruction() ? dir.getLastPos().getPos() : arg2.toInt();

        if (!ignoreExceptions) {
            int dataLength;

            switch (modifier.dataMode()) {
                case H -> dataLength = 2;
                case B -> dataLength = 1;
                case null, default -> dataLength = 4;
            }

            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (modifier.dataMode()) {
            case null -> arg1.setData(stateContainer.getMemory().getWord(address));
            case H -> {
                if (this.modifier.doUpdateFlags()) {
                    arg1.setData(stateContainer.getMemory().getHalf(address));
                } else {
                    arg1.setData(Short.toUnsignedInt(stateContainer.getMemory().getHalf(address)));
                }
            }
            case B -> {
                if (this.modifier.doUpdateFlags()) {
                    arg1.setData(stateContainer.getMemory().getByte(address));
                } else {
                    arg1.setData(Byte.toUnsignedInt(stateContainer.getMemory().getByte(address)));
                }
            }
        }

        if (!isPseudoInstruction()) arg2.update(i1);
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, AddressArgument.UpdatableInteger arg2, RegisterOrImmediate arg3, ShiftFunction arg4) throws SyntaxASMException {
        arg4.check(arg3);

        if (arg3.intValue() != 0 && !arg2.canUpdate()) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.indexing"));
        }
        if (this.arg2 != null) {
            if ((this.modifier.dataMode() == DataMode.H || this.modifier.doUpdateFlags()) && ((AddressArgument) this.arg2).getMode() == AddressType.SHIFTED_REGISTER_OFFSET) {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.shifting"));
            }
        }
    }

    @Override
    public boolean isPseudoInstruction() {
        return arg2 != null && ((AddressArgument) arg2).isPseudoInstruction();
    }

    @Override
    public void allocate(StateContainer container) {
        container.getCurrentMemoryPos().incrementPos(4);
    }

    @Override
    public ParsedObject generate(StateContainer container) throws ASMException {
        Matcher matcher = PSEUDO_INS_PATTERN.matcher(arg2.getOriginalString());
        if (!matcher.find()) throw new IllegalStateException("Can't find Pseudo-Instruction");
        long value = container.evalWithAccessible(matcher.group("VALUE"));
        dir = new WordDirective(Section.RODATA, Long.toString(value));
        dir.setGenerated();
        dir.setLineNumber(this.getLineNumber());
        return dir;
    }
}
