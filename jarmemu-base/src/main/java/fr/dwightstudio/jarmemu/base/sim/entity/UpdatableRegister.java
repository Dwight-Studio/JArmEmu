package fr.dwightstudio.jarmemu.base.sim.entity;

public class UpdatableRegister extends Register {
    private final Register register;
    private boolean update;

    public UpdatableRegister(Register register, boolean update) {
        this.register = register;
        this.update = update;
    }

    @Override
    public int getData() {
        return register.getData();
    }

    @Override
    public void setData(int data) throws IllegalArgumentException {
        register.setData(data);
    }

    @Override
    public boolean get(int index) throws IllegalArgumentException {
        return register.get(index);
    }

    @Override
    public void set(int index, boolean value) {
        register.set(index, value);
    }

    @Override
    public void add(int value) {
        register.add(value);
    }

    /**
     * Met à jour le registre en fonction du nombre de registres de l'argument RegisterArray
     */
    public void update(int value) {
        if (update) register.add(value);
        update = false;
    }
}
