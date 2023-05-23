package com.github.javachaos.jchess.gamelogic.ai.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ai.trees.GameTree;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;

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
        return m;
    }
}
