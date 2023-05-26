package com.github.javachaos.jchess.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@SuppressWarnings("all")
public class BitUtils {

    public record MoveSet(Move[] moves, long occupancy) {}

    public record Pos(char file, char rank) {
        public String toString() {
            return "" + file + rank;
        }
    }
    public record Move(Pos from, Pos to, char promotion) implements Comparable<Move> {
        static int score;
        public String toString() {
            return "" + from + to + (promotion == '.' ? "" : promotion);
        }

        @Override
        public int compareTo(Move o) {
            return score - o.score;
        }
    }

    private BitUtils() {}

    public static final Logger LOGGER = LogManager.getLogger(BitUtils.class);

    private static final int BOARD_SIZE = 8;
    private static final long RANK_8  = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_11111111L;
    private static final long RANK_4  = 0b00000000_00000000_00000000_11111111_00000000_00000000_00000000_00000000L;
    private static final long RANK_5  = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
    private static final long RANK_1  = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long FILE_A  = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    private static final long FILE_AB = 0b11000000_11000000_11000000_11000000_11000000_11000000_11000000_11000000L;
    private static final long FILE_H  = 0b00000001_00000001_00000001_00000001_00000001_00000001_00000001_00000001L;
    private static final long FILE_GH = 0b00000011_00000011_00000011_00000011_00000011_00000011_00000011_00000011L;
    private static final long KING_SIDE = 0b00001111_00001111_00001111_00001111_00001111_00001111_00001111_00001111L;
    private static final long QUEEN_SIDE = 0b11110000_11110000_11110000_11110000_11110000_11110000_11110000_11110000L;
    private static final long NOT_A_FILE = ~FILE_A;
    private static final long NOT_H_FILE = ~FILE_H;
    private static final long NOT_RANK_8 = ~RANK_8;
    private static final long NOT_RANK_1 = ~RANK_1;
    private static long captureBlackPieces = 0L;
    private static long captureWhitePieces = 0L;
    private static long empty = 0L;
    
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

