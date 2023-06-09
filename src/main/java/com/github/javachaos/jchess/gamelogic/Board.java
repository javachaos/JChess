package com.github.javachaos.jchess.gamelogic;

import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.player.Player;

public interface Board {

    /**
     * Start a new game on this board.
     */
    void start();

    /**
     * Undo a move.
     * @param m the move to undo
     */
    void undoMove(Move m);

    /**
     * Perform a move on this board.
     * If there was a capture, return it.
     *
     * @param m the move to be performed
     * @return the captured piece, null if none.
     */
    Piece doMove(Move m);

    /**
     * Reset the board to the default start state.
     */
    void reset();

    /**
     * Clear the board, and update this boards pieces to reflect the fen string.
     * @param fen the FEN (Forsyth-Edwards Notation) string
     */
    void applyFen(String fen);

    Player getActivePlayer();
}
