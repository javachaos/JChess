package com.github.javachaos.jchess.gamelogic.managers;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.ai.player.AIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Represents a game state manager singleton,
 * shortened the name for brevity.
 */
public class GSM {

    private static final Logger LOGGER = LogManager.getLogger(GSM.class);
    private static GSM instance;
    private Player currentPlayer;
    private GameState currentState;

    private final Board board;

    private final Deque<Move> undoStack = new ArrayDeque<>();
    private final Deque<Move> redoStack = new ArrayDeque<>();

    private GSM() {
        this.board = new ChessBoard(new MinimaxAIPlayer(Player.BLACK));
    }

    public static GSM instance() {
        if (instance == null) {
            instance = new GSM();
        }
        return instance;
    }

    public void reset() {
        undoStack.clear();
        redoStack.clear();
        board.reset();
    }

    public Board getBoard() {
        return board;
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Move lastMove = undoStack.pop();
            GSM.instance().changeTurns();
            board.undoMove(lastMove.reverse());
            redoStack.push(lastMove);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Move lastMove = redoStack.pop();
            LOGGER.info("Redoing move: {}", lastMove);
            board.doMove(lastMove);
            undoStack.push(lastMove);
        }
    }

    public void makeMove(Move currentMove) {
        undoStack.push(currentMove);
    }

    public Deque<Move> getUndos() {
        return new ArrayDeque<>(undoStack);
    }

    public void setUndos(Deque<Move> undos) {
        if (undos != null) {
            this.undoStack.clear();
            this.undoStack.addAll(undos);
        }
    }

    public Deque<Move> getRedos() {
        return new ArrayDeque<>(redoStack);
    }

    public void setRedos(Deque<Move> redos) {
        if (redos != null) {
            this.redoStack.clear();
            this.redoStack.addAll(redos);
        }
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
            setTurn(Player.BLACK);
        } else {
            setTurn(Player.WHITE);
        }
    }

    public Player getTurn() {
        return currentPlayer;
    }

    /**
     * Set the current player.
     * @param player the players whose turn it is.
     */
    public void setTurn(Player player) {
        this.currentPlayer = player;
    }

    public boolean isAITurn() {
        return currentPlayer == getAI().getColor();
    }

    public boolean isPlayerTurn() {
        return !isAITurn();
    }

    public AIPlayer getAI() {
        return board.getAI();
    }

    public enum GameState {
        START,
        STALEMATE,
        CHECKMATE,
        WHITE_CHECK,
        BLACK_CHECK,
        BLACKS_TURN,
        WHITES_TURN,
        GAMEOVER,
        NONE
    }
}
