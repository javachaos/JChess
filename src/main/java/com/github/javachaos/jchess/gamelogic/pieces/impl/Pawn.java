package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.JChessException;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import java.util.List;

public class Pawn extends AbstractPiece {

    private final PiecePos start;

    public Pawn(Player p, char x, char y) {
        super(p, x, y);
        this.start = pos;
    }

    @Override
    public AbstractPiece.PieceType getType() {
        return PieceType.PAWN;
    }

    @Override
    public boolean canMove(Board b, PiecePos p) {
        //TODO implement check scanning
        // will need to check if this moves causes a check.
        //TODO implement en-passant later.
        if (notInCheck(b, p)) {
            if (!b.isOnBoard(p) || getPos().equals(p)) {
                return false;
            }
            PiecePos oneAhead, twoAhead, right, left;
            if (getPlayer() == Player.WHITE) {
                oneAhead = new PiecePos(getPos().x(), (char) (getPos().y() + 1));
                twoAhead = new PiecePos(getPos().x(), (char) (getPos().y() + 2));
                right = new PiecePos((char) (getPos().x() + 1), (char) (getPos().y() + 1));
                left = new PiecePos((char) (getPos().x() - 1), (char) (getPos().y() + 1));
            } else {
                oneAhead = new PiecePos(getPos().x(), (char) (getPos().y() - 1));
                twoAhead = new PiecePos(getPos().x(), (char) (getPos().y() - 2));
                right = new PiecePos((char) (getPos().x() - 1), (char) (getPos().y() - 1));
                left = new PiecePos((char) (getPos().x() + 1), (char) (getPos().y() - 1));
            }

            //Test diagonal right
            if (b.isOnBoard(right) && b.getPiece(right).isPresent()) {
                Piece c = b.getPiece(right).get();
                if (c.getPlayer() != getPlayer()) {
                    if (p.equals(c.getPos())) {
                        c.capture();
                        return true;
                    }
                }
            }

            //Test diagonal left
            if (b.isOnBoard(left) && b.getPiece(left).isPresent()) {
                Piece c = b.getPiece(left).get();
                if (c.getPlayer() != getPlayer()) {
                    if (p.equals(c.getPos())) {
                        c.capture();
                        return true;
                    }
                }
            }

            if (getPos().equals(start)) {
                if (p.equals(oneAhead)) {
                    return b.getPiece(oneAhead).isEmpty();
                }
                if (p.equals(twoAhead)) {
                    return b.getPiece(twoAhead).isEmpty();
                }
            } else {
                if (p.equals(oneAhead)) {
                    return b.getPiece(oneAhead).isEmpty();
                }
            }
        }
        return false;
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public List<PiecePos> potentialMoves(Board b) {
        return null;//TODO change how this is implemented.
    }

}
