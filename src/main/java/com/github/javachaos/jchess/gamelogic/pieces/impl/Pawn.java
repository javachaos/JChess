package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import java.util.Objects;
import java.util.Optional;

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
        if (!b.isOnBoard(p) || getPos().equals(p)) {
            return false;
        }
        PawnLocations pawns = getPawnLocations();

        //Test diagonal right
        if (testDiag(b, p, pawns.right())) return true;
        //Test diagonal left
        if (testDiag(b, p, pawns.left())) return true;

        if (getPos().equals(start)) {
            if (p.equals(pawns.oneAhead())) {
                return b.getPiece(pawns.oneAhead()).isEmpty();
            }
            if (p.equals(pawns.twoAhead())) {
                return b.getPiece(pawns.twoAhead()).isEmpty();
            }
        } else {
            if (p.equals(pawns.oneAhead())) {
                return b.getPiece(pawns.oneAhead()).isEmpty();
            }
        }
        return false;
    }

    private PawnLocations getPawnLocations() {
        PiecePos oneAhead;
        PiecePos twoAhead;
        PiecePos right;
        PiecePos left;
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
        return new PawnLocations(oneAhead, twoAhead, right, left);
    }

    private record PawnLocations(PiecePos oneAhead, PiecePos twoAhead, PiecePos right, PiecePos left) {
    }

    private boolean testDiag(Board b, PiecePos p, PiecePos right) {
        //Test diagonal right
        Optional<Piece> op = b.getPiece(right);
        if (b.isOnBoard(right) && op.isPresent()) {
            Piece c = op.get();
            if (c.getPlayer() != getPlayer() && p.equals(c.getPos())) {
                c.capture();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isKing() {
        return false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pawn pawn = (Pawn) o;
        return Objects.equals(start, pawn.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start);
    }

}
