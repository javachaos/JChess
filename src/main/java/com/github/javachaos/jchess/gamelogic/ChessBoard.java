package com.github.javachaos.jchess.gamelogic;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.*;
import com.github.javachaos.jchess.gamelogic.player.AIPlayer;
import com.github.javachaos.jchess.gamelogic.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;
import com.github.javachaos.jchess.utils.Constants;
import com.github.javachaos.jchess.utils.ExceptionUtils;

import com.github.javachaos.jchess.utils.PieceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
    private final List<SimplePiece> capturedPieces;
    private final AIPlayer ai;

    private PiecePos enpassant;

    private int halfMoveClock;
    private int fullMoveNumber;

    private final boolean[] castleRights = new boolean[4];

    private Player activePlayer;


    public ChessBoard(AIPlayer ai) {
        castleRights[0] = true; // K
        castleRights[1] = true; // Q
        castleRights[2] = true; // k
        castleRights[3] = true; // q
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
            capturedPieces.add(captive.toSimple());
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
            Piece p = PieceFactory.createPiece(m.type(), m.color(), f);
            assert p != null;
            currentPieces.add(p);
            capturedPieces.remove(p.toSimple());
        }
        fromPiece.ifPresent(piece -> piece.move(t));
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
        parseFenAndUpdate(Constants.START_FEN);
    }

    @Override
    public void applyFen(String fen) {
        if (fen == null || fen.isEmpty()) {
            throw new IllegalArgumentException("FEN string must not be null or empty.");
        }
        parseFenAndUpdate(fen);
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

    public List<SimplePiece> getCapturedPieces() {
        return capturedPieces;
    }

    public void setCapturedPieces(List<SimplePiece> capturedPieces) {
        this.capturedPieces.clear();
        this.capturedPieces.addAll(capturedPieces);
    }

    public List<PiecePos> getAllPositions() {
        return allPositions;
    }

    /**
     * Clear the current board of all pieces.
     * Parse the input fen string.
     * Add pieces to the board according to fen string
     * and update board logic.
     *
     * @param fen a valid fen string
     */
    private void parseFenAndUpdate(String fen) {
        String fenRegex =
                "^(?:[rnbqkpRNBQKP1-8]{1,8}/){7}[rnbqkpRNBQKP1-8]{1,8} " +// board
                        "[wb] " + // active player
                        "(?:[KQkq-]{1,4} )?" + // Castle Rights
                        "(?:[a-h][1-8]|-)" + // Enpassant square
                        "\\s+" +
                        "(\\d+)" + // half move count
                        "\\s+" +
                        "(\\d+)" + // full move count
                        "\\s*$";

        if (!fen.matches(fenRegex)) {
            throw new IllegalArgumentException("Invalid fen string!");
        }
        currentPieces.clear();
        allPositions.clear();
        IntStream.range(0, 8).forEach(x ->
                IntStream.range(0, 8).forEach(y ->
                        allPositions.add(new PiecePos((char) ('a' + x), (char) ('1' + y)))));
        String[] parts = fen.split(" ");

        // Parse piece placement
        String[] ranks = parts[0].split("/");
        for (int rank = 0; rank < ranks.length; rank++) {
            String fenRank = ranks[rank];
            int file = 0;
            for (int i = 0; i < fenRank.length(); i++) {
                char c = fenRank.charAt(i);
                if (Character.isDigit(c)) {
                    int emptySquares = Character.getNumericValue(c);
                    file += emptySquares;
                } else {
                    addPiece(PieceFactory.fromSimple(new SimplePiece(c,
                            new PiecePos((char) ('a' + file),
                                    (char) ('8' - rank)))));
                    file++;
                }
            }
        }

        // Parse active color, castling rights,
        // en passant, halfmove clock,
        // and fullmove number
        String activeColor = parts[1];
        String castlingRights = parts[2];
        String enPassantSquare = parts[3];
        halfMoveClock = Integer.parseInt(parts[4]);
        fullMoveNumber = Integer.parseInt(parts[5]);

        // Update castling rights based on the FEN string
        boolean whiteKingside = castlingRights.contains("K");
        boolean whiteQueenside = castlingRights.contains("Q");
        boolean blackKingside = castlingRights.contains("k");
        boolean blackQueenside = castlingRights.contains("q");

        // Store the castling rights
        castleRights[0] = whiteKingside;
        castleRights[1] = whiteQueenside;
        castleRights[2] = blackKingside;
        castleRights[3] = blackQueenside;

        if (activeColor.equals("w")) {
            activePlayer = Player.WHITE;
        } else if (activeColor.equals("b")) {
            activePlayer = Player.BLACK;
        } else {
            activePlayer = Player.NONE;
        }

        // Check if en passant is possible
        boolean enPassantPossible = !enPassantSquare.equals("-");

        if (enPassantPossible) {
            // Parse the file and rank of the en passant square
            char file = enPassantSquare.charAt(0);
            char rank = enPassantSquare.charAt(1);

            // Store the en passant square
            enpassant = new PiecePos(file, rank);
        } else {
            enpassant = null;
        }
    }

    @SuppressWarnings("all")
    public String getFenString() {
        StringBuilder fenBuilder = new StringBuilder();

        // Iterate over ranks in reverse order
        for (int rank = 7; rank >= 0; rank--) {
            int emptySquareCount = 0;

            // Iterate over files
            for (int file = 0; file < 8; file++) {
                PiecePos pos = new PiecePos((char) ('a' + file), (char) ('1' + rank));
                Optional<Piece> piece = getPiece(pos);

                if (piece.isPresent()) {
                    // Append the piece symbol
                    if (emptySquareCount > 0) {
                        fenBuilder.append(emptySquareCount);
                        emptySquareCount = 0;
                    }
                    fenBuilder.append(PieceFactory.getPieceFENSymbol(piece.get()));
                } else {
                    // Empty square
                    emptySquareCount++;
                }
            }

            // Append the empty square count or slash
            if (emptySquareCount > 0) {
                fenBuilder.append(emptySquareCount);
            }
            if (rank > 0) {
                fenBuilder.append("/");
            }
        }

        fenBuilder.append(" ");
        fenBuilder.append(GSM.instance().getTurn() == Player.WHITE ? "w" : "b");
        fenBuilder.append(" ");

        // Append the castling rights for white
        boolean whiteCanCastleKingSide = castleRights[0];
        boolean whiteCanCastleQueenSide = castleRights[1];
        fenBuilder.append(whiteCanCastleKingSide ? "K" : "");
        fenBuilder.append(whiteCanCastleQueenSide ? "Q" : "");

        boolean blackCanCastleKingSide = castleRights[2];
        boolean blackCanCastleQueenSide = castleRights[3];
        fenBuilder.append(blackCanCastleKingSide ? "k" : "");
        fenBuilder.append(blackCanCastleQueenSide ? "q" : "");

        if (!(whiteCanCastleKingSide || whiteCanCastleQueenSide || blackCanCastleKingSide || blackCanCastleQueenSide)) {
            fenBuilder.append("-");
        }

        fenBuilder.append(" ");
        fenBuilder.append(enpassant == null ? "-" : enpassant.toString());
        fenBuilder.append(" ");
        fenBuilder.append(halfMoveClock);
        fenBuilder.append(" ");
        fenBuilder.append(fullMoveNumber);


        return fenBuilder.toString();
    }

    public int boardScore() {
        int whiteScore = 0;
        int blackScore = 0;

        for (Piece p : currentPieces) {
            int score = switch (p.getType()) {
                case PAWN -> 1;
                case ROOK -> 5;
                case BISHOP, KNIGHT -> 3;
                case KING -> 20;
                case NONE -> 0;
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

    @Override
    public Player getActivePlayer() {
        return activePlayer;
    }
}
