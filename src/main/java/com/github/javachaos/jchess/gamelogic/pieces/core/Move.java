package com.github.javachaos.jchess.gamelogic.pieces.core;

import com.github.javachaos.jchess.gamelogic.player.Player;

/**
 * Represents a single move done in a chess game.
 *
 * @param from the position from which to move
 * @param to   the position to move
 * @param type the type of piece captured if there was a capture
 * @param color    the piece being moved
 */
public record Move(PiecePos from, PiecePos to,
                   AbstractPiece.PieceType type, Player color) {

    public static Move empty() {
        return new Move(PiecePos.A1, PiecePos.A1, AbstractPiece.PieceType.NONE, Player.WHITE);
    }

    public Move reverse() {
        return new Move(to, from, type, color);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
