package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.moves.Move;
import com.github.javachaos.jchess.moves.MoveScore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.javachaos.jchess.utils.MoveUtils.*;
import static com.github.javachaos.jchess.utils.PrintUtils.printBitboard;

/**
 * 0 white pawn
 * 1 white rook
 * 2 white knight
 * 3 white bishop
 * 4 white king
 * 5 white queen
 * 6 black pawn
 * 7 black rook
 * 8 black knight
 * 9 black bishop
 * 10 black king
 * 11 black queen
 */
public class BitUtils {

    private BitUtils() {}

    static final long[] FILES = {
            FILE_A,
            FILE_B,
            FILE_C,
            FILE_D,
            FILE_E,
            FILE_F,
            FILE_G,
            FILE_H
    };

    @SuppressWarnings("unused")
    static final long[] RANKS = {
            RANK_1,
            RANK_2,
            RANK_3,
            RANK_4,
            RANK_5,
            RANK_6,
            RANK_7,
            RANK_8
    };
    /**
     * Keep track of capturable black pieces.
     */
    private static long captureBlackPieces = 0L;

    /**
     * Keep track of capturable white pieces.
     */
    private static long captureWhitePieces = 0L;

    /**
     * Keep track of empty squares.
     */
    private static long empty = 0L;

    /**
     * Keep track of occupied squares.
     */
    private static long occupancy = 0L;

    /**
     * Keep track of attacked king squares (checks)
     */
    private static long checkBoardWhite = 0L;

    /**
     * Keep track of attacked king squares (checks)
     */
    private static long checkBoardBlack = 0L;

    /**
     * Track location of all white pieces
     */
    private static long colorBoardWhite = 0L;

    /**
     * Track location of all black pieces
     */
    private static long colorBoardBlack = 0L;

    static Move lastMove;

    public static long[] infoBoards() {
        return new long[] {captureWhitePieces, captureBlackPieces, empty, occupancy,
        checkBoardBlack, checkBoardWhite, colorBoardBlack, colorBoardWhite};
    }

    public static void updateWhites(long[] bits) {
    	captureWhitePieces = bits[0] | bits[1] | bits[2] | bits[3] | bits[5];
        colorBoardWhite = captureWhitePieces | bits[4];
    }

    public static void updateBlacks(long[] bits) {
    	captureBlackPieces = bits[11] | bits[9] | bits[8] | bits[7] | bits[6];
        colorBoardBlack = captureBlackPieces |  bits[10];
    }

    public static void updateEmpty(long[] bits) {
        empty = ~(bits[0]|bits[1]|bits[2]|bits[3]|bits[4]|bits[5]|bits[6]|bits[7]|bits[8]|bits[9]|bits[10]|bits[11]);
    }

    public static void updateOccupancy(long[] bits) {
        for (long bit : bits) {
            occupancy |= bit;
        }
    }

    public static void clearCceo() {
        captureBlackPieces = 0L;
        captureWhitePieces = 0L;
        empty = 0L;
        occupancy = 0L;
    }

    public static void updateBoards(long[] bits) {
        updateOccupancy(bits);
        updateWhites(bits);
        updateBlacks(bits);
        updateEmpty(bits);
        updateCheckBoard(bits, lastMove);
    }

    private static void updateCheckBoard(long[] bits, Move lastMove) {
        for (int i = 0; i < NUM_PIECES; i++) {
            checkBoardWhite |= getAttackingSquares(bits, MoveUtils.indexToPiece(i), lastMove) & bits[4];
            checkBoardBlack |= getAttackingSquares(bits, MoveUtils.indexToPiece(i), lastMove) & bits[10];
        }
    }

    private static long getAttackingSquares(long[] bits, char type, Move lastMove) {
        switch(type) {
            case 'P':
                return pawnAttacksBlack(bits, lastMove);
            case 'p':
                return pawnAttacksWhite(bits, lastMove);
            default: return 0;
        }
    }

