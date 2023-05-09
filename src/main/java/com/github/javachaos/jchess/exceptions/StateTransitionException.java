package com.github.javachaos.jchess.exceptions;

import com.github.javachaos.jchess.gamelogic.states.core.GameState;
import org.apache.logging.log4j.Logger;

public class StateTransitionException extends IllegalStateException {
    public StateTransitionException(GameState from, GameState to, Logger logger) {
        super("Invalid state transition: [from: " + from + ", to: " + to + "]");
        logger.error(getMessage());
    }
}
