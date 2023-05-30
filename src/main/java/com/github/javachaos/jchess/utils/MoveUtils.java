package com.github.javachaos.jchess.utils;

@SuppressWarnings("unused")
public class MoveUtils {

    private MoveUtils() {
        //Unused
    }

    public static final int BOARD_SIZE = 8;
    public static final long RANK_1  = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    public static final long RANK_2  = 0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
    public static final long RANK_3  = 0b00000000_00000000_11111111_00000000_00000000_00000000_00000000_00000000L;
    public static final long RANK_4  = 0b00000000_00000000_00000000_11111111_00000000_00000000_00000000_00000000L;
    public static final long RANK_5  = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
    public static final long RANK_6  = 0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L;
    public static final long RANK_7  = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000L;
    public static final long RANK_8  = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_11111111L;
    public static final long FILE_A  = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    public static final long FILE_B  = 0b01000000_01000000_01000000_01000000_01000000_01000000_01000000_01000000L;
    public static final long FILE_C  = 0b00100000_00100000_00100000_00100000_00100000_00100000_00100000_00100000L;
    public static final long FILE_D  = 0b00010000_00010000_00010000_00010000_00010000_00010000_00010000_00010000L;
    public static final long FILE_E  = 0b00001000_00001000_00001000_00001000_00001000_00001000_00001000_00001000L;
    public static final long FILE_F  = 0b00000100_00000100_00000100_00000100_00000100_00000100_00000100_00000100L;
    public static final long FILE_G  = 0b00000010_00000010_00000010_00000010_00000010_00000010_00000010_00000010L;
    public static final long FILE_H  = 0b00000001_00000001_00000001_00000001_00000001_00000001_00000001_00000001L;
    public static final long FILE_AB = 0b11000000_11000000_11000000_11000000_11000000_11000000_11000000_11000000L;
    public static final long FILE_GH = 0b00000011_00000011_00000011_00000011_00000011_00000011_00000011_00000011L;
    public static final long KING_SIDE = 0b00001111_00001111_00001111_00001111_00001111_00001111_00001111_00001111L;
    public static final long QUEEN_SIDE = 0b11110000_11110000_11110000_11110000_11110000_11110000_11110000_11110000L;
    public static final long NOT_A_FILE = ~FILE_A;
    public static final long NOT_H_FILE = ~FILE_H;
    public static final long NOT_RANK_8 = ~RANK_8;
    public static final long NOT_RANK_1 = ~RANK_1;

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
}
