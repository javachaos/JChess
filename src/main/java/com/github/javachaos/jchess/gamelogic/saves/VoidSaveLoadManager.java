package com.github.javachaos.jchess.gamelogic.saves;

public class VoidSaveLoadManager implements ISaveLoader {
    @Override
    public void save() {
        //NO-OP
    }

    @Override
    public UndoData load() {
        //NO-OP
        return null;
    }
}
