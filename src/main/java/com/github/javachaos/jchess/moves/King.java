package com.github.javachaos.jchess.moves;


@SuppressWarnings("all")
public class King {
    // King moves to the north
    public static final long[] KING_MOVE_NORTH = {
            0x000000000000FF00L,
            0x000000000000FF00L << 8,
            0x000000000000FF00L << 16,
            0x000000000000FF00L << 24,
            0x000000000000FF00L << 32,
            0x000000000000FF00L << 40,
            0x000000000000FF00L << 48,
            0x000000000000FF00L << 56
    };

    // King moves to the south
    public static final long[] KING_MOVES_SOUTH = {
            0x00FF000000000000L,
            0x00FF000000000000L >> 8,
            0x00FF000000000000L >> 16,
            0x00FF000000000000L >> 24,
            0x00FF000000000000L >> 32,
            0x00FF000000000000L >> 40,
            0x00FF000000000000L >> 48,
            0x00FF000000000000L >> 56
    };

    // King moves to the west
    public static final long[] KING_MOVES_WEST = {
            0x8080808080808080L,
            0x8080808080808080L >> 1,
            0x8080808080808080L >> 2,
            0x8080808080808080L >> 3,
            0x8080808080808080L >> 4,
            0x8080808080808080L >> 5,
            0x8080808080808080L >> 6,
            0x8080808080808080L >> 7
    };

    // King moves to the east
    public static final long[] KING_MOVES_EAST = {
            0x0101010101010101L,
            0x0101010101010101L << 1,
            0x0101010101010101L << 2,
            0x0101010101010101L << 3,
            0x0101010101010101L << 4,
            0x0101010101010101L << 5,
            0x0101010101010101L << 6,
            0x0101010101010101L << 7
    };

    // King moves to the northwest
    public static final long[] KING_MOVES_NORTHWEST = {
            0x8040201008040201L,
            0x8040201008040201L << 8,
            0x8040201008040201L << 16,
            0x8040201008040201L << 24,
            0x8040201008040201L << 32,
            0x8040201008040201L << 40,
            0x8040201008040201L << 48,
            0x8040201008040201L << 56
    };

    // King moves to the northeast
    public static final long[] KING_MOVES_NORTHEAST = {
            0x0102040810204080L,
            0x0102040810204080L << 8,
            0x0102040810204080L << 16,
            0x0102040810204080L << 24,
            0x0102040810204080L << 32,
            0x0102040810204080L << 40,
            0x0102040810204080L << 48,
            0x0102040810204080L << 56
    };

    // King moves to the southwest
    public static final long[] KING_MOVES_SOUTHWEST = {
            0x0102040810204080L >> 8,
            0x0102040810204080L >> 16,
            0x0102040810204080L >> 24,
            0x0102040810204080L >> 32,
            0x0102040810204080L >> 40,
            0x0102040810204080L >> 48,
            0x0102040810204080L >> 56,
            0x0102040810204080L >> 64
    };

    // King moves to the southeast
    public static final long[] KING_MOVES_SOUTHEAST = {
            0x8040201008040201L >> 8,
            0x8040201008040201L >> 16,
            0x8040201008040201L >> 24,
            0x8040201008040201L >> 32,
            0x8040201008040201L >> 40,
            0x8040201008040201L >> 48,
            0x8040201008040201L >> 56,
            0x8040201008040201L >> 64
    };

}
