package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.JChessException;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import java.util.ArrayList;
import java.util.List;

public class King extends AbstractPiece {

    public King(Player p, char x, char y) throws JChessException {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.KING;
    }


    @Override
    public List<PiecePos> potentialMoves(Board b) {
        List<PiecePos> potentials = new ArrayList<>();
        int[][] offsets = {
                {1, 0},
                {0, 1},
                {-1, 0},
                {0, -1},
                {1, 1},
                {-1, 1},
                {-1, -1},
                {1, -1}
        };
        for (int[] o : offsets) {
            PiecePos pp = new PiecePos(
                    (char) (getPos().x() + o[0]),
                    (char) (getPos().y() + o[1]));
            if (b.isOnBoard(pp)) {
                potentials.add(pp);
            }
        }
        return potentials;
    }

    @Override
    public boolean canMove(Board b, PiecePos p) {

        if (notInCheck(b, p)) {
            List<PiecePos> pom = potentialMoves(b);
            List<PiecePos> removals = new ArrayList<>();
            List<Piece> pieces = new ArrayList<>();
            pom.forEach(pp -> b.getPiece(pp).ifPresent(pieces::add));
            for (Piece piece : pieces) {
                if (piece.getPlayer() == getPlayer()) {
                    removals.add(piece.getPos());
                }
            }
            pom.removeAll(removals);

            return pom.contains(p);
        }
        return false;
    }

    @Override
    public boolean isKing() {
        return true;
    }

    public void check() {
        //TODO check for checkmate ect
    }
}
