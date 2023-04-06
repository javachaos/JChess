package com.github.javachaos.jchess.gamelogic.pieces.core;

/**
 * Represents a single move done in a chess game.
 *
 * @param player the player making the move
 * @param from the position from which to move
 * @param to the position to move
 * @param p the piece being moved
 */
public record Move(AbstractPiece.Player player, PiecePos from, PiecePos to, Piece p, Piece captive) {

    public Move reverse() {
        return new Move(player, to, from, p, captive);
    }
}
