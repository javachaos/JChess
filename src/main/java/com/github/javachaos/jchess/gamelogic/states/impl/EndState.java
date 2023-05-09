package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;

public class EndState extends AbstractGameState {

    public EndState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof WhiteWinState)
                || !(game.getPrevState() instanceof BlackWinState)
                || !(game.getPrevState() instanceof StalemateState)) {
            invalidState();
        }

        if (game.requestRematch()) {
            game.setState(new StartState(game));
        } else {
            game.clearBoard();
        }
    }
}
