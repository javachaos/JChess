package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;

public class BlackCheckState extends AbstractGameState {
    protected BlackCheckState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof WhitesTurnState)
                || !(game.getPrevState() instanceof BlacksTurnState)) {
            invalidState();
        }

        if (!game.canMove(Player.WHITE) && game.isInCheck(Player.WHITE)) {
            game.setState(new BlackWinState(game));
        } else if (!game.canMove(Player.WHITE) && !game.isInCheck(Player.WHITE)) {
            game.setState(new StalemateState(game));
        } else if (game.resign(Player.BLACK)) {
            game.setState(new WhiteWinState(game));
        } else {
            game.setState(new BlacksTurnState(game));
        }
    }
}
