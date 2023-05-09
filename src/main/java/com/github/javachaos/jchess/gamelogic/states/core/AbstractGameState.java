package com.github.javachaos.jchess.gamelogic.states.core;

import com.github.javachaos.jchess.exceptions.StateTransitionException;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractGameState implements GameState {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected final ChessGame game;

    protected AbstractGameState(ChessGame game) {
        this.game = game;
    }

    /**
     * Helper method to throw an invalid state exception.
     *
     * @throws StateTransitionException throws on invalid state.
     */
    protected void invalidState() throws StateTransitionException {
        throw new StateTransitionException(game.getPrevState(), this, logger);
    }

    /**
     * Helper function to wait 100 milliseconds.
     */
    protected static void sleep100() {
        try {//release CPU while we wait
            Thread.sleep(100);
        } catch (InterruptedException e) {
            ExceptionUtils.log(e);
            Thread.currentThread().interrupt();
        }
    }

}
