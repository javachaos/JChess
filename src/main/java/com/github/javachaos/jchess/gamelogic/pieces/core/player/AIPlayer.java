package com.github.javachaos.jchess.gamelogic.pieces.core.player;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;

import java.util.List;

/**
 * The player interface.
 */
public interface AIPlayer {

    /**
     * Get the move for this player. The move is not
     * performed by this method.
     *
     * @param b the board on which to execute the move.
     * @return the move this player should make
     */
    Move getNextMove(Board b);

    /**
     * Returns the list of captured enemy pieces for this player.
     *
     * @return the list of captured enemy pieces
     */
    List<Piece> getCapturedPieces();

    /**
     * Get the color of this player.
     *
     * @return the color of this player
     */
    Player getColor();

    /**
     * Return the opposing player color.
     *
     * @return the opponents color
     */
    Player getOpponent();

}
