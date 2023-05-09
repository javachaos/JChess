package com.github.javachaos.jchess.gamelogic.states.core;

import com.github.javachaos.jchess.JChessController;
import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.Alerts;
import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.impl.King;
import com.github.javachaos.jchess.gamelogic.states.impl.EndState;
import com.github.javachaos.jchess.gamelogic.states.impl.StartState;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import javafx.application.Platform;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChessGame {
    private GameState currentState;
    private GameState prevState;

    private final JChessController controller;

    private final Deque<Move> undoStack;
    private final Deque<Move> redoStack;
    private Move lastMove;

    /**
     * Total time for each player. 15mins
     */
    private static final int TOTAL_TIME_MILLIS = (1000 * 60 * 15);

    private int whiteRemainTimeMillis;
    private int blackRemainTimeMillis;

    private final Board board;

    public ChessGame(JChessController controller) {
        this.controller = controller;
        this.currentState = new StartState(this);
        this.prevState = new EndState(this);
        undoStack = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
        board = new ChessBoard(new MinimaxAIPlayer(Player.BLACK));
        resetTimers();
    }

    private void resetTimers() {
        whiteRemainTimeMillis = TOTAL_TIME_MILLIS;
        blackRemainTimeMillis = TOTAL_TIME_MILLIS;
    }

    public int getWhiteRemainTimeMillis() {
        return whiteRemainTimeMillis;
    }

    public int getBlackRemainTimeMillis() {
        return blackRemainTimeMillis;
    }

    public void undo() {
        currentState = prevState;
        lastMove = undoStack.pop();
        redoStack.push(lastMove);
    }

    public void redo() {
        prevState = currentState;
        lastMove = redoStack.pop();
        undoStack.push(lastMove);
    }

    public void setState(GameState state) {
        this.prevState = currentState;
        this.currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameState getPrevState() {
        return prevState;
    }

    public boolean requestRematch() {
        return Alerts.yesNoPrompt("Would you like a rematch?");
    }

    public boolean resign(Player p) {
        //TODO Check if current player has chosen to resign.
        return false;
    }

    public boolean isInCheck(Player player) {
        return board.isInCheck(player);
    }

    public boolean canMove(Player p) {
        AtomicBoolean canMove = new AtomicBoolean(true);
        board.getPieces(p).forEach(piece -> board.getAllPositions().forEach(
                pp -> canMove.set(canMove.get() && piece.canMove(board, pp))));
        return canMove.get();
    }

    public boolean isCheckmate(Player player) {
        return !canMove(player) && isInCheck(player);
    }

    public boolean isStalemate(Player player) {
        return !canMove(player) && !isInCheck(player);
    }

    public void resetGame() {
        resetTimers();
        clearUndo();
        clearRedo();
        resetBoard();
    }

    public void resetBoard() {
        board.reset();
    }

    /**
     * Clear the chess board
     */
    public void clearBoard() {
        board.clear();
    }


    /**
     * Clear the undo stack
     */
    public void clearUndo() {
        undoStack.clear();
    }

    public void clearRedo() {
        redoStack.clear();
    }

    public void movePiece(PiecePos pos, PiecePos desiredPos) throws JChessException {
        Optional<Piece> p = board.getPiece(pos);
        if (p.isPresent()) {
            Piece piece = p.get();
            if (GSM.instance().getTurn() != piece.getPlayer()) {
                throw new JChessException("Not your turn.");
            }

            if (!piece.canMove(board, desiredPos)) {
                //LOG
            } else {
                Move currentMove = new Move(pos, desiredPos,
                        AbstractPiece.PieceType.NONE, piece.getPlayer());
                Piece captive = doMove(currentMove);
                if (captive != null) {
                    currentMove = new Move(
                            pos, desiredPos, captive.getType(), captive.getPlayer());
                }
                GSM.instance().makeMove(currentMove);
                //Check for check
                inCheck(currentMove);
                GSM.instance().changeTurns();
                lastMove = currentMove;
            }
        } else {
            //LOG
        }
    }

    /**
     * Check if the move currentMove would put the player into check.
     *
     * @param currentMove the current move.
     * @throws JChessException if the currentMove would put the player into check
     */
    private void inCheck(Move currentMove) throws JChessException {
        Optional<Piece> p = board.getPiece(currentMove.to());
        if (p.isPresent()) {
            King ourKing = (King) board.getKing(p.get().getPlayer());
            for (Piece enemyPiece : board.getPieces(p.get().getOpponent())) {
                if (board.getPotentialMoves(enemyPiece.getPos()).contains(ourKing.getPos())) {
                    GSM.instance().undo();
                    GSM.instance().changeTurns();
                    throw new JChessException("This move puts king in check. " + currentMove);
                }
            }
        }
    }

    private Piece doMove(Move m) {
        Piece captive = null;
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = board.getPiece(f);
        Optional<Piece> toPiece = board.getPiece(t);
        if (toPiece.isPresent() && !toPiece.get().isKing()) {
            captive = toPiece.get();
            board.remove(captive);
            board.addCaptive(captive);
        }
        fromPiece.ifPresent(piece -> piece.move(t));
        return captive;
    }

    public Board getBoard() {
        return board;
    }

    public JChessController getController() {
        return controller;
    }
}

