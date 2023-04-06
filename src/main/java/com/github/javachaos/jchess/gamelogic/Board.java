package com.github.javachaos.jchess.gamelogic;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import com.github.javachaos.jchess.exceptions.JChessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.impl.Bishop;
import com.github.javachaos.jchess.gamelogic.pieces.impl.King;
import com.github.javachaos.jchess.gamelogic.pieces.impl.Knight;
import com.github.javachaos.jchess.gamelogic.pieces.impl.Pawn;
import com.github.javachaos.jchess.gamelogic.pieces.impl.Queen;
import com.github.javachaos.jchess.gamelogic.pieces.impl.Rook;

import static com.github.javachaos.jchess.gamelogic.GameStateManager.GameState.*;

/**
 * Defines a simple 8x8 chess board.
 */
public class Board {

    private static final Logger LOGGER = LogManager.getLogger(
            Board.class);
    private final ArrayDeque<Move> undoStack = new ArrayDeque<>();
    private final ArrayDeque<Move> redoStack = new ArrayDeque<>();


    /**
     * The current pieces in play.
     */
    private final List<Piece> currentPieces = new ArrayList<>();

    private final List<PiecePos> allPositions = new ArrayList<>();

    /**
     * Captured pieces.
     */
      private final ArrayDeque<Piece> capturedPieces = new ArrayDeque<>();

    AbstractPiece.Player currentPlayer;

    public Board() {
        //Unused
    }

    public void start() {
        GameStateManager.getInstance().setState(NONE);
        reset();
    }

    public void movePiece(PiecePos pos, PiecePos desiredPos) throws JChessException {
        Optional<Piece> p = getPiece(pos);
        if (p.isPresent()) {
            GameStateManager.getInstance().setState(
                    p.get().isWhite() ? WHITES_TURN : BLACKS_TURN);
            if (!p.get().canMove(this, desiredPos)) {
                LOGGER.debug("Invalid move for player {}: {}",p.get().getPlayer(), desiredPos);
            } else {
                Move currentMove = new Move(p.get().getPlayer(),p.get().getOpponent(),
                        pos, desiredPos, p.get(), null);
                Piece captive = doMove(currentMove);
                currentMove = new Move(p.get().getPlayer(),p.get().getOpponent(),
                        pos, desiredPos, p.get(), captive);
                undoStack.push(currentMove);
                //Check for check
                inCheck(currentMove);
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
        King ourKing = (King) getKing(currentMove.player());
        for (Piece enemyPiece : getPieces(currentMove.opponent())) {
            if (getPotentialMoves(enemyPiece.getPos()).contains(ourKing.getPos())) {
                undo();
                throw new JChessException("This move puts king in check. " + currentMove);
            }
        }
    }

    @SuppressWarnings("unused")
    public List<Piece> getAllPieces() {
        return List.copyOf(currentPieces);
    }

    public List<Piece> getPieces(AbstractPiece.Player p) {
        return List.copyOf(currentPieces.stream().filter(piece ->
                piece.getPlayer().equals(p)).toList());
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            GameStateManager.getInstance().setState(UNDO);
            Move lastMove = undoStack.pop();
            undoMove(lastMove.reverse());
            redoStack.push(lastMove);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            GameStateManager.getInstance().setState(REDO);
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
    private Piece doMove(Move m) {
        LOGGER.info("Attempting move: {}", m);
        Piece captive = null;
        currentPlayer = m.player();
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        Optional<Piece> toPiece = getPiece(t);
        if (toPiece.isPresent() && !toPiece.get().isKing()) {
            toPiece.get().capture();
            captive = toPiece.get();
            currentPieces.remove(captive);
            capturedPieces.add(captive);
        }
        fromPiece.ifPresent(piece -> piece.move(t));
        return captive;
    }

    private void undoMove(Move m) {
        LOGGER.info("Undoing move: {}", m);
        currentPlayer = m.player();
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        if (m.captive() != null) {
            m.captive().resurrect();
            currentPieces.add(m.captive());
            capturedPieces.remove(m.captive());
        }
        fromPiece.ifPresent(piece -> piece.move(t));
    }

    @SuppressWarnings("unused")
    public Piece getKing(AbstractPiece.Player p) {
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
                        allPositions.add(new PiecePos((char)('a' + x), (char)('1' + y)))));
        GameStateManager.getInstance().setState(NONE);
        currentPlayer = AbstractPiece.Player.WHITE;
        undoStack.clear();
        currentPieces.clear();
        currentPieces.addAll(Arrays.asList(
                new Pawn(AbstractPiece.Player.WHITE, 'a', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'b', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'c', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'd', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'e', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'f', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'g', '2'),
                new Pawn(AbstractPiece.Player.WHITE, 'h', '2'),
                new Rook(AbstractPiece.Player.WHITE, 'a', '1'),
                new Rook(AbstractPiece.Player.WHITE, 'h', '1'),
                new Knight(AbstractPiece.Player.WHITE, 'b', '1'),
                new Knight(AbstractPiece.Player.WHITE, 'g', '1'),
                new Bishop(AbstractPiece.Player.WHITE, 'c', '1'),
                new Bishop(AbstractPiece.Player.WHITE, 'f', '1'),
                new Queen(AbstractPiece.Player.WHITE, 'd', '1'),
                new King(AbstractPiece.Player.WHITE, 'e', '1'),

                new Pawn(AbstractPiece.Player.BLACK, 'a', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'b', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'c', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'd', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'e', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'f', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'g', '7'),
                new Pawn(AbstractPiece.Player.BLACK, 'h', '7'),
                new Rook(AbstractPiece.Player.BLACK, 'a', '8'),
                new Rook(AbstractPiece.Player.BLACK, 'h', '8'),
                new Knight(AbstractPiece.Player.BLACK, 'b', '8'),
                new Knight(AbstractPiece.Player.BLACK, 'g', '8'),
                new Bishop(AbstractPiece.Player.BLACK, 'c', '8'),
                new Bishop(AbstractPiece.Player.BLACK, 'f', '8'),
                new Queen(AbstractPiece.Player.BLACK, 'd', '8'),
                new King(AbstractPiece.Player.BLACK, 'e', '8')
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
}
