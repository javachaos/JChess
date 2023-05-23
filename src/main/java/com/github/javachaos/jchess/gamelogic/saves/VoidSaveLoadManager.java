package com.github.javachaos.jchess.gamelogic.saves;

public class VoidSaveLoadManager implements ISaveLoader {
    @Override
    public void save() {
        //NO-OP
    }

    @Override
    public SaveData load() {
        //NO-OP
        return null;
    }

    @Override
    public void setSaveData(SaveData saveData) {
        //UNUSED
    }
}
