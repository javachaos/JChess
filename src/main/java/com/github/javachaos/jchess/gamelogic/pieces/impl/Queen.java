package com.github.javachaos.jchess.gamelogic.pieces.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.JChessException;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

public class Queen extends AbstractPiece {

    public Queen(Player p, char x, char y) throws JChessException {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }


    @Override
    public List<PiecePos> potentialMoves(Board b) {
        List<PiecePos> potentials = new ArrayList<>();

        int[][] directions = {{1, 1}, {-1, 1}, {1, -1},
                {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] dir : directions) {
            PiecePos pp = new PiecePos((char) (getPos().x() + dir[0]), (char) (getPos().y() + dir[1]));
            while (b.isOnBoard(pp)) {
                potentials.add(pp);
                pp = new PiecePos((char) (pp.x() + dir[0]), (char) (pp.y() + dir[1]));
            }
        }

        return potentials;
    }

    @Override
    public boolean canMove(Board b, PiecePos p) {
    	List<PiecePos> potentialMoves = potentialMoves(b);
        if (potentialMoves.contains(p)) {
            List<Piece> pieces = getPiecesLateral(b, getPos(), p);
            pieces.addAll(getPiecesDiagonal(b, getPos(), p));
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
