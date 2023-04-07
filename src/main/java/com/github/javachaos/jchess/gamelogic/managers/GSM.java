package com.github.javachaos.jchess.gamelogic.managers;

import com.github.javachaos.jchess.gamelogic.pieces.core.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameStateManager {

    private static final Logger LOGGER = LogManager.getLogger(GameStateManager.class);
    private static GameStateManager instance;
    private Player currentPlayer;
    private GameState currentState;
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

    public void changeTurns() {
        LOGGER.info("Turn: {}", currentPlayer);
        if (getTurn() == Player.WHITE) {
            setCurrentPlayer(Player.BLACK);
        } else {
            setCurrentPlayer(Player.WHITE);
        }
    }

    public Player getTurn() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public boolean isAITurn() {
        //TODO Consider adding an initializer here to choose which color the AI is.
        return currentPlayer == Player.BLACK;
    }

    public boolean isPlayerTurn() {
        return !isAITurn();
    }

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
        GAMEOVER,
        NONE
    }
}
