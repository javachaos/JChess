package com.github.javachaos.jchess.gamelogic.pieces.core.player;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.utils.ExceptionUtils;

import java.util.*;

/**
 * A chess player which uses the Minimax algorithm to play chess.
 */
public class MinimaxAIPlayer extends AbstractAIPlayer implements AIPlayer {

    public MinimaxAIPlayer(Player c) {
        super(c);
    }

    @Override
    public Move getNextMove(ChessBoard b) {
        Map<Move, Integer> moveScores = new HashMap<>();
        for (Move m : getAllPossibleMoves(b)) {
        	try {
    			b.movePiece(m.from(), m.to());
    		} catch (JChessException e) {
    			ExceptionUtils.log(e);
    			continue;
    		}
            int score = b.boardScore();

            GSM.instance().undo(b);
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

    /**
     * Return a list of all potential moves for this player
     * given the board b.
     *
     * @param b the board
     */
    private List<Move> getAllPossibleMoves(ChessBoard b) {
        List<Move> moves = new ArrayList<>();
        for (Piece p : b.getPieces(getColor())) {
            b.getPotentialMoves(p.getPos()).forEach(pot -> {
                AbstractPiece.PieceType type = AbstractPiece.PieceType.NONE;
                Optional<Piece> op = b.getPiece(pot);
                if (op.isPresent()) {
                    type = op.get().getType();
                }
                moves.add(new Move(p.getPos(), pot, type, getColor()));
            });
        }
        return moves;
    }
}
