package com.github.javachaos.jchess.gamelogic;


import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

/**
 * Defines a simple 8x8 chess board.
 */
public class Board {

    private final HashMap<AbstractPiece.PieceType,
            Pair<Image, Image>> images = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(
            Board.class);
    private final ArrayDeque<Move> undoStack = new ArrayDeque<>();
    private final ArrayDeque<Move> redoStack = new ArrayDeque<>();


    @SuppressWarnings("unused")
    public enum GameState {
        START,
        STALEMATE,
        MATE,
        BLACKS_TURN,
        WHITES_TURN,
        UNDO,
        REDO,
        NONE
    }

    /**
     * The current pieces in play.
     */
    private final List<Piece> currentPieces = new ArrayList<>();

    private final List<PiecePos> allPositions = new ArrayList<>();

    private GameState currentState;


    /**
     * Captured pieces.
     */
    @SuppressWarnings("all")
    private final ArrayDeque<Piece> capturedPieces = new ArrayDeque<>();

    @SuppressWarnings("unused")
    private static boolean isCheck;

    AbstractPiece.Player currentPlayer;

    public Board() throws JChessException {
        currentState = GameState.NONE;
        reset();
        loadImages();
    }

    public void movePiece(PiecePos pos, PiecePos desiredPos) {
        Optional<Piece> p = getPiece(pos);
        if (p.isPresent()) {
            currentState = p.get().isWhite() ? GameState.WHITES_TURN : GameState.BLACKS_TURN;
            if (!p.get().canMove(this, desiredPos)) {
                //Alerts.err("Sorry this move is invalid.");
                LOGGER.debug("Invalid move for player {}: {}",p.get().getPlayer(), desiredPos);
            } else {
                Move currentMove = new Move(p.get().getPlayer(), pos, desiredPos, p.get());
                doMove(currentMove);
                undoStack.push(currentMove);
            }
        } else {
            Alerts.err("No piece at this position");
            LOGGER.info("Invalid move, piece does not exist at {}", pos);
        }
    }

    public List<Piece> getPieces() {
        return List.copyOf(currentPieces);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            currentState = GameState.UNDO;
            Move lastMove = undoStack.pop();
            undoMove(lastMove.reverse());
            redoStack.push(lastMove);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            currentState = GameState.REDO;
            Move lastMove = redoStack.pop();
            doMove(lastMove);
            undoStack.push(lastMove);
        }
    }

    /**
     * Helper function to move pieces on this board.
     *
     * @param m the move to do
     */
    private void doMove(Move m) {
        currentPlayer = m.player();
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        Optional<Piece> toPiece = getPiece(t);
        if (toPiece.isPresent() && !toPiece.get().isKing()) {
            toPiece.get().capture();
            currentPieces.remove(toPiece.get());
            capturedPieces.add(toPiece.get());
        }
        fromPiece.ifPresent(piece -> piece.move(t));
    }

    private void undoMove(Move m) {
        currentPlayer = m.player();
        PiecePos f = m.from();
        PiecePos t = m.to();
        Optional<Piece> fromPiece = getPiece(f);
        Optional<Piece> toPiece = getPiece(t);
        if (toPiece.isPresent() && !toPiece.get().isKing()) {
            toPiece.get().resurrect();
            currentPieces.add(toPiece.get());
            capturedPieces.remove(toPiece.get());
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
    public void reset() throws JChessException {
        allPositions.clear();
        IntStream.range(0, 8).forEach(x ->
                IntStream.range(0, 8).forEach(y ->
                        allPositions.add(new PiecePos((char)('a' + x), (char)('1' + y)))));
        currentState = GameState.NONE;
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

    private InputStream getImg(String name) {
        return getClass().getResourceAsStream("/img/" + name + ".png");
    }

    private void loadImages() {
        images.put(AbstractPiece.PieceType.PAWN, new Pair<>(
               new Image(getImg("pawn_white")),
               new Image(getImg("pawn_black"))));
        images.put(AbstractPiece.PieceType.BISHOP, new Pair<>(
                new Image(getImg("bishop_white")),
                new Image(getImg("bishop_black"))));
        images.put(AbstractPiece.PieceType.ROOK, new Pair<>(
                new Image(getImg("rook_white")),
                new Image(getImg("rook_black"))));
        images.put(AbstractPiece.PieceType.KNIGHT, new Pair<>(
                new Image(getImg("knight_white")),
                new Image(getImg("knight_black"))));
        images.put(AbstractPiece.PieceType.KING, new Pair<>(
                new Image(getImg("king_white")),
                new Image(getImg("king_black"))));
        images.put(AbstractPiece.PieceType.QUEEN, new Pair<>(
               new Image(getImg("queen_white")),
               new Image(getImg("queen_black"))));
    }

    public void printBoardState() {
        char[][] board = new char[8][8];
        for (Piece piece : currentPieces) {
            int row = piece.getPos().x() - 'a';
            int col = piece.getPos().y() - '1';
            board[row][col] = typeToChar(piece.getPlayer(), piece.getType());
        }
        StringBuilder line = new StringBuilder();
            line.append(System.lineSeparator())
                .append("---------------")
                .append(System.lineSeparator());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                line.append(board[i][j] == 0 ? "." : board[i][j]).append(" ");
            }
            line.append(System.lineSeparator());
        }
        line.append("---------------").append(System.lineSeparator());
        LOGGER.info("{}", line.toString());
    }

    private char typeToChar(AbstractPiece.Player p, AbstractPiece.PieceType t) {
        switch (t) {
            case PAWN -> {
                if (p == AbstractPiece.Player.WHITE) {
                    return 'p';
                } else {
                    return 'P';
                }
            }
            case ROOK -> {
                if (p == AbstractPiece.Player.BLACK) {
                    return 'R';
                } else {
                    return 'r';
                }
            }
            case BISHOP -> {
                if (p == AbstractPiece.Player.BLACK) {
                    return 'B';
                } else {
                    return 'b';
                }
            }
            case KNIGHT -> {
                if (p == AbstractPiece.Player.BLACK) {
                    return 'N';
                } else {
                    return 'n';
                }
            }
            case KING -> {
                if (p == AbstractPiece.Player.BLACK) {
                    return 'K';
                } else {
                    return 'k';
                }
            }
            case QUEEN -> {
                if (p == AbstractPiece.Player.BLACK) {
                    return 'Q';
                } else {
                    return 'q';
                }
            }
        }
        return '.';
    }

    public ImageView getImageForPiece(Piece p) {
        if (p.isBlack()) {
            return new ImageView(images.get(p.getType()).getValue());
        } else {
            return new ImageView(images.get(p.getType()).getKey());
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }

    public boolean isOnBoard(PiecePos p) {
        return isOnBoard(p.x(), p.y());
    }

    public boolean isOnBoard(char x, char y) {
        return (x <= 'h' && x >= 'a') && (y <= '8' && y >= '1');
    }

    public static void check() {
        isCheck = true;
    }

    @SuppressWarnings("unused")
    public static void uncheck() {
        isCheck = false;
    }

}
