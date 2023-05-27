package com.github.javachaos.jchess.logic;

import com.github.javachaos.jchess.moves.Move;
import com.github.javachaos.jchess.utils.BitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Lightweight chess board representation, using bitboards.
 *
 * @author fred
 */
@SuppressWarnings("unused")
public class ChessBoard {
    public static final Logger LOGGER = LogManager.getLogger(ChessBoard.class);
    private static final char[][] INIT_BOARD = {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {'.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.'},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };
    private final char[][] board = new char[8][8];
    private long[] bits;
    private long occupancy = 0L;
    private final Deque<long[]> history;
    private final boolean[] castleRights = new boolean[4];
    private static final int HALFMOVECLOCK = 0;
    private int fullMoveClock = 0;

    public ChessBoard(char[][] initialBoard) {
        bits = BitUtils.createBitBoard(initialBoard);
        this.history = new ArrayDeque<>();
        this.castleRights[0] = true;// K
        this.castleRights[1] = true;// Q
        this.castleRights[2] = true;// k
        this.castleRights[3] = true;// q
        updateOccupancy();
    }

    public boolean[] getCastleRights() {
        return castleRights;
    }

    public ChessBoard() {
        this(INIT_BOARD);
    }

    public void makeMove(Move m) {
        history.push(bits);
        fullMoveClock++;
        if (BitUtils.doMove(bits, m)) {
            LOGGER.info("Move successful: {}", m);
        }
    }

    public void undoMove() {
        bits = history.pop();
        fullMoveClock--;
    }

    private void updateOccupancy() {
        for (long bit : bits) {
            occupancy |= bit;
        }
    }

    @SuppressWarnings("unused")
    public long getOccupancy() {
        return occupancy;
    }

    public void printOccupancy() {
        BitUtils.printBitboard(occupancy);
    }

    public void printBoard() {
        BitUtils.printBoard(bits);
    }

    public char[][] toCharArray() {
        return BitUtils.bitsToCharArray(bits, board);
    }

    public long[] getBits() {
        return bits;
    }
}