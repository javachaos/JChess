package com.github.javachaos.jchess.moves;

public record Pos(char file, char rank, int index) {
    public String toString() {
        return String.valueOf(file) + rank;
    }
}
