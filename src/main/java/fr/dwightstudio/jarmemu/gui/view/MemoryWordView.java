package fr.dwightstudio.jarmemu.gui.view;

import fr.dwightstudio.jarmemu.sim.obj.MemoryAccessor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;

public class MemoryWordView {
    private final MemoryAccessor memoryAccessor;
    private final ReadOnlyIntegerProperty addressProperty;
    private final IntegerProperty valueProperty;
    private final IntegerProperty byte0;
    private final IntegerProperty byte1;
    private final IntegerProperty byte2;
    private final IntegerProperty byte3;

    public MemoryWordView(MemoryAccessor memoryAccessor, int address) {
        this.memoryAccessor = memoryAccessor;
        this.addressProperty = new ReadOnlyIntegerWrapper(address);
        this.valueProperty = memoryAccessor.getProperty(address);
        this.byte0 = new SimpleIntegerProperty(0);
        this.byte1 = new SimpleIntegerProperty(0);
        this.byte2 = new SimpleIntegerProperty(0);
        this.byte3 = new SimpleIntegerProperty(0);
    }
}