    /**
     * Piece Index
     *  0 white pawn
     *  1 white rook
     *  2 white knight
     *  3 white bishop
     *  4 white king
     *  5 white queen
     *  6 black pawn
     *  7 black rook
     *  8 black knight
     *  9 black bishop
     *  10 black king
     *  11 black queen
     * @param bits the chessboard bitboards
     * @return list of pawn moves for black
     */
    public static List<Move> pawnMovesBlack(long[] bits, Move lastMove) {
        List<Move> moves = new ArrayList<>(64);

        long moveBitsRight =    (bits[6] << 7)  & captureWhitePieces & NOT_RANK_1 & NOT_A_FILE;
        long moveBitsLeft =     (bits[6] << 9)  & captureWhitePieces & NOT_RANK_1 & NOT_H_FILE;
        long moveBitsOneAhead = (bits[6] << 8)  & empty & NOT_RANK_1;
        long moveBitsTwoAhead = (bits[6] << 16) & empty & (empty << 8) & RANK_5;
        long moveBitsRightP =   (bits[6] << 7)  & captureWhitePieces & RANK_1 & NOT_A_FILE;
        long moveBitsLeftP =    (bits[6] << 9)  & captureWhitePieces & RANK_1 & NOT_H_FILE;
        long moveBitsP =        (bits[6] << 8)  & empty & RANK_1;

        //If the last move was a 2-square move.
        if (lastMove != null && lastMove.to().file() == lastMove.from().file()//same file
                && Math.abs(lastMove.to().rank() - lastMove.from().rank()) == 2//last move was a 2 move advance
                && MoveUtils.getPieceChar(bits, lastMove) == 'P'
        ) {
        	int file = 'h' - lastMove.from().file();
            long enpassantRight = (bits[6] >> 1) & bits[0] & RANK_4 & NOT_A_FILE & FILES[file];
            long enpassantLeft =  (bits[6] << 1) & bits[0] & RANK_4 & NOT_H_FILE & FILES[file];
            if (enpassantRight != 0 || enpassantLeft != 0) {
            	enpassantLeft  <<= 8;
            	enpassantRight <<= 8;
                processMoveBits(enpassantRight, '.', -1, 1, moves);
	            processMoveBits(enpassantLeft, '.', -1, -1, moves);
            }
        }

        processMoveBits(moveBitsRight, '.', -1, 1, moves);
        processMoveBits(moveBitsLeft, '.', -1, -1, moves);
        processMoveBits(moveBitsOneAhead, '.', -1, 0, moves);
        processMoveBits(moveBitsTwoAhead, '.', -2, 0, moves);
        processMoveBits(moveBitsRightP, 'Q', -1, 1, moves);
        processMoveBits(moveBitsRightP, 'R', -1, 1, moves);
        processMoveBits(moveBitsRightP, 'B', -1, 1, moves);
        processMoveBits(moveBitsRightP, 'N', -1, 1, moves);
        processMoveBits(moveBitsLeftP, 'Q', -1, 0, moves);
        processMoveBits(moveBitsLeftP, 'R', -1, 0, moves);
        processMoveBits(moveBitsLeftP, 'B', -1, 0, moves);
        processMoveBits(moveBitsLeftP, 'N', -1, 0, moves);
        processMoveBits(moveBitsP, 'Q', 0, 0, moves);
        processMoveBits(moveBitsP, 'R', 0, 0, moves);
        processMoveBits(moveBitsP, 'B', 0, 0, moves);
        processMoveBits(moveBitsP, 'N', 0, 0, moves);

        return moves;
    }

