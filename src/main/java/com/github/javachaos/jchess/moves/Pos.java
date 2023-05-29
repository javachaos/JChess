package com.github.javachaos.jchess.moves;


import com.github.javachaos.jchess.utils.BitUtils;
import com.github.javachaos.jchess.utils.RandUtils;

public record Pos(char file, char rank, int index) {
    public static Pos random() {
        char f = (char) ('a' + RandUtils.random.nextInt(8));
        char r = (char) ('1' + RandUtils.random.nextInt(8));
        return new Pos(f, r, BitUtils.getIndex(f, r));
    }

    public String toString() {
        return String.valueOf(file) + rank;
    }
}
