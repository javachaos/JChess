package com.github.javachaos.jchess.gamelogic.pieces.core;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.player.Player;

public interface Piece {

    /**
     * Return the type of this piece
     *
     * @return the type of this piece
     */
    AbstractPiece.PieceType getType();

    /**
     * Get the current owner of this piece.
     *
     * @return the current owner of this piece
     */
    Player getPlayer();

    /**
     * Get the current opponent of this piece.
     *
     * @return the opponent of this piece.
     */
    Player getOpponent();

    /**
     * Return the location of this piece on the board.
     *
     * @return the location of this piece on the board
     */
    PiecePos getPos();

    /**
     * Move this piece to position x, y on the board unconditionally.
     *
     * @param x the desired x position
     * @param y the desired y position
     */
    void move(char x, char y);

    /**
     * Move this piece to position x, y on the board unconditionally.
     *
     * @param p the desired position
     */
    void move(PiecePos p);

    /**
     * Called when this piece has been captured.
     * Removing it from the game.
     */
    void capture();

    /**
     * Called when this piece is brought back into play.
     */
    void resurrect();

    /**
     * True if this pieces is a black player piece.
     *
     * @return whether this piece is a black piece
     */
    boolean isBlack();

    /**
     * True if this piece is a white player piece.
     *
     * @return whether this piece is a white piece
     */
    boolean isWhite();

    /**
     * Validate if a move is valid.
     *
     * @param board      the board
     * @param desiredPos the desired new position
     * @return true if the move is valid
     */
    boolean canMove(Board board, PiecePos desiredPos);

    /**
     * Check if this piece is the king.
     *
     * @return true if this piece is the king
     */
    boolean isKing();

}
