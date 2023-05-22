package com.github.javachaos.jchess.gamelogic.saves;

import com.github.javachaos.jchess.gamelogic.pieces.core.Move;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoData {
    private final Deque<Move> undoList;

    private final Deque<Move> redoList;

    public UndoData() {
        this.undoList = new ArrayDeque<>();
        this.redoList = new ArrayDeque<>();
    }

    public UndoData(Deque<Move> undoList, Deque<Move> redoList) {
        this.undoList = undoList;
        this.redoList = redoList;
    }

    public Deque<Move> getUndoList() {
        return undoList;
    }

    public Deque<Move> getRedoList() {
        return redoList;
    }

}
