package com.github.javachaos.jchess.gamelogic.ai.player;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;

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
     * Get the color of this player.
     *
     * @return the color of this player
     */
    Player getColor();

    /**
     * Return the opponent.
     * @return the opponent
     */
    Player getOpponent();

}
