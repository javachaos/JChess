package com.github.javachaos.jchess.moves;

import com.github.javachaos.jchess.utils.BitUtils;

@SuppressWarnings("unused")
public record Move(Pos from, Pos to, char promotion) implements Comparable<Move> {
    public static Move random() {
        return new Move(Pos.random(), Pos.random(), '.');
    }

    public String toString() {
        return String.valueOf(from) + to + (promotion == '.' ? "" : promotion);
    }

    public long fromBitboard() {
        return 1L << from.index();
    }

    public long toBitboard() {
        return 1L << to.index();
    }

    public Move reverse() {
        return new Move(to, from, '.');
    }

    /**
     * Create a move from a string of length 4-5 characters.
     * This implementation should be quite fast. O(1) * ~20.
     *
     * @param move string representing a move, the move should be valid
     *             if the move is invalid it could lead to buffer overflows
     *             ect, no checking is done in this method
     * @return a move representing the input string
     */
    public static Move fromString(String move) {
        return new Move(
                new Pos(move.charAt(0), move.charAt(1),
                        BitUtils.getIndex(move.charAt(0), move.charAt(1))),
                new Pos(move.charAt(2), move.charAt(3),
                        BitUtils.getIndex(move.charAt(2), move.charAt(3))),
                (move.length() == 5) ? move.charAt(4) : '.');
    }

    @Override
    public int compareTo(Move o) {
        if (promotion != '.' && o.promotion == '.') {
            return 1;
        }
        if (promotion == '.' && o.promotion != '.') {
            return -1;
        }
        return 0;
        //add better move ordering later.
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Move m) {
            return m.toString().equals(toString());
        } else {
            return false;
        }
    }
}
