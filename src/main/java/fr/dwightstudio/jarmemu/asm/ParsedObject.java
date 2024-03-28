package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedObject {

    protected static final boolean VERBOSE = true;

    private ParsedFile file;
    private int lineNumber;
    private boolean generated;

    /**
     * Effectue un test pour vérifier la capacité d'exécution
     *
     * @param stateSupplier le fournisseur d'état de base (état pré-exécution)
     * @throws ASMException lorsque une erreur est détectée
     */
    public abstract void verify(Supplier<StateContainer> stateSupplier) throws ASMException;

    public ParsedFile getFile() {
        return file;
    }

    public void setFile(ParsedFile file) {
        this.file = file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return la position dans le programme (fichier et ligne)
     */
    public FilePos getFilePos() {
        return new FilePos(file.getIndex(), lineNumber).freeze();
    }

    /**
     * @return vrai si l'objet a été généré
     */
    public boolean isGenerated() {
        return generated;
    }

    /**
     * Définie l'objet comme généré
     */
    public void setGenerated() {
        this.generated = true;
    }
}
