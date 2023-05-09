package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.gamelogic.Alerts;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import javafx.application.Platform;

public class StalemateState extends AbstractGameState {
    protected StalemateState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof BlackCheckState)
                || !(game.getPrevState() instanceof WhiteCheckState)) {
            invalidState();
        }

        Platform.runLater(() -> Alerts.info("Draw."));
        game.setState(new EndState(game));
    }

}
