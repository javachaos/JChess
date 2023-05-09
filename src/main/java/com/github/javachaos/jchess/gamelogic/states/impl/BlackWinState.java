package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.gamelogic.Alerts;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import javafx.application.Platform;

public class BlackWinState extends AbstractGameState {
    public BlackWinState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof WhiteCheckState)
        || !(game.getPrevState() instanceof BlacksTurnState)
        || !(game.getPrevState() instanceof WhitesTurnState)) {
            invalidState();
        }
        Platform.runLater(() -> Alerts.info("Black Wins."));
        game.setState(new EndState(game));
    }
}
