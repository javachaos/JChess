package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends AbstractPiece {

    public Bishop(Player p, char x, char y) {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.BISHOP;
    }

    @Override
    public List<PiecePos> potentialMoves(Board b) {
        List<PiecePos> potentials = new ArrayList<>();
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        for (int[] dir : directions) {
            PiecePos pp = new PiecePos((char)(getPos().x() + dir[0]), (char)(getPos().y() + dir[1]));
            while (b.isOnBoard(pp)) {
                potentials.add(pp);
                pp = new PiecePos((char)(pp.x() + dir[0]), (char)(pp.y() + dir[1]));
            }
        }
        return potentials;
    }

    @Override
    public boolean canMove(Board b, PiecePos p) {
        List<PiecePos> possibleMoves = potentialMoves(b);
        if (possibleMoves.contains(p)) {
            List<Piece> pieces = getPiecesDiagonal(b, getPos(), p);
            LOGGER.debug(pieces);
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
