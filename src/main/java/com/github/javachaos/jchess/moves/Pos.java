package com.github.javachaos.jchess.moves;

public record Pos(char file, char rank) {
    public String toString() {
        return String.valueOf(file) + rank;
    }
}
