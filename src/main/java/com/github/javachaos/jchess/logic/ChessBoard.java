package com.github.javachaos.jchess.logic;

import com.github.javachaos.jchess.moves.Move;
import com.github.javachaos.jchess.utils.BitUtils;
import com.github.javachaos.jchess.utils.MoveUtils;
import com.github.javachaos.jchess.utils.PrintUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
    private final char[][] board;
    private long[] bits;
    private final Deque<long[]> boardHistory;
    private final Deque<Move> moveHistory;
    private final boolean[] castleRights = new boolean[4];
    private int halfMoveClock = 0;
    private int fullMoveClock = 0;
    private int turn = 1;//1 for white, -1 for black

    public ChessBoard(char[][] initialBoard) {
        bits = BitUtils.createBitBoard(initialBoard);
        BitUtils.clearCceo();
        BitUtils.updateBoards(bits);
        board = new char[8][8];
        this.boardHistory = new ArrayDeque<>();
        this.moveHistory = new ArrayDeque<>();
        this.castleRights[0] = true;// K
        this.castleRights[1] = true;// Q
        this.castleRights[2] = true;// k
        this.castleRights[3] = true;// q
    }

    public boolean[] getCastleRights() {
        return castleRights;
    }

    public ChessBoard() {
        this(INIT_BOARD);
    }

    public boolean makeMove(Move m) {
        boardHistory.push(bits);
        moveHistory.push(m);
        fullMoveClock++;
        halfMoveClock++;
        if (MoveUtils.doMove(bits, moveHistory.peek(), m, turn)) {
            turn *= -1;
            moveHistory.push(m);
            return true;
        } else {
            LOGGER.info("Move Invalid: {}", m);
            LOGGER.debug("Possible Moves: {}", getAllPossibleMoves());
            return false;
        }
    }
    
    public void resetHalfMoveClock() {
    	halfMoveClock = 0;
    }

    public void undoMove() {
        bits = boardHistory.pop();
        BitUtils.updateBoards(bits);
        Move m = moveHistory.pop();
        turn *= -1;
        fullMoveClock--;
    }

    public List<Move> getAllPossibleMoves() {
        return BitUtils.getAllPossibleMoves(bits, getLastMove(), turn);
    }

    public void printOccupancy() {
        BitUtils.printOccupancy();
    }

    public void printBoard() {
        PrintUtils.printBoard(bits);
    }

    public void prettyPrintBoard() {
        PrintUtils.prettyPrintBoard(bits);
    }

    public char[][] toCharArray() {
        return PrintUtils.bitsToCharArray(bits, board);
    }

    public long[] getBits() {
        return bits;
    }

    public long getOccupancy() {
        return BitUtils.infoBoards()[3];
    }

    public Move getLastMove() {
        return moveHistory.peek();
    }

    public int getTurn() {
        return turn;
    }
}