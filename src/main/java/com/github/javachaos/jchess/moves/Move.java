package com.github.javachaos.jchess.moves;

public record Move(Pos from, Pos to, char promotion) implements Comparable<Move> {
    public String toString() {
        return String.valueOf(from) + to + (promotion == '.' ? "" : promotion);
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
