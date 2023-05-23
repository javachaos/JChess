package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;

public class StartState extends AbstractGameState {
    public StartState(ChessGame chessGame) {
        super(chessGame);
    }

    @Override
    public void handle() {
        game.resetGame();
        game.setState(new WhitesTurnState(game));
    }

}
