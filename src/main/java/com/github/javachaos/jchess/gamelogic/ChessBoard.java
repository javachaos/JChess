package com.github.javachaos.jchess.gamelogic;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.core.player.AIPlayer;
import com.github.javachaos.jchess.gamelogic.pieces.core.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;
import com.github.javachaos.jchess.utils.ExceptionUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static com.github.javachaos.jchess.gamelogic.managers.GSM.GameState.*;

/**
 * Defines a simple 8x8 chess board.
 */
public class Board {

    private static final Logger LOGGER = LogManager.getLogger(
            Board.class);
    private final Deque<Move> undoStack = new ArrayDeque<>();
    private final Deque<Move> redoStack = new ArrayDeque<>();
    /**
     * The current pieces in play.
     */
    private final List<Piece> currentPieces = new ArrayList<>();
    private final List<PiecePos> allPositions = new ArrayList<>();
    /**
     * Captured pieces.
     */
    private final ArrayDeque<Piece> capturedPieces = new ArrayDeque<>();
    private final AIPlayer ai;

    public Board(AIPlayer ai) {
        this.ai = ai;
        GSM.instance().setAIColor(ai.getColor());
    }

    public void start() {
        GSM.instance().setState(START);
        reset();
        GSM.instance().setTurn(Player.WHITE);
    }

    public void doAIMove() {
        if (GSM.instance().isAITurn()) {
            Move nextMove = ai.getNextMove(this);
            move(nextMove);
        }
    }

    public void movePiece(PiecePos pos, PiecePos desiredPos) throws JChessException {
        Optional<Piece> p = getPiece(pos);
        if (p.isPresent()) {
            if (GSM.instance().getTurn() != p.get().getPlayer()) {
                throw new JChessException("Not your turn.");
            }

            if (!p.get().canMove(this, desiredPos)) {
                LOGGER.debug("Invalid move for player {}: {}",
                        p.get().getPlayer(), desiredPos);
            } else {
                Move currentMove = new Move(pos, desiredPos,
                        AbstractPiece.PieceType.NONE, p.get().getPlayer());
                Piece captive = doMove(currentMove);
                if (captive != null) {
                    currentMove = new Move(
                            pos, desiredPos, captive.getType(), captive.getPlayer());
                }
                undoStack.push(currentMove);
                //Check for check
                inCheck(currentMove);
                GSM.instance().changeTurns();
            }
        } else {
            Alerts.err("No piece at this position");
            LOGGER.info("Invalid move, piece does not exist at {}", pos);
        }
    }

    /**
     * Check if the move currentMove would put the player into check.
     *
     * @param currentMove the current move.
     * @throws JChessException if the currentMove would put the player into check
     */
    private void inCheck(Move currentMove) throws JChessException {
        Optional<Piece> p = getPiece(currentMove.to());
        if (p.isPresent()) {
            King ourKing = (King) getKing(p.get().getPlayer());
            for (Piece enemyPiece : getPieces(p.get().getOpponent())) {
                if (getPotentialMoves(enemyPiece.getPos()).contains(ourKing.getPos())) {
                    undo();
                    GSM.instance().changeTurns();
                    throw new JChessException("This move puts king in check. " + currentMove);
                }
            }
        }
    }

    public List<Piece> getPieces(Player p) {
        return currentPieces.stream().filter(piece ->
                piece.getPlayer().equals(p)).toList();
    }

