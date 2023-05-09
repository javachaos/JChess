package com.github.javachaos.jchess.gamelogic.ai.player;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.Alerts;
import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ai.trees.GameTree;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * A chess player which uses the Minimax algorithm to play chess.
 */
public class MinimaxAIPlayer extends AbstractAIPlayer implements AIPlayer {

    public static final Logger LOGGER = LogManager.getLogger(MinimaxAIPlayer.class);

    private GameTree tree;

    public MinimaxAIPlayer(Player c, ChessGame game) {
        super(c);
        tree = new GameTree(game);
    }

    @Override
    public Move getNextMove(Board b) {
        Move m = tree.getAIMove(b, getColor());
        if (m == null) {
            m = getMove(b);
            LOGGER.info("Tree returned null, getting move.");
        }
        return m;
    }

    public Move getMove(Board b) {
        Map<Move, Integer> moveScores = new HashMap<>();
        for (Move m : getAllPossibleMoves(b)) {
            try {
                b.movePiece(m.from(), m.to());
            } catch (JChessException e) {
                ExceptionUtils.log(e);
                continue;
            }
            int score = b.boardScore(getColor());

            //GSM.instance().undo();
            moveScores.put(m, score);
        }

        if (moveScores.isEmpty()) {
            if (b.isInCheck(getColor())) {
                Alerts.info("GAME OVER. CHECKMATE");
                //GSM.instance().setState(GSM.GameState.CHECKMATE);
            } else {
                Alerts.info("GAME OVER. STALEMATE");
                //GSM.instance().setState(GSM.GameState.STALEMATE);
            }
            return Move.empty();
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
    private List<Move> getAllPossibleMoves(Board b) {
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
