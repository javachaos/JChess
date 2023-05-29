package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.moves.Move;

import java.util.List;
@SuppressWarnings("unused")
public class PerftUtils {
    private static int isWhitesTurn = 1;

    private PerftUtils() {}

    public static long perft(int depth, long[] bits) {
        int numMoves = 0;
        Move lastMove = null;
        List<Move> moves = BitUtils.getAllPossibleMoves(bits, lastMove, isWhitesTurn);

        if (depth == 1) {
            return numMoves;
        }

        for (Move m : moves) {
            BitUtils.doMove(bits, m, lastMove, isWhitesTurn);
            isWhitesTurn *= -1;
            lastMove = m;
            numMoves += perft(depth - 1, bits);
            BitUtils.undoMove(bits, m);
        }
        return numMoves;
    }
}
