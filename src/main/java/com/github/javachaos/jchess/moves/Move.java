package com.github.javachaos.jchess.moves;

@SuppressWarnings("unused")
public record Move(Pos from, Pos to, char promotion) implements Comparable<Move> {
    public String toString() {
        return String.valueOf(from) + to + (promotion == '.' ? "" : promotion);
    }

    public long fromBitboard() {
        return 1L << from.index();
    }

    public long toBitboard() {
        return 1L << to.index();
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
}
