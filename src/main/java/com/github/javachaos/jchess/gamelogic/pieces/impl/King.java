package com.github.javachaos.jchess.gamelogic.pieces.impl;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;

import java.util.ArrayList;
import java.util.List;

public class King extends AbstractPiece {

    public King(Player p, char x, char y) {
        super(p, x, y);
    }

    @Override
    public PieceType getType() {
        return PieceType.KING;
    }


    private List<PiecePos> potentialMoves(Board b) {
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
        return true;
    }

}
