package com.github.javachaos.jchess.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@SuppressWarnings("unused")
public class BitUtils {

    private BitUtils() {}

    public static final Logger LOGGER = LogManager.getLogger(BitUtils.class);

    private static final int BOARD_SIZE = 8;
    private static final long RANK_8 = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111L;
    private static final long RANK_1 = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long FILE_A = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    private static final long FILE_H = 0b00000001_00000001_00000001_00000001_00000001_00000001_00000001L;
    private static final long NOT_A_FILE = ~FILE_A;
    private static final long NOT_H_FILE = ~FILE_H;
    private static long captureBlackPieces = 0L;
    private static long captureWhitePieces = 0L;
    
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

    public static long[] createBitBoard(char[][] cb) {
        long[] bits = new long[]{0L, // 0 white pawn
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

    public static void printBits(long[] bits) {
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

    public static int popCount(long x) {
    	return Long.bitCount(x);
    }
}
