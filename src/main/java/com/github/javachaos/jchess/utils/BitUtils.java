package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.moves.Move;
import com.github.javachaos.jchess.moves.Pos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_LIST;

@SuppressWarnings("all")
public class BitUtils {
    public static final Logger LOGGER = LogManager.getLogger(BitUtils.class);

    private BitUtils() {}

    private static final int BOARD_SIZE = 8;
    private static final long RANK_1  = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long RANK_2  = 0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long RANK_3  = 0b00000000_00000000_11111111_00000000_00000000_00000000_00000000_00000000L;
    private static final long RANK_4  = 0b00000000_00000000_00000000_11111111_00000000_00000000_00000000_00000000L;
    private static final long RANK_5  = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
    private static final long RANK_6  = 0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L;
    private static final long RANK_7  = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000L;
    private static final long RANK_8  = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_11111111L;
    private static final long FILE_A  = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    private static final long FILE_B  = 0b01000000_01000000_01000000_01000000_01000000_01000000_01000000_01000000L;
    private static final long FILE_C  = 0b00100000_00100000_00100000_00100000_00100000_00100000_00100000_00100000L;
    private static final long FILE_D  = 0b00010000_00010000_00010000_00010000_00010000_00010000_00010000_00010000L;
    private static final long FILE_E  = 0b00001000_00001000_00001000_00001000_00001000_00001000_00001000_00001000L;
    private static final long FILE_F  = 0b00000100_00000100_00000100_00000100_00000100_00000100_00000100_00000100L;
    private static final long FILE_G  = 0b00000010_00000010_00000010_00000010_00000010_00000010_00000010_00000010L;
    private static final long FILE_H  = 0b00000001_00000001_00000001_00000001_00000001_00000001_00000001_00000001L;
    private static final long FILE_AB = 0b11000000_11000000_11000000_11000000_11000000_11000000_11000000_11000000L;
    private static final long FILE_GH = 0b00000011_00000011_00000011_00000011_00000011_00000011_00000011_00000011L;
    private static final long KING_SIDE = 0b00001111_00001111_00001111_00001111_00001111_00001111_00001111_00001111L;
    private static final long QUEEN_SIDE = 0b11110000_11110000_11110000_11110000_11110000_11110000_11110000_11110000L;
    private static final long NOT_A_FILE = ~FILE_A;
    private static final long NOT_H_FILE = ~FILE_H;
    private static final long NOT_RANK_8 = ~RANK_8;
    private static final long NOT_RANK_1 = ~RANK_1;
    private static long[] FileMasks = {
        FILE_A,
        FILE_B,
        FILE_C,
        FILE_D,
        FILE_E,
        FILE_F,
        FILE_G,
        FILE_H
    };

    private static long[] RankMasks = {
    	RANK_1,
    	RANK_2,
    	RANK_3,
    	RANK_4,
    	RANK_5,
    	RANK_6,
    	RANK_7,
    	RANK_8
    };
    private static long captureBlackPieces = 0L;
    private static long captureWhitePieces = 0L;
    private static long empty = 0L;
    private static long occupancy = 0L;

    public static void clearCceo() {
        captureBlackPieces = 0L;
        captureWhitePieces = 0L;
        empty = 0L;
        occupancy = 0L;
    }
    public static long[] infoBoards() {
        return new long[] {captureWhitePieces, captureBlackPieces, empty, occupancy};
    }
    
    public static long getCaptureWhitePieces() {
    	return captureWhitePieces;
    }
    
    public static long getCaptureBlackPieces() {
    	return captureBlackPieces;
    }
    
    public static void updateWhites(long[] bits) {
    	captureWhitePieces = bits[0] | bits[1] | bits[2] | bits[3] | bits[5];
    }
    
    public static void updateBlacks(long[] bits) {
    	captureBlackPieces = bits[11] | bits[9] | bits[8] | bits[7] | bits[6];
    }

    public static void updateEmpty(long[] bits) {
        empty = ~(bits[0]|bits[1]|bits[2]|bits[3]|bits[4]|bits[5]|bits[6]|bits[7]|bits[8]|bits[9]|bits[10]|bits[11]);
    }

    public static void updateOccupancy(Move m) {
        occupancy ^= m.toBitboard();
        occupancy |= m.fromBitboard();
    }

    public static void updateOccupancy(long[] bits) {
        for (long bit : bits) {
            occupancy |= bit;
        }
    }

    /**
     * Remove empty squares from the empty bitboard, any bit set it both
     * squares and empty will be zero'd. (XOR)
     * Much faster than updating with the entire set of bit boards. (~10x faster)
     *
     * @param squares bitboard of squares to set empty
     */
    public static void updateEmpty(Move m) {
        empty ^= m.fromBitboard();
    }

