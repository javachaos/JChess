package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.core.SimplePiece;
import com.github.javachaos.jchess.gamelogic.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Knight extends AbstractPiece {

    public Knight(Player p, char x, char y) {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.KNIGHT;
    }

    private List<PiecePos> potentialMoves(ChessBoard b) {
        ArrayList<PiecePos> potentials = new ArrayList<>();
        int[][] offsets = {
                {-2, 1},
                {-1, 2},
                {1, 2},
                {2, 1},
                {2, -1},
                {1, -2},
                {-1, -2},
                {-2, -1}
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
    public boolean canMove(ChessBoard b, PiecePos p) {
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

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public SimplePiece toSimple() {
        return new SimplePiece(getPlayer() == Player.WHITE ? 'N' : 'n', getPos());
    }
}
