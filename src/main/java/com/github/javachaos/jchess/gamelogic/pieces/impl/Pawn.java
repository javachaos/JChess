package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.core.player.Player;

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
    public boolean canMove(ChessBoard b, PiecePos p) {
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

    private boolean testDiag(ChessBoard b, PiecePos p, PiecePos right) {
        //Test diagonal right
        Optional<Piece> op = b.getPiece(right);
        if (b.isOnBoard(right) && op.isPresent()) {
            Piece c = op.get();
            return c.getPlayer() != getPlayer() && p.equals(c.getPos());
        }
        return false;
    }

    @Override
    public boolean isKing() {
        return false;
    }

    private record PawnLocations(PiecePos oneAhead, PiecePos twoAhead, PiecePos right, PiecePos left) {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Pawn that = (Pawn) o;
        return  pos.equals(that.getPos())
                && color == that.color
                && that.getType() == getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, color);
    }

}
