package com.github.javachaos.jchess.gamelogic;

import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;

public class GameStateManager {

    private AbstractPiece.Player currentPlayer;


    @SuppressWarnings("unused")
    public enum GameState {
        START,
        STALEMATE,
        MATE,
        WHITE_CHECK,
        BLACK_CHECK,
        BLACKS_TURN,
        WHITES_TURN,
        UNDO,
        REDO,
        NONE
    }

    private GameState currentState;
    private static GameStateManager instance;

    private GameStateManager() {
        //Unused
    }

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public boolean isStart() {
        return currentState == GameState.START;
    }

    public void setState(GameState s) {
        currentState = s;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentPlayer(AbstractPiece.Player player) {
        this.currentPlayer = player;
    }

    public AbstractPiece.Player getCurrentPlayer() {
        return currentPlayer;
    }
}
