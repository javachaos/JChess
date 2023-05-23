package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Queen extends AbstractPiece {

    public Queen(Player p, char x, char y) {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }

    private List<PiecePos> potentialMoves(Board b) {
        List<PiecePos> potentials = new ArrayList<>();
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
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
        List<PiecePos> possibleMoves = potentialMoves(b);
        if (possibleMoves.contains(p)) {
            List<Piece> diagonalPieces = getPiecesDiagonal(b, getPos(), p);
            diagonalPieces.remove(this);
            List<Piece> lateralPieces = getPiecesLateral(b, getPos(), p);
            lateralPieces.remove(this);

            // check if move is valid for rook
            if (!lateralPieces.isEmpty()) {
                Piece piece = b.getPiece(p).orElse(null);
                return lateralPieces.size() == 1
                        && lateralPieces.contains(piece)
                        && piece != null
                        && piece.getPlayer() != getPlayer();
            }

            // check if move is valid for bishop
            if (!diagonalPieces.isEmpty()) {
                Piece piece = b.getPiece(p).orElse(null);
                return diagonalPieces.size() == 1
                        && diagonalPieces.contains(piece)
                        && piece != null
                        && piece.getPlayer() != getPlayer();
            }

            return true;
        }
        return false;
    }


    @Override
    public boolean isKing() {
        return false;
    }
}
