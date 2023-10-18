package fr.dwightstudio.jarmemu.sim;

public class PSR extends Register {

    public void setN(boolean value) {
        this.set(31, value);
    }

    public boolean getN() {
        return this.get(31);
    }

    public void setZ(boolean value) {
        this.set(30, value);
    }

    public boolean getZ() {
        return this.get(30);
    }

    public void setC(boolean value) {
        this.set(29, value);
    }

    public boolean getC() {
        return this.get(29);
    }

    public void setV(boolean value) {
        this.set(28, value);
    }

    public boolean getV() {
        return this.get(28);
    }

    public void setI(boolean value) {
        this.set(7, value);
    }

    public boolean getI() {
        return this.get(7);
    }

    public void setF(boolean value) {
        this.set(6, value);
    }

    public boolean getF() {
        return this.get(6);
    }

    public void setT(boolean value) {
        this.set(5, value);
    }
    public boolean getT() {
        return this.get(5);
    }

}
