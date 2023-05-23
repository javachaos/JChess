package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;

public class WhiteCheckState extends AbstractGameState {
    protected WhiteCheckState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof WhitesTurnState)
                || !(game.getPrevState() instanceof BlacksTurnState)) {
            invalidState();
        }

        if (!game.canMove(Player.BLACK) && game.isInCheck(Player.BLACK)) {
            game.setState(new WhiteWinState(game));
        } else if (!game.canMove(Player.BLACK) && !game.isInCheck(Player.BLACK)) {
            game.setState(new StalemateState(game));
        } else if (game.resign(Player.WHITE)) {
            game.setState(new BlackWinState(game));
        } else {
            game.setState(new BlacksTurnState(game));
        }
    }
}
