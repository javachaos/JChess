package com.github.javachaos.jchess.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@SuppressWarnings("unused")
public class BitUtils {

    public static final Logger LOGGER = LogManager.getLogger(BitUtils.class);

    private static final int BOARD_SIZE = 8;
    private static final long NOT_A_FILE = 0xfefefefefefefefeL;
    private static final long NOT_H_FILE = 0x7f7f7f7f7f7f7f7fL;

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

    public static long strToBitBoard(String bin) {
        if (bin.charAt(0) == '0') {
            return Long.parseLong(bin, 2);
        } else {
            return Long.parseLong("1" + bin.substring(2), 2) * 2;
        }
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

    public static long soutOne(long b) {
        return b >> 8;
    }

    public static long nortOne(long b) {
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

    public int popCount(long x) {
        /* -1/3 */
        long k1 = 0x5555555555555555L;
        x = x - ((x >> 1) & k1); /* put count of each 2 bits into those 2 bits */
        /* -1/5 */
        long k2 = 0x3333333333333333L;
        x = (x & k2) + ((x >> 2) & k2); /* put count of each 4 bits into those 4 bits */
        /* -1/17 */
        long k4 = 0x0f0f0f0f0f0f0f0fL;
        x = (x + (x >> 4)) & k4; /* put count of each 8 bits into those 8 bits */
        /* -1/255 */
        long kf = 0x0101010101010101L;
        x = (x * kf) >> 56; /* returns 8 most significant bits of x + (x<<8) + (x<<16) + (x<<24) + ... */
        return (int) x;
    }
}
