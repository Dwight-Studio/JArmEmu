package fr.dwightstudio.jarmemu.asm;

import java.util.ArrayList;

public class CodeScanner {

    private int currentInstructionValue;
    private ArrayList<String> code;

    public CodeScanner(ArrayList<String> code){
        this.code = code;
        this.currentInstructionValue = -1;
    }

    public String nextLine(){
        this.currentInstructionValue++;
        return this.code.get(this.currentInstructionValue);
    }

    public boolean hasNextLine(){
        return this.currentInstructionValue == this.code.size() - 1;
    }

    public void goTo(int lineNb){
        this.currentInstructionValue = lineNb;
    }

    public String goToValue(int lineNb){
        this.currentInstructionValue = lineNb;
        return this.code.get(this.currentInstructionValue);
    }

}
