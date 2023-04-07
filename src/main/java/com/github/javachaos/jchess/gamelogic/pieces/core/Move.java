package com.github.javachaos.jchess.gamelogic.pieces.core;

import com.github.javachaos.jchess.gamelogic.pieces.core.player.Player;

/**
 * Represents a single move done in a chess game.
 *
 * @param from the position from which to move
 * @param to   the position to move
 * @param p    the piece being moved
 */
public record Move(PiecePos from, PiecePos to,
                   AbstractPiece.PieceType type, Player p) {

    public Move reverse() {
        return new Move(to, from, type, p);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
