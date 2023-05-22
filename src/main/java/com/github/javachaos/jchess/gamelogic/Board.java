package com.github.javachaos.jchess.gamelogic;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.ai.player.AIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import java.util.List;
import java.util.Optional;

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
     * Return the last move made on this board.
     * @return the last move made on this board
     */
    Move getLastMove();

    /**
     * Return the board score for this board.
     * @return
     */
    int boardScore(Player p);

    /**
     * Move a piece on the board from fromPos to toPos.
     * @param fromPos start position
     * @param toPos end position
     *
     * @throws JChessException if the move is invalid or would put the player who owns the piece at
     * fromPos into check
     */
    void movePiece(PiecePos fromPos, PiecePos toPos) throws JChessException;

    /**
     * Return a list of pieces for this board for player p.
     *
     * @param p the player for which to get the pieces for
     * @return the pieces for the player p
     */
    List<Piece> getPieces(Player p);

    /**
     * Get all potential moves for the piece at pos on this board.
     *
     * @param pos the position to query all possible pieces.
     * @return the list of all potential moves
     */
    List<PiecePos> getPotentialMoves(PiecePos pos);

    /**
     * Get the piece at pp
     *
     * @param pp the PiecePos representing the position of a piece on this board.
     * @return the piece at pp on this board
     */
    Optional<Piece> getPiece(PiecePos pp);

    /**
     * Create a deep copy of this board
     *
     * @return a full copy of this board.
     */
    Board deepCopy();

    /**
     * Move a piece on this board
     *
     * @param m the move to make
     */
    void move(Move m);

    /**
     * Get the king piece for the player p.
     *
     * @param p the player for whom to get the king
     * @return the king for player p
     */
    Piece getKing(Player p);

    /**
     * Check if the player p is in check.
     *
     * @param p the player to check if in check
     * @return true if player p is in check
     */
    boolean isInCheck(Player p);

    /**
     * Get a piece from this board
     * @param x x co-ord
     * @param y y co-ord
     * @return the optional piece
     */
    Optional<Piece> getPiece(char x, char y);

    /**
     * Ensure the piece at potentialMove is on the board.
     *
     * @param potentialMove the position to check
     * @return true if this position exists on the board
     */
    boolean isOnBoard(PiecePos potentialMove);

    /**
     * Ensure the piece at potentialMove is on the board.
     *
     * @param x the x position to check
     * @param y the y position to check
     * @return true if this position exists on the board
     */
    boolean isOnBoard(char x, char y);

    /**
     * Perform AI move for this board.
     *
     * @return
     */
    Move getAIMove();

    /**
     * Get the AI for this board.
     * @return the AIPlayer for this board
     */
    AIPlayer getAI();

    /**
     * Clear all pieces off this board.
     */
    void clear();

    /**
     * Remove a piece from the board.
     * @param captive the piece to remove
     */
    void remove(Piece captive);

    /**
     * Add a captive to the captured pieces list.
     * @param captive the captured piece
     */
    void addCaptive(Piece captive);

    /**
     * Get a list of all possible positions.
     * @return the list of all positions
     */
    List<PiecePos> getAllPositions();

    /**
     * Return the FEN representation of the board.
     * @return a string in FEN notation of the board
     */
    String getFenString();

    /**
     * Set the current player.
     * @param player the player
     */
    void setActivePlayer(Player player);

    /**
     * Apply the given fen string to this board.
     * @param fenStr the string representing the board
     */
    void applyFen(String fenStr);
}
