package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.logic.ChessBoard;
import com.github.javachaos.jchess.moves.Move;

import java.util.List;
@SuppressWarnings("unused")
public class PerftUtils {

    private PerftUtils() {}

    public static long perft(int depth, ChessBoard bits, boolean printBoards) {
        if (printBoards) {
            bits.prettyPrintBoard();
        }
        List<Move> moves = bits.getAllPossibleMoves();
        int numMoves = moves.size();
        if (depth == 1) {
            return numMoves;
        }

        for (Move m : moves) {
            bits.makeMove(m);
            numMoves += perft(depth - 1, bits, printBoards);
            bits.undoMove();
        }
        return numMoves;
    }
}
