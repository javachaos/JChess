package com.github.javachaos.jchess.gamelogic.pieces.core.player;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;

import java.util.*;

/**
 * A chess player which uses the Minimax algorithm to play chess.
 */
public class MinimaxAIPlayer extends AbstractAIPlayer implements AIPlayer {

    private List<Move> allPossibleMoves = new ArrayList<>();

    public MinimaxAIPlayer(Player c) {
        super(c);
    }


    @Override
    public Move getNextMove(Board b) {
        Map<Move, Integer> moveScores = new HashMap<>();
        getAllPossibleMoves(b);
        for (Move m : allPossibleMoves) {
            b.move(m);
            int score = b.boardScore();
            //TODO Figure out why we are losing pieces here.
            b.undo();
            moveScores.put(m, score);
        }
        if (getColor() == Player.WHITE) {
            return Collections.max(moveScores.entrySet(),
                    Comparator.comparingInt(Map.Entry::getValue)).getKey();
        } else {
            return Collections.min(moveScores.entrySet(),
                    Comparator.comparingInt(Map.Entry::getValue)).getKey();
        }
    }

    @Override
    public List<Piece> getCapturedPieces() {
        return null;
    }

    @Override
    public Player getOpponent() {
        return null;
    }

    /**
     * Return a list of all potential moves for this player
     * given the board b.
     *
     * @param b the board
     */
    private void getAllPossibleMoves(Board b) {
        allPossibleMoves.clear();
        for (Piece p : b.getPieces(getColor())) {
            b.getPotentialMoves(p.getPos()).forEach(pot -> {
                AbstractPiece.PieceType type = AbstractPiece.PieceType.NONE;
                Optional<Piece> op = b.getPiece(pot);
                if (op.isPresent()) {
                    type = op.get().getType();
                }
                allPossibleMoves.add(new Move(p.getPos(), pot, type, getColor()));
            });
        }
    }
}
