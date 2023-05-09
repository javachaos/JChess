package com.github.javachaos.jchess.gamelogic.ai.player;

public abstract class AbstractAIPlayer implements AIPlayer {

    private final Player player;

    protected AbstractAIPlayer(Player c) {
        player = c;
    }

    @Override
    public Player getColor() {
        return player;
    }

    @Override
    public Player getOpponent() {
        return player == Player.BLACK ? Player.WHITE : Player.BLACK;
    }
}
