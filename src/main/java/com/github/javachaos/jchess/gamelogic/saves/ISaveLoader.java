package com.github.javachaos.jchess.gamelogic.saves;

public interface ISaveLoader {
    void save();
    UndoData load();
}
