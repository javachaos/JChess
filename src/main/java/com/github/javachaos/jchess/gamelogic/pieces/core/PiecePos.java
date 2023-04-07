package com.github.javachaos.jchess.gamelogic.pieces.core;

/**
 * Represents a single x, y position for a piece on the board
 * as 2 characters, x between [1-8] and y [a-h]
 *
 * @param x the x co-ordinate
 * @param y the y co-ordinate
 */
public record PiecePos(char x, char y) {

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