    @SuppressWarnings("unused")
    public static long pawnAttacksBlack(long[] bits, Move lastMove) {

        long moveBitsRight =    (bits[6] << 7)  & captureWhitePieces & NOT_RANK_1 & NOT_A_FILE;
        long moveBitsLeft =     (bits[6] << 9)  & captureWhitePieces & NOT_RANK_1 & NOT_H_FILE;
        long moveBitsOneAhead = (bits[6] << 8)  & empty & NOT_RANK_1;
        long moveBitsTwoAhead = (bits[6] << 16) & empty & (empty << 8) & RANK_5;
        long moveBitsRightP =   (bits[6] << 7)  & captureWhitePieces & RANK_1 & NOT_A_FILE;
        long moveBitsLeftP =    (bits[6] << 9)  & captureWhitePieces & RANK_1 & NOT_H_FILE;
        long moveBitsP =        (bits[6] << 8)  & empty & RANK_1;

        long moveOccupancy = moveBitsRight | moveBitsLeft
                | moveBitsOneAhead | moveBitsTwoAhead | moveBitsRightP | moveBitsLeftP
                | moveBitsP;

        //If the last move was a 2-square move.
        if (lastMove != null && lastMove.to().file() == lastMove.from().file()//same file
                && Math.abs(lastMove.to().rank() - lastMove.from().rank()) == 2 &&
            getPieceChar(bits, lastMove) == 'P') {
            int file = 'h' - lastMove.from().file();
            long enpassantRight = (bits[6] >> 1) & bits[0] & RANK_4 & NOT_A_FILE & FILES[file];
            long enpassantLeft =  (bits[6] << 1) & bits[0] & RANK_4 & NOT_H_FILE & FILES[file];
            if (enpassantRight != 0 || enpassantLeft != 0) {
                enpassantLeft  <<= 8;
                enpassantRight <<= 8;
                moveOccupancy |= enpassantRight | enpassantLeft;
            }
        }
        return moveOccupancy << 9 | moveOccupancy >> 7;
    }

    public static List<Move> pawnMovesWhite(long[] bits, Move lastMove) {
        List<Move> moves = new ArrayList<>(64);
        long moveBitsRight =    (bits[0] >> 7)  & captureBlackPieces & NOT_RANK_8 & NOT_H_FILE;
        long moveBitsLeft =     (bits[0] >> 9)  & captureBlackPieces & NOT_RANK_8 & NOT_A_FILE;
        long moveBitsOneAhead = (bits[0] >> 8)  & empty & NOT_RANK_8;
        long moveBitsTwoAhead = (bits[0] >> 16) & empty & (empty >> 8) & RANK_4;
        long moveBitsRightP =   (bits[0] >> 7)  & captureBlackPieces & RANK_8 & NOT_H_FILE;
        long moveBitsLeftP =    (bits[0] >> 9)  & captureBlackPieces & RANK_8 & NOT_A_FILE;
        long moveBitsP =        (bits[0] >> 8)  & empty & RANK_8;

        //If the last move was a 2-square move.
        if (lastMove != null && lastMove.to().file() == lastMove.from().file()//same file
                && Math.abs(lastMove.to().rank() - lastMove.from().rank()) == 2//last move was a 2 move advance
                && getPieceChar(bits, lastMove) == 'p'
        ) {
            int file = 'h' - lastMove.from().file();
            long enpassantRight = (bits[0] << 1) & bits[6] & RANK_5 & NOT_A_FILE & FILES[file];
            long enpassantLeft =  (bits[0] >> 1) & bits[6] & RANK_5 & NOT_H_FILE & FILES[file];
            if (enpassantRight != 0 || enpassantLeft != 0) {
                enpassantLeft  >>= 8;
                enpassantRight >>= 8;
                processMoveBits(enpassantRight, '.', 1, -1, moves);
                processMoveBits(enpassantLeft, '.', 1, 1, moves);
            }
        }
        processMoveBits(moveBitsRight, '.', 1, -1, moves);
        processMoveBits(moveBitsLeft, '.', 1, 1, moves);
        processMoveBits(moveBitsOneAhead, '.', 1, 0, moves);
        processMoveBits(moveBitsTwoAhead, '.', 2, 0, moves);
        processMoveBits(moveBitsRightP, 'Q', 1, -1, moves);
        processMoveBits(moveBitsRightP, 'R', 1, -1, moves);
        processMoveBits(moveBitsRightP, 'B', 1, -1, moves);
        processMoveBits(moveBitsRightP, 'N', 1, -1, moves);
        processMoveBits(moveBitsLeftP, 'Q', 1, 0, moves);
        processMoveBits(moveBitsLeftP, 'R', 1, 0, moves);
        processMoveBits(moveBitsLeftP, 'B', 1, 0, moves);
        processMoveBits(moveBitsLeftP, 'N', 1, 0, moves);
        processMoveBits(moveBitsP, 'Q', 0, 0, moves);
        processMoveBits(moveBitsP, 'R', 0, 0, moves);
        processMoveBits(moveBitsP, 'B', 0, 0, moves);
        processMoveBits(moveBitsP, 'N', 0, 0, moves);
        return moves;
    }

