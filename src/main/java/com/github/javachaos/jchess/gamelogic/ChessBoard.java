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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.github.javachaos.jchess.gamelogic.managers.GSM.GameState.*;

/**
 * Defines a simple 8x8 chess board.
 */
public class ChessBoard implements Board {
    private static final Logger LOGGER = LogManager.getLogger(
            ChessBoard.class);
    /**
     * The current pieces in play.
     */
    private final List<Piece> currentPieces;
    private final List<PiecePos> allPositions;
    /**
     * Captured pieces.
     */
    private final List<Piece> capturedPieces;
    private final AIPlayer ai;

    public ChessBoard(AIPlayer ai) {
        capturedPieces = new ArrayList<>();
        currentPieces = new ArrayList<>();
        allPositions = new ArrayList<>();
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
            Piece piece = p.get();
            if (GSM.instance().getTurn() != piece.getPlayer()) {
                throw new JChessException("Not your turn.");
            }

            if (!piece.canMove(this, desiredPos)) {
                LOGGER.debug("Invalid move for player {}: {}",
                        piece.getPlayer(), desiredPos);
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
            }
        } else {
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
                    GSM.instance().undo(this);
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

    @Override
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

    @Override
    public void undoMove(Move m) {
        LOGGER.info("Undoing move: {}", m);
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        if (m.type() != AbstractPiece.PieceType.NONE) {
            Piece p = createPiece(m.type(), m.color(), f);
            assert p != null;
            currentPieces.add(p);
            capturedPieces.remove(p);
        }
        fromPiece.ifPresent(piece -> piece.move(t));
    }

    private Piece createPiece(AbstractPiece.PieceType type, Player color, PiecePos piecePos) {
        Map<AbstractPiece.PieceType, Supplier<Piece>> pieceMap = Map.of(
                AbstractPiece.PieceType.PAWN, () -> new Pawn(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.ROOK, () -> new Rook(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.BISHOP, () -> new Bishop(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.KNIGHT, () -> new Knight(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.KING, () -> new King(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.QUEEN, () -> new Queen(color, piecePos.x(), piecePos.y())
        );

        return pieceMap.getOrDefault(type, () -> null).get();
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


    @Override
    public void reset() {
        allPositions.clear();
        IntStream.range(0, 8).forEach(x ->
                IntStream.range(0, 8).forEach(y ->
                        allPositions.add(new PiecePos((char) ('a' + x), (char) ('1' + y)))));
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

    public void addPiece(Piece piece) {
        if (isOnBoard(piece.getPos())
                && getPiece(piece.getPos()).isEmpty()) {
            currentPieces.add(piece);
        }
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

    public int boardScore() {
        int whiteScore = 0;
        int blackScore = 0;

        for (Piece p : currentPieces) {
            int score = switch (p.getType()) {
                case PAWN -> 1;
                case ROOK -> 5;
                case BISHOP, KNIGHT -> 3;
                case KING, NONE -> 0;
                case QUEEN -> 9;
            };

            if (p.isWhite()) {
                whiteScore += score;
            } else {
                blackScore += score;
            }
        }
        return whiteScore - blackScore;
    }

}
