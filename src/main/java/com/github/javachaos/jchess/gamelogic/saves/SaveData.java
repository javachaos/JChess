package com.github.javachaos.jchess.gamelogic.saves;

import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.SimplePiece;

import java.util.Deque;
import java.util.List;

public record SaveData(Deque<Move> undoList, Deque<Move> redoList, List<SimplePiece> captives, String fen) {}