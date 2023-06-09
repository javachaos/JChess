package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.core.SimplePiece;
import com.github.javachaos.jchess.gamelogic.player.Player;

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

    private List<PiecePos> potentialMoves(ChessBoard b) {
        List<PiecePos> potentials = new ArrayList<>();
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
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
    public boolean canMove(ChessBoard b, PiecePos p) {
        List<PiecePos> possibleMoves = potentialMoves(b);
        if (possibleMoves.contains(p)) {
            List<Piece> pieces = getPiecesDiagonal(b, getPos(), p);
            pieces.remove(this);
            if (!pieces.isEmpty()) {
                Piece piece = b.getPiece(p).orElse(null);
                return pieces.size() == 1
                        && pieces.contains(piece)
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

    @Override
    public SimplePiece toSimple() {
        return new SimplePiece(getPlayer() == Player.WHITE ? 'B' : 'b', getPos());
    }

}
