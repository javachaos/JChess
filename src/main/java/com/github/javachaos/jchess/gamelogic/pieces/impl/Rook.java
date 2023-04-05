package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import java.util.ArrayList;
import java.util.List;

public class Rook extends AbstractPiece {

    public Rook(Player p, char x, char y) {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }

    @Override
    public List<PiecePos> potentialMoves(Board b) {
        List<PiecePos> potentials = new ArrayList<>();
        PiecePos pp = new PiecePos((char)(getPos().x()+1), getPos().y());
        while(b.isOnBoard(pp)) {
            potentials.add(pp);
            pp = new PiecePos((char)(pp.x()+1), pp.y());
        }
        PiecePos pp2 = new PiecePos(getPos().x(), (char)(getPos().y()+1));
        while(b.isOnBoard(pp2)) {
            potentials.add(pp2);
            pp2 = new PiecePos(pp2.x(), (char)(pp2.y()+1));
        }
        PiecePos pp3 = new PiecePos((char)(getPos().x()-1), getPos().y());
        while(b.isOnBoard(pp3)) {
            potentials.add(pp3);
            pp3 = new PiecePos((char)(pp3.x()-1), pp3.y());
        }
        PiecePos pp4 = new PiecePos(getPos().x(), (char)(getPos().y()-1));
        while(b.isOnBoard(pp4)) {
            potentials.add(pp4);
            pp4 = new PiecePos(pp4.x(), (char)(pp4.y()-1));
        }
        return potentials;
    }

    @Override
    public boolean canMove(Board b, PiecePos p) {
        List<PiecePos> pom = potentialMoves(b);
        if (pom.contains(p)) {
            List<Piece> pieces = getPiecesLateral(b, getPos(), p);
            pieces.remove(this);
            if (!pieces.isEmpty()) {
                Piece piece = b.getPiece(p).orElse(null);
                if (pieces.size() == 1
                    && pieces.contains(piece)
                    && piece != null
                    && piece.getPlayer() != getPlayer()) {
                    return true;
                }
                return false;
            }

            // c. check if this move would put our king into check
            return notInCheck(b, p);//TODO implement
        }
        return false;
    }

    @Override
    public boolean isKing() {
        return false;
    }
}