    public static char[][] occupancyToCharArray(long occupancy) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            cb[i / BOARD_SIZE][i % BOARD_SIZE] = '.';
        }
        if (Long.bitCount(occupancy) > 0) {
            //in the case that bitCount uses 1 machine instruction this could be faster.
            int start = Long.numberOfTrailingZeros(occupancy);
            int end = Long.numberOfLeadingZeros(occupancy);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((occupancy >> i) & 1L) == 1) {
                    cb[i/8][i%8] = '@';
                }
            }
        }
        return cb;
    }

    public static void updateBoards(long[] bits) {
        updateBlacks(bits);
        updateEmpty(bits);
    }

    public static MoveSet pawnMovesWhite(long[] bits) {
        Move[] moves = new Move[64];
        //pawn right captures
        long moveBitsRight = ((bits[0] >> 7) & captureBlackPieces & NOT_RANK_8 & NOT_A_FILE);
        //pawn left captures
        long moveBitsLeft = ((bits[0] >> 9) & captureBlackPieces & NOT_RANK_8 & NOT_H_FILE);
        //one ahead
        long moveBitsOneAhead = (bits[0] >> 8) & empty & NOT_RANK_8;
        //two ahead
        long moveBitsTwoAhead = (bits[0] >> 16) & empty & (empty>>8)&RANK_4;
        //check capture right promotions
        long moveBitsRightP = (bits[0] >> 7)&captureBlackPieces&RANK_8&NOT_A_FILE;
        //check capture left promotions
        long moveBitsLeftP = (bits[0] >> 9)&captureBlackPieces&RANK_8&NOT_H_FILE;
        //check one ahead promotion
        long moveBitsP = (bits[0] >> 8)&empty&RANK_8;

        long moveOccupancy = moveBitsRight | moveBitsLeft
                | moveBitsOneAhead | moveBitsTwoAhead | moveBitsRightP | moveBitsLeftP | moveBitsP;
        if (Long.bitCount(moveBitsRight) > 0) {
            //in the case that popCount uses 1 machine instruction this could be faster.
            int start = Long.numberOfTrailingZeros(moveBitsRight);
            int end = Long.numberOfLeadingZeros(moveBitsRight);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsRight >>i) & 1L) == 1) {
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8 - 1),
                            indexToPos(i / 8,  i % 8), '.'));
                }
            }
        }

        if (Long.bitCount(moveBitsLeft) > 0) {
            int start = Long.numberOfTrailingZeros(moveBitsLeft);
            int end = Long.numberOfLeadingZeros(moveBitsLeft);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsLeft >>i) & 1L) == 1) {
                    moves[i] =(new Move(indexToPos(i / 8 + 1, i % 8 + 1),
                            indexToPos(i / 8,  i % 8), '.'));
                }
            }
        }

        if (Long.bitCount(moveBitsOneAhead) > 0) {
            int start = Long.numberOfTrailingZeros(moveBitsOneAhead);
            int end = Long.numberOfLeadingZeros(moveBitsOneAhead);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsOneAhead >>i) & 1L) == 1) {
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8),
                            indexToPos(i / 8,  i % 8), '.'));
                }
            }
        }

        if (Long.bitCount(moveBitsTwoAhead) > 0) {
            int start = Long.numberOfTrailingZeros(moveBitsTwoAhead);
            int end = Long.numberOfLeadingZeros(moveBitsTwoAhead);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsTwoAhead >>i) & 1L) == 1) {
                    moves[i] = (new Move(indexToPos(i / 8 + 2, i % 8),
                            indexToPos(i / 8,  i % 8), '.'));
                }
            }
        }
        if (Long.bitCount(moveBitsRightP) > 0) {
            int start = Long.numberOfTrailingZeros(moveBitsRightP);
            int end = Long.numberOfLeadingZeros(moveBitsRightP);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsRightP >>i) & 1L) == 1) {
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8 - 1),
                            indexToPos(i / 8,  i % 8), 'Q'));
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8 - 1),
                            indexToPos(i / 8,  i % 8), 'R'));
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8 - 1),
                            indexToPos(i / 8,  i % 8), 'B'));
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8 - 1),
                            indexToPos(i / 8,  i % 8), 'N'));
                }
            }
        }

        if (Long.bitCount(moveBitsLeftP) > 0) {
            int start = Long.numberOfTrailingZeros(moveBitsLeftP);
            int end = Long.numberOfLeadingZeros(moveBitsLeftP);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsLeftP >>i) & 1L) == 1) {
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8),
                            indexToPos(i / 8,  i % 8), 'Q'));
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8),
                            indexToPos(i / 8,  i % 8), 'R'));
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8),
                            indexToPos(i / 8,  i % 8), 'B'));
                    moves[i] = (new Move(indexToPos(i / 8 + 1, i % 8),
                            indexToPos(i / 8,  i % 8), 'N'));
                }
            }
        }
        if (Long.bitCount(moveBitsP) > 0) {
            int start = Long.numberOfTrailingZeros(moveBitsP);
            int end = Long.numberOfLeadingZeros(moveBitsP);
            for (int i = start; i < BOARD_SIZE * BOARD_SIZE - end; i++) {
                if (((moveBitsP >>i) & 1L) == 1) {
                    moves[i] = (new Move(indexToPos(i / 8, i % 8),
                            indexToPos(i / 8,  i % 8), 'Q'));
                    moves[i] = (new Move(indexToPos(i / 8, i % 8),
                            indexToPos(i / 8,  i % 8), 'R'));
                    moves[i] = (new Move(indexToPos(i / 8, i % 8),
                            indexToPos(i / 8,  i % 8), 'B'));
                    moves[i] = (new Move(indexToPos(i / 8, i % 8),
                            indexToPos(i / 8,  i % 8), 'N'));
                }
            }
        }

        return new MoveSet(moves, moveOccupancy);
    }

    public static long[] createBitBoard(char[][] cb) {
        long[] bits = new long[]{
                0L, // 0 white pawn
                0L, // 1 white rook
                0L, // 2 white knight
                0L, // 3 white bishop
                0L, // 4 white king
                0L, // 5 white queen
                0L, // 6 black pawn
                0L, // 7 black rook
                0L, // 8 black knight
                0L, // 9 black bishop
                0L, // 10 black king
                0L  // 11 black queen
        };

        long bin;
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            bin = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
            bin = setBit(bin, i);

            switch (cb[i / BOARD_SIZE][i % BOARD_SIZE]) {
                case 'P':
                    bits[0] += bin;
                    break;
                case 'R':
                    bits[1] += bin;
                    break;
                case 'N':
                    bits[2] += bin;
                    break;
                case 'B':
                    bits[3] += bin;
                    break;
                case 'K':
                    bits[4] += bin;
                    break;
                case 'Q':
                    bits[5] += bin;
                    break;
                case 'p':
                    bits[6] += bin;
                    break;
                case 'r':
                    bits[7] += bin;
                    break;
                case 'n':
                    bits[8] += bin;
                    break;
                case 'b':
                    bits[9] += bin;
                    break;
                case 'k':
                    bits[10] += bin;
                    break;
                case 'q':
                    bits[11] += bin;
                    break;
                case '.':
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + cb[i / BOARD_SIZE][i % BOARD_SIZE]);
            }
        }
        updateWhites(bits);
        updateBlacks(bits);
        return bits;
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
        char[][] cb = bitsToCharArray(bits);
        for (int i = 0; i < BOARD_SIZE; i++) {
            String s = Arrays.toString(cb[i]);
            LOGGER.info(s);
        }
    }

    @SuppressWarnings("all")
    public static char[][] bitsToCharArray(long[] bits) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            cb[i / BOARD_SIZE][i % BOARD_SIZE] = '.';
        }

        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            if (((bits[0] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'P';
            }
            if (((bits[1] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'R';
            }
            if (((bits[2] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'N';
            }
            if (((bits[3] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'B';
            }
            if (((bits[4] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'K';
            }
            if (((bits[5] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'Q';
            }
            if (((bits[6] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'p';
            }
            if (((bits[7] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'r';
            }
            if (((bits[8] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'n';
            }
            if (((bits[9] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'b';
            }
            if (((bits[10] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'k';
            }
            if (((bits[11] >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = 'q';
            }
        }
        return cb;
    }

    public static void printBitboard(long bitboard) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (char[] row : cb) {
            Arrays.fill(row, '.');
        }

        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            if (((bitboard >> i) & 1L) == 1) {
                cb[i / BOARD_SIZE][i % BOARD_SIZE] = '@';
            }
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            String s = Arrays.toString(cb[i]);
            LOGGER.info(s);
        }
        LOGGER.info("");
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

    public static Pos indexToPos(int rank, int file) {
        if (rank < 0 || file < 0) {
            throw new IllegalArgumentException("Cannot have a negative index!");
        }
        return new Pos((char) ('a' + file), (char) ('8' - rank));
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

}