    public static char[][] occupancyToCharArray(long occupancy) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < 64; i++) {
            cb[i / BOARD_SIZE][i % BOARD_SIZE] = '.';
        }
        if (Long.bitCount(occupancy) > 0) {
            //in the case that bitCount uses 1 machine instruction this could be faster.
            int start = Long.numberOfTrailingZeros(occupancy);
            int end = Long.numberOfLeadingZeros(occupancy);
            for (int i = start; i < 64 - end; i++) {
                if ((occupancy & (1L << i)) != 0) {
                    cb[i/BOARD_SIZE][i%BOARD_SIZE] = '@';
                }
            }
        }
        return cb;
    }

    public static void updateBoards(long[] bits) {
        updateWhites(bits);
        updateBlacks(bits);
        updateEmpty(bits);
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
     * @param bits
     * @param movesList
     * @return
     */
    public static List<Move> pawnMovesBlack(long[] bits, Move lastMove) {
        Move[] moves = new Move[64];

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
        if (Math.abs(lastMove.to().rank() - lastMove.from().rank()) == 2) {
        	int file = 'h' - lastMove.from().file();
            long enpassantRight = (bits[6] >> 1) & bits[0] & RANK_4 & NOT_A_FILE & FileMasks[file];
            long enpassantLeft =  (bits[6] << 1) & bits[0] & RANK_4 & NOT_H_FILE & FileMasks[file];
            if (enpassantRight != 0 || enpassantLeft != 0) {
            	enpassantLeft  <<= 8;
            	enpassantRight <<= 8;
	            moveOccupancy |= enpassantRight | enpassantLeft;
	            moves = processMoveBits(enpassantRight, '.', -1, 1, moves);
	            moves = processMoveBits(enpassantLeft, '.', -1, -1, moves);
            }
        }

        moves = processMoveBits(moveBitsRight, '.', -1, 1, moves);
        moves = processMoveBits(moveBitsLeft, '.', -1, -1, moves);
        moves = processMoveBits(moveBitsOneAhead, '.', -1, 0, moves);
        moves = processMoveBits(moveBitsTwoAhead, '.', -2, 0, moves);
        moves = processMoveBits(moveBitsRightP, 'Q', -1, 1, moves);
        moves = processMoveBits(moveBitsRightP, 'R', -1, 1, moves);
        moves = processMoveBits(moveBitsRightP, 'B', -1, 1, moves);
        moves = processMoveBits(moveBitsRightP, 'N', -1, 1, moves);
        moves = processMoveBits(moveBitsLeftP, 'Q', -1, 0, moves);
        moves = processMoveBits(moveBitsLeftP, 'R', -1, 0, moves);
        moves = processMoveBits(moveBitsLeftP, 'B', -1, 0, moves);
        moves = processMoveBits(moveBitsLeftP, 'N', -1, 0, moves);
        moves = processMoveBits(moveBitsP, 'Q', 0, 0, moves);
        moves = processMoveBits(moveBitsP, 'R', 0, 0, moves);
        moves = processMoveBits(moveBitsP, 'B', 0, 0, moves);
        moves = processMoveBits(moveBitsP, 'N', 0, 0, moves);

        return getMoves(moves, moveOccupancy);
    }

    public static List<Move> pawnMovesWhite(long[] bits) {
        Move[] moves = new Move[64];
        long moveBitsRight =    (bits[0] >> 7)  & captureBlackPieces & NOT_RANK_8 & NOT_A_FILE;
        long moveBitsLeft =     (bits[0] >> 9)  & captureBlackPieces & NOT_RANK_8 & NOT_H_FILE;
        long moveBitsOneAhead = (bits[0] >> 8)  & empty & NOT_RANK_8;
        long moveBitsTwoAhead = (bits[0] >> 16) & empty & (empty >> 8) & RANK_4;
        long moveBitsRightP =   (bits[0] >> 7)  & captureBlackPieces & RANK_8 & NOT_A_FILE;
        long moveBitsLeftP =    (bits[0] >> 9)  & captureBlackPieces & RANK_8 & NOT_H_FILE;
        long moveBitsP =        (bits[0] >> 8)  & empty & RANK_8;

        long moveOccupancy = moveBitsRight | moveBitsLeft
                        | moveBitsOneAhead | moveBitsTwoAhead
                          | moveBitsRightP | moveBitsLeftP
                                           | moveBitsP;
        moves = processMoveBits(moveBitsRight, '.', 1, -1, moves);
        moves = processMoveBits(moveBitsLeft, '.', 1, 1, moves);
        moves = processMoveBits(moveBitsOneAhead, '.', 1, 0, moves);
        moves = processMoveBits(moveBitsTwoAhead, '.', 2, 0, moves);
        moves = processMoveBits(moveBitsRightP, 'Q', 1, -1, moves);
        moves = processMoveBits(moveBitsRightP, 'R', 1, -1, moves);
        moves = processMoveBits(moveBitsRightP, 'B', 1, -1, moves);
        moves = processMoveBits(moveBitsRightP, 'N', 1, -1, moves);
        moves = processMoveBits(moveBitsLeftP, 'Q', 1, 0, moves);
        moves = processMoveBits(moveBitsLeftP, 'R', 1, 0, moves);
        moves = processMoveBits(moveBitsLeftP, 'B', 1, 0, moves);
        moves = processMoveBits(moveBitsLeftP, 'N', 1, 0, moves);
        moves = processMoveBits(moveBitsP, 'Q', 0, 0, moves);
        moves = processMoveBits(moveBitsP, 'R', 0, 0, moves);
        moves = processMoveBits(moveBitsP, 'B', 0, 0, moves);
        moves = processMoveBits(moveBitsP, 'N', 0, 0, moves);
        return getMoves(moves, moveOccupancy);
    }

    /**
     * Given an array of moves and the moveOccupancy bitboard
     * return a list of all non-null moves from move array moves.
     * All 1 bits in moveOccupancy correspond to index's of the moves
     * in moves.
     *
     * (marked final to encourage JVM inlining)
     * @param moves
     * @param moveOccupancy
     * @return
     */
    private static final List<Move> getMoves(Move[] moves, long moveOccupancy) {
        int numMoves = Long.bitCount(moveOccupancy);
        if (numMoves > 0) {
            List<Move> moveList = new ArrayList<>();
            int s = Long.numberOfTrailingZeros(moveOccupancy);
            int e = Long.numberOfLeadingZeros(moveOccupancy);
            for (int i = s; i < 64 - e; i++) {
                if ((moveOccupancy & (1L << i)) != 0) {
                    moveList.add(moves[i]);
                }
            }
            return moveList;
        }
        return EMPTY_LIST;
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
     *
     * (marked final to encourage JVM inlining)
     *
     * @param moveBits
     * @param promotion
     * @param rowOffset
     * @param colOffset
     * @param moves
     * @return
     */
    private static final Move[] processMoveBits(long moveBits, char promotion, int rowOffset, int colOffset, Move[] moves) {
        while (moveBits != 0) {
            long bit = moveBits & -moveBits;  // Get the least significant set bit
            int index = Long.numberOfTrailingZeros(bit);
            moveBits ^= bit;  // Clear the least significant set bit
            moves[index] = new Move(
                    indexToPos(index / 8 + rowOffset, index % 8 + colOffset, index),
                    indexToPos(index / 8, index % 8, index),
                    promotion
            );
        }
        return moves;
    }

    public static long[] createBitBoard(char[][] cb) {
        long[] bits = new long[]{
                0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L
        };

        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            char piece = cb[i / BOARD_SIZE][i % BOARD_SIZE];
            if (piece != '.') {
                long bin = 1L << i;
                int index = getPieceIndex(piece);
                bits[index] |= bin;
            }
        }
        updateWhites(bits);
        updateBlacks(bits);
        return bits;
    }

    private static int getPieceIndex(char piece) {
       return switch (piece) {
            case 'P' -> 0;
            case 'R' -> 1;
            case 'N' -> 2;
            case 'B' -> 3;
            case 'K' -> 4;
            case 'Q' -> 5;
            case 'p' -> 6;
            case 'r' -> 7;
            case 'n' -> 8;
            case 'b' -> 9;
            case 'k' -> 10;
            case 'q' -> 11;
           default -> throw new IllegalStateException("Unexpected value: " + piece);
       };
    }

    public static long clearBit(long number, int bitIndex) {
        long mask = ~(1L << bitIndex);
        return number & mask;
    }

    public static long setBit(long number, int bitIndex) {
        long mask = 1L << bitIndex;
        return number | mask;
    }

    public static void printBoard(long[] bits) {
        LOGGER.info(toString(bits));
    }

    public static String toString(long[] bits) {
        char[][] cb = bitsToCharArray(bits, new char[8][8]);
        String s = System.lineSeparator();
        for (int i = 0; i < BOARD_SIZE; i++) {
            s += Arrays.toString(cb[i]) + System.lineSeparator();
        }
        return s;
    }

    /**
     * Pretty print the chess board bits.
     *
     * @param bits
     */
    public static void prettyPrintBoard(long[] bits) {
        char[][] cb = bitsToCharArray(bits, new char[8][8]);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cb[i][j] = charToFancyMap.get(cb[i][j]);
            }
        }
        String s = "";
        LOGGER.info("┏━━━━━━━━┓");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                char piece = cb[i][j];
                if ((i + j) % 2 == 0) {
                    s += (Character.toString(piece) );
                } else {
                    s += ("\u001B[47m" + Character.toString(piece) + "\u001B[0m");
                }
            }
            LOGGER.info("┃{}┃", s);
            s = "";
        }
        LOGGER.info("┗━━━━━━━━┛");
    }

    private static Map<Character, Character> charToFancyMap = Map.ofEntries(
            Map.entry('K', Constants.FANCY_W_KING),
            Map.entry('Q', Constants.FANCY_W_QUEEN),
            Map.entry('R', Constants.FANCY_W_ROOK),
            Map.entry('B', Constants.FANCY_W_BISHOP),
            Map.entry('N', Constants.FANCY_W_KNIGHT),
            Map.entry('P', Constants.FANCY_W_PAWN),

            Map.entry('k', Constants.FANCY_B_KING),
            Map.entry('q', Constants.FANCY_B_QUEEN),
            Map.entry('r', Constants.FANCY_B_ROOK),
            Map.entry('b', Constants.FANCY_B_BISHOP),
            Map.entry('n', Constants.FANCY_B_KNIGHT),
            Map.entry('p', Constants.FANCY_B_PAWN),
            Map.entry('.', ' ')
    );

    public static char[][] bitsToCharArray(long[] bits, char[][] board) {
        int boardSizeSquared = BOARD_SIZE * BOARD_SIZE;
        for (int i = 0; i < boardSizeSquared; i++) {
            board[i / BOARD_SIZE][i % BOARD_SIZE] = '.';
        }

        char[] pieces = {'P', 'R', 'N', 'B', 'K', 'Q', 'p', 'r', 'n', 'b', 'k', 'q'};
        int numPieces = pieces.length;
        for (int i = 0; i < numPieces; i++) {
            long currentBit = bits[i];
            for (int j = 0; j < boardSizeSquared; j++) {
                if ((currentBit & (1L << j)) != 0) {
                    board[j / BOARD_SIZE][j % BOARD_SIZE] = pieces[i];
                }
            }
        }
        return board;
    }

    public static void printBitboard(long bitboard) {
        LOGGER.info(toString(bitboard));
    }

    public static String toString(long bitboard) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (char[] row : cb) {
            Arrays.fill(row, '.');
        }

        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            if (((bitboard >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = '@';
            }
        }
        String s = System.lineSeparator();
        for (int i = 0; i < BOARD_SIZE; i++) {
            s += Arrays.toString(cb[i]) + System.lineSeparator();
        }
        return s;
    }

    public static long southOne(long b) {
        return b >> 8;
    }

    public static long northOne(long b) {
        return b << 8;
    }

    public static long eastOne(long b) {
        return (b << 1) & NOT_A_FILE;
    }

    public static long noEaOne(long b) {
        return (b << 9) & NOT_A_FILE;
    }

    public static long soEaOne(long b) {
        return (b >> 7) & NOT_A_FILE;
    }

    public static long westOne(long b) {
        return (b >> 1) & NOT_H_FILE;
    }

    public static long soWeOne(long b) {
        return (b >> 9) & NOT_H_FILE;
    }

    public static long noWeOne(long b) {
        return (b << 7) & NOT_H_FILE;
    }

    public static int getIndex(char file, char rank) {
        int s;
        s = (Character.toUpperCase(file)) - 65;
        s += 8 * (56 - rank);
        return s;
    }

    public static Pos indexToPos(int rank, int file, int index) {
        if (rank < 0) {
            rank = 0;
        }
        if (file < 0) {
            file = 0;
        }
        return new Pos((char) ('a' + file), (char) ('8' - rank), index);
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

    public static List<Move> getAllPossibleMoves(long[] bits, boolean isWhite) {
        List<Move> moveList;
        if (isWhite) {
            moveList = pawnMovesWhite(bits);
            //add the rest of the piece types
            //moveList.addAll(rookMovesWhite(bits));
            //ect..
        } else {
            moveList = pawnMovesWhite(bits);
        }
        return moveList;
    }

    public static boolean doMove(long[] bits, Move m) {
        if (getAllPossibleMoves(bits, false).contains(m)) {
            //implement, perform move m on board bits
            updateEmpty(m);//Empty the from square
            updateOccupancy(m);//update the occupancy board
            return true;
        }
        return false;
    }

    public static void printOccupancy() {
        printBitboard(occupancy);
    }

}
