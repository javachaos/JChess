package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.Alerts;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import com.github.javachaos.jchess.gamelogic.states.core.GameState;
import javafx.application.Platform;

public class WhiteWinState extends AbstractGameState {
    public WhiteWinState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof BlackCheckState)
                || !(game.getPrevState() instanceof BlacksTurnState)
                || !(game.getPrevState() instanceof WhitesTurnState)) {
            invalidState();
        }
        game.setState(new EndState(game));
    }

}
