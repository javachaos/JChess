package com.github.javachaos.jchess.gamelogic.saves;

public interface ISaveLoader {
    void save();
    SaveData load();
    void setSaveData(SaveData saveData);
}