    public void move(Move m) {
    	try {
			movePiece(m.from(), m.to());
		} catch (JChessException e) {
			ExceptionUtils.log(e);
		}
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            GSM.instance().setState(UNDO);
            Move lastMove = undoStack.pop();
            GSM.instance().changeTurns();
            undoMove(lastMove.reverse());
            redoStack.push(lastMove);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            GSM.instance().setState(REDO);
            Move lastMove = redoStack.pop();
            LOGGER.info("Redoing move: {}", lastMove);
            doMove(lastMove);
            undoStack.push(lastMove);
        }
    }

    /**
     * Helper function to move pieces on this board.
     *
     * @param m the move to do
     */
    public Piece doMove(Move m) {
        LOGGER.info("Attempting move: {}", m);
        Piece captive = null;
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        Optional<Piece> toPiece = getPiece(t);
        if (toPiece.isPresent() && !toPiece.get().isKing()) {
            captive = toPiece.get();
            currentPieces.remove(captive);
            capturedPieces.add(captive);
        }
        fromPiece.ifPresent(piece -> piece.move(t));
        return captive;
    }

    public void undoMove(Move m) {
        LOGGER.info("Undoing move: {}", m);
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        if (m.type() != AbstractPiece.PieceType.NONE) {
            Piece p = createPiece(m.type(), m.p(), m.to());
            assert p != null;
            currentPieces.add(p);
            capturedPieces.remove(p);
        }
        fromPiece.ifPresent(piece -> piece.move(t));
    }

    private Piece createPiece(AbstractPiece.PieceType type, Player p, PiecePos piecePos) {

        switch (type) {
            case PAWN -> {
                return new Pawn(p, piecePos.x(), piecePos.y());
            }
            case ROOK -> {
                return new Rook(p, piecePos.x(), piecePos.y());
            }
            case BISHOP -> {
                return new Bishop(p, piecePos.x(), piecePos.y());
            }
            case KNIGHT -> {
                return new Knight(p, piecePos.x(), piecePos.y());
            }
            case KING -> {
                return new King(p, piecePos.x(), piecePos.y());
            }
            case QUEEN -> {
                return new Queen(p, piecePos.x(), piecePos.y());
            }
            case NONE -> {
                return null;
            }
        }
        return null;
    }

    public Piece getKing(Player p) {
        AtomicReference<Piece> ref = new AtomicReference<>();
        currentPieces.forEach(piece -> {
            if (piece.isKing() && piece.getPlayer() == p) {
                ref.set(piece);
            }
        });
        return ref.get();
    }

    public Optional<Piece> getPiece(PiecePos p) {
        return currentPieces.stream()
                .filter(x -> x.getPos().equals(p))
                .reduce((a, b) -> a);
    }

    public Optional<Piece> getPiece(char x, char y) {
        return getPiece(new PiecePos(x, y));
    }

    public List<PiecePos> getPotentialMoves(PiecePos pos) {
        List<PiecePos> potentials = new ArrayList<>();
        getPiece(pos).ifPresent(piece -> potentials.addAll(allPositions.stream()
                .filter(piecePos -> piece.canMove(this, piecePos)).toList()));
        return potentials;
    }

    /**
     * Reset the board to the default start state.
     */
    public void reset() {
        allPositions.clear();
        IntStream.range(0, 8).forEach(x ->
                IntStream.range(0, 8).forEach(y ->
                        allPositions.add(new PiecePos((char) ('a' + x), (char) ('8' - y)))));
        undoStack.clear();
        currentPieces.clear();
        currentPieces.addAll(Arrays.asList(
                new Pawn(Player.WHITE, 'a', '2'),
                new Pawn(Player.WHITE, 'b', '2'),
                new Pawn(Player.WHITE, 'c', '2'),
                new Pawn(Player.WHITE, 'd', '2'),
                new Pawn(Player.WHITE, 'e', '2'),
                new Pawn(Player.WHITE, 'f', '2'),
                new Pawn(Player.WHITE, 'g', '2'),
                new Pawn(Player.WHITE, 'h', '2'),
                new Rook(Player.WHITE, 'a', '1'),
                new Rook(Player.WHITE, 'h', '1'),
                new Knight(Player.WHITE, 'b', '1'),
                new Knight(Player.WHITE, 'g', '1'),
                new Bishop(Player.WHITE, 'c', '1'),
                new Bishop(Player.WHITE, 'f', '1'),
                new Queen(Player.WHITE, 'd', '1'),
                new King(Player.WHITE, 'e', '1'),

                new Pawn(Player.BLACK, 'a', '7'),
                new Pawn(Player.BLACK, 'b', '7'),
                new Pawn(Player.BLACK, 'c', '7'),
                new Pawn(Player.BLACK, 'd', '7'),
                new Pawn(Player.BLACK, 'e', '7'),
                new Pawn(Player.BLACK, 'f', '7'),
                new Pawn(Player.BLACK, 'g', '7'),
                new Pawn(Player.BLACK, 'h', '7'),
                new Rook(Player.BLACK, 'a', '8'),
                new Rook(Player.BLACK, 'h', '8'),
                new Knight(Player.BLACK, 'b', '8'),
                new Knight(Player.BLACK, 'g', '8'),
                new Bishop(Player.BLACK, 'c', '8'),
                new Bishop(Player.BLACK, 'f', '8'),
                new Queen(Player.BLACK, 'd', '8'),
                new King(Player.BLACK, 'e', '8')
        ));
    }

    public boolean isOnBoard(PiecePos p) {
        return isOnBoard(p.x(), p.y());
    }

    public boolean isOnBoard(char x, char y) {
        return (x <= 'h' && x >= 'a') && (y <= '8' && y >= '1');
    }

    public void addPiece(Piece a) {
        //Check if location is valid.
        currentPieces.add(a);
    }

    @SuppressWarnings("unused")
    public List<Piece> getWhiteCaptives() {
        return capturedPieces.stream().filter(Piece::isWhite).toList();
    }

    @SuppressWarnings("unused")
    public List<Piece> getBlackCaptives() {
        return capturedPieces.stream().filter(Piece::isBlack).toList();
    }

    public List<PiecePos> getAllPositions() {
        return allPositions;
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

    public int boardScore() {
        //TODO add more heuristics for king position pawn structure ect.

        AtomicInteger whiteScore = new AtomicInteger();
        AtomicInteger blackScore = new AtomicInteger();

        currentPieces.forEach(p -> {
            switch (p.getType()) {
                case PAWN -> {
                    if (p.isWhite()) {
                        whiteScore.addAndGet(1);
                    } else {
                        blackScore.addAndGet(1);
                    }
                }
                case ROOK -> {
                    if (p.isWhite()) {
                        whiteScore.addAndGet(5);
                    } else {
                        blackScore.addAndGet(5);
                    }
                }
                case BISHOP, KNIGHT -> {
                    if (p.isWhite()) {
                        whiteScore.addAndGet(3);
                    } else {
                        blackScore.addAndGet(3);
                    }
                }
                case QUEEN -> {
                    if (p.isWhite()) {
                        whiteScore.addAndGet(9);
                    } else {
                        blackScore.addAndGet(9);
                    }
                }
                default -> {
                }
            }
        });


        return whiteScore.get() - blackScore.get();
    }
}
