package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;

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

    public List<PiecePos> potentialMoves(Board b) {
        List<PiecePos> potentials = new ArrayList<>();
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            PiecePos pp = new PiecePos((char) (getPos().x() + dx), (char) (getPos().y() + dy));
            while (b.isOnBoard(pp)) {
                potentials.add(pp);
                pp = new PiecePos((char) (pp.x() + dx), (char) (pp.y() + dy));
            }
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
}