    private static long pawnAttacksWhite(long[] bits, Move lastMove) {
        long moveBitsRight =    (bits[0] >> 7)  & captureBlackPieces & NOT_RANK_8 & NOT_A_FILE;
        long moveBitsLeft =     (bits[0] >> 9)  & captureBlackPieces & NOT_RANK_8 & NOT_H_FILE;
        long moveBitsOneAhead = (bits[0] >> 8)  & empty & NOT_RANK_8;
        long moveBitsTwoAhead = (bits[0] >> 16) & empty & (empty >> 8) & RANK_4;
        long moveBitsRightP =   (bits[0] >> 7)  & captureBlackPieces & RANK_8 & NOT_A_FILE;
        long moveBitsLeftP =    (bits[0] >> 9)  & captureBlackPieces & RANK_8 & NOT_H_FILE;
        long moveBitsP =        (bits[0] >> 8)  & empty & RANK_8;

        long moveOccupancy = moveBitsRight | moveBitsLeft
                | moveBitsOneAhead | moveBitsTwoAhead | moveBitsRightP | moveBitsLeftP
                | moveBitsP;

        //If the last move was a 2-square move.
        if (lastMove != null && lastMove.to().file() == lastMove.from().file()//same file
                && Math.abs(lastMove.to().rank() - lastMove.from().rank()) == 2 &&
                getPieceChar(bits, lastMove) == 'p') {
            int file = 'h' - lastMove.from().file();
            long enpassantRight = (bits[0] << 1) & bits[6] & RANK_5 & NOT_A_FILE & FILES[file];
            long enpassantLeft =  (bits[0] >> 1) & bits[6] & RANK_5 & NOT_H_FILE & FILES[file];
            if (enpassantRight != 0 || enpassantLeft != 0) {
                enpassantLeft  >>= 8;
                enpassantRight >>= 8;
                moveOccupancy |= enpassantRight | enpassantLeft;
            }
        }
        return moveOccupancy << 9 | moveOccupancy >> 7;
    }

    /**
     * Process a 64-bit string of bits (moveBits) one by one
     * starting at the first set bit and looping until we reach
     * the last set bit.
     * At each set bit we add the move to the array moves at i.
     * this should be fairly efficient but not optimal.
     * To achieve 100% optimal code we would need to create a
     * fairly large lookup table for every single piece storing
     * all bitmasks for each of the 64 possible positions for each piece,
     * for our application this would be impractical.
     * <p>
     * (marked final to encourage JVM inlining)
     *
     * @param moveBits bitboard of valid moves
     * @param promotion char of possible promotion
     * @param rowOffset row index
     * @param colOffset column index
     * @param moves array of moves to add to
     *
     */
    private static void processMoveBits(long moveBits, char promotion, int rowOffset, int colOffset, List<Move> moves) {
        while (moveBits != 0) {
            long bit = moveBits & -moveBits;  // Get the least significant set bit
            int index = Long.numberOfTrailingZeros(bit);
            moveBits ^= bit;  // Clear the least significant set bit
            moves.add(new Move(
                    indexToPos(index / 8 + rowOffset, index % 8 + colOffset,
                            index + (rowOffset * 8) + colOffset),
                    indexToPos(index / 8, index % 8, index),
                    promotion
            ));
        }
    }

    public static long[] createBitBoard(char[][] cb) {
        long[] bits = new long[]{
                0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L
        };

        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            char piece = cb[i / BOARD_SIZE][i % BOARD_SIZE];
            if (piece != '.') {
                long bin = 1L << i;
                int index = PrintUtils.getPieceIndex(piece);
                bits[index] |= bin;
            }
        }
        updateBoards(bits);
        return bits;
    }

    /**
     * Faster than {@code Long.bitCount(x) % 2 == 0}
     * @param x the value to test
     * @return true if the value x has an odd number of set bits
     */
    public static boolean isOddParity(long x) {
        x ^= x <<  1;
        x ^= x <<  2;
        x ^= x <<  4;
        x ^= x <<  8;
        x ^= x << 16;
        x ^= x << 32;
        return x < 0;
    }

    public static List<Move> getAllPossibleMoves(long[] bits, Move lastMove, int turn) {
        List<Move> moveList;
        if (turn == 1) {
            moveList = pawnMovesWhite(bits, lastMove);
            //add the rest of the piece types
        } else {
            moveList = pawnMovesBlack(bits, lastMove);
        }
        return moveList;
    }

    public static List<Move> getAllCaptures(long[] bits, int turn) {
        List<Move> moveList = getAllPossibleMoves(bits, lastMove, turn);
        //implement
        if (turn == -1) {
            moveList.add(Move.fromString("a1a2"));
        }
        if (turn == 1) {
            moveList.add(Move.fromString("a1a2"));
        }
        return Collections.emptyList();
    }

    static void updateBoards(long[] bits, Move m) {
        int piece = getPiece(bits, m);
        int captive = getCapture(bits, m);
        long fromBB = m.fromBitboard();
        long toBB = m.toBitboard();
        long fromToBB = fromBB ^ toBB;
        if (piece > -1) {
            bits[piece] ^= fromToBB;
            if (captive > -1) {
                bits[captive] ^= toBB;
            }
            occupancy ^= m.fromBitboard();
            empty ^= m.fromBitboard();
        }
        updateBoards(bits);
    }

    /**
     * Given a move, get the piece on the board the to square.
     * -1 if there is no piece.
     * @param m the move
     * @return piece index [0-11]
     */
    @SuppressWarnings("unused")
    private static int getCapture(long[] bits, Move m) {
        //implement/test
        return -1;
    }

    public static MoveScore quiesce(List<Move> captures, long[] bits, int turn, int a, int b) {
        int s = evaluation(bits, turn);
        if (s >= b) {
            return new MoveScore(captures.get(0), b);
        }
        if (a < s) {
            a = s;
        }
        MoveScore ms = null;
        for (Move m : captures) {
            doMove(bits, m, lastMove, turn);
            lastMove = m;
            int qScore = -quiesce(captures.subList(captures.indexOf(m), captures.indexOf(m) + 1), bits, turn, -a, -b).score();
            undoMove(bits, m);
            if (qScore >= b) {
                return new MoveScore(m, b);
            }
            if (qScore > a) {
                a = qScore;
                ms = new MoveScore(m, a);
            }
        }
        return ms;
    }

    public static int evaluation(long[] bits, int turn) {
        int numWhitePieces = 0;
        int numBlackPieces = 0;

        // Count the number of white and black pieces
        for (int i = 0; i < bits.length; i++) {
            long bitboard = bits[i];
            int count = Long.bitCount(bitboard);

            if (i <= 5) {
                // White pieces
                numWhitePieces += count;
            } else {
                // Black pieces
                numBlackPieces += count;
            }
        }
        int materialWeight = 1; // Assign a weight to the material (you can adjust this as needed)
        return materialWeight * (numWhitePieces - numBlackPieces) * turn;
    }

    @SuppressWarnings("unused")
    public static MoveScore getBestMove(long[] bits, int turn) {
        return negamaxABHelper(Arrays.copyOf(bits, bits.length), turn, Integer.MIN_VALUE, Integer.MAX_VALUE, 10);
    }

    /**
     * Simple negamax implementation.
     * Must test this!
     *
     * @param bits board state
     * @param turn who's turn, -1 for black 1 for white
     * @param a alpha
     * @param b beta
     * @param depth tree depth
     * @return a move score object which is a tuple of Move and int (java record) {@link MoveScore}
     */
    public static MoveScore negamaxABHelper(long[] bits, int turn, int a, int b, int depth) {
        int score;
        if (depth == 0) {
            return quiesce(getAllCaptures(bits, turn), bits, turn, a, b);
        }
        MoveScore ms = null;
        for (Move m : getAllPossibleMoves(bits, lastMove, turn)) {
            score = -negamaxABHelper(bits, -turn, -b, -a, depth - 1).score();
            if (score >= b) {
                return new MoveScore(m, b);
            }
            if (score > a) {
                a = score;
                ms = new MoveScore(m, a);
            }
        }
        return ms;
    }

    public static void printOccupancy() {
        printBitboard(occupancy);
    }
}
