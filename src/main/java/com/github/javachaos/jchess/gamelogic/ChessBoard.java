package com.github.javachaos.jchess.gamelogic;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.AIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;
import com.github.javachaos.jchess.utils.ChessPieceFactory;
import com.github.javachaos.jchess.utils.ExceptionUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;

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

    private PiecePos enpassant;

    private int halfMoveClock;
    private int fullMoveNumber;

    private final boolean[] castleRights = new boolean[4];
    private final List<PiecePos> allPositions;
    /**
     * Captured pieces.
     */
    private final List<Piece> capturedPieces;
    private final AIPlayer ai;

    private Move lastMove;

    private Player activePlayer;

    private static final Map<Character, Function<PiecePos, Piece>> pieceMap = Map.ofEntries(
            Map.entry('r', pos -> new Rook(Player.BLACK, pos.x(), pos.y())),
            Map.entry('n', pos -> new Knight(Player.BLACK, pos.x(), pos.y())),
            Map.entry('b', pos -> new Bishop(Player.BLACK, pos.x(), pos.y())),
            Map.entry('k', pos -> new King(Player.BLACK, pos.x(), pos.y())),
            Map.entry('q', pos -> new Queen(Player.BLACK, pos.x(), pos.y())),
            Map.entry('p', pos -> new Pawn(Player.BLACK, pos.x(), pos.y())),
            Map.entry('R', pos -> new Rook(Player.WHITE, pos.x(), pos.y())),
            Map.entry('N', pos -> new Knight(Player.WHITE, pos.x(), pos.y())),
            Map.entry('B', pos -> new Bishop(Player.WHITE, pos.x(), pos.y())),
            Map.entry('K', pos -> new King(Player.WHITE, pos.x(), pos.y())),
            Map.entry('Q', pos -> new Queen(Player.WHITE, pos.x(), pos.y())),
            Map.entry('P', pos -> new Pawn(Player.WHITE, pos.x(), pos.y()))
    );

    public ChessBoard(AIPlayer ai) {
        castleRights[0] = true;//White can castle
        castleRights[1] = true;//Black can castle
        capturedPieces = new ArrayList<>();
        currentPieces = new ArrayList<>();
        allPositions = new ArrayList<>();
        this.ai = ai;
    }

    public ChessBoard(AIPlayer ai, String fen) {
        this(ai);
        parseFenAndUpdate(fen);
    }

    private ChessBoard(AIPlayer p, List<Piece> capturedPieces,
                       List<Piece> currentPieces, List<PiecePos> allPositions) {
        this.capturedPieces = new ArrayList<>(capturedPieces);
        this.currentPieces = new ArrayList<>(currentPieces);
        this.allPositions = new ArrayList<>(allPositions);
        this.ai = p;
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
                    addPiece(getPieceFromFenCharacter(c,
                            new PiecePos((char) ('a' + file),
                                         (char) ('8' - rank))));
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

    @Override
    public void start() {
        reset();
    }

    @Override
    public Move getAIMove() {
        return ai.getNextMove(this);
    }

    @Override
    public void movePiece(PiecePos pos, PiecePos desiredPos) throws JChessException {
        Optional<Piece> p = getPiece(pos);
        if (p.isPresent()) {
            Piece piece = p.get();

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
                //Check for check
                inCheck(currentMove);
                lastMove = currentMove;
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
                    throw new JChessException("This move puts king in check. " + currentMove);
                }
            }
        }
    }

    @Override
    public boolean isInCheck(Player p) {
        King ourKing = (King) getKing(p);
        for (Piece enemyPiece : getPieces(ourKing.getOpponent())) {
            if (getPotentialMoves(enemyPiece.getPos()).contains(ourKing.getPos())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Piece> getPieces(Player p) {
        return currentPieces.stream().filter(piece ->
                piece.getPlayer().equals(p)).toList();
    }

    @Override
    public void move(Move m) {
    	try {
			movePiece(m.from(), m.to());
		} catch (JChessException e) {
			ExceptionUtils.log(e);
		}
    }

    public void setActivePlayer(Player p) {
        this.activePlayer = p;
    }

    @Override
    public void applyFen(String fenStr) {
        this.parseFenAndUpdate(fenStr);
    }

    public Player getActivePlayer() {
        return activePlayer;
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
            Piece p = ChessPieceFactory.createPiece(m.type(), m.color(), f);
            assert p != null;
            currentPieces.add(p);
            capturedPieces.remove(p);
        }
        fromPiece.ifPresent(piece -> piece.move(t));
    }

    @Override
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

    @Override
    public Optional<Piece> getPiece(char x, char y) {
        return getPiece(new PiecePos(x, y));
    }

    @Override
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

    @Override
    public Move getLastMove() {
        return lastMove;
    }

    @Override
    public boolean isOnBoard(PiecePos p) {
        return isOnBoard(p.x(), p.y());
    }

    @Override
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

    @Override
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
                    fenBuilder.append(getPieceFENSymbol(piece.get()));
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
        fenBuilder.append(activePlayer == Player.WHITE ? "w" : "b");
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

    @Override
    public int boardScore(Player player) {
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
        if (player == Player.WHITE) {
            return  whiteScore - blackScore;
        } else {
            return blackScore - whiteScore;
        }
    }

    public ChessBoard deepCopy() {
        ChessBoard cb = new ChessBoard(ai, capturedPieces, currentPieces, allPositions);
        cb.setLastMove(getLastMove());
        return cb;
    }

    @Override
    public AIPlayer getAI() {
        return ai;
    }

    @Override
    public void clear() {
        currentPieces.clear();
        capturedPieces.clear();
    }

    @Override
    public void remove(Piece captive) {
        currentPieces.remove(captive);
    }

    @Override
    public void addCaptive(Piece captive) {
        //TODO check for duplicated pieces
        capturedPieces.add(captive);
    }

    private void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    private Piece getPieceFromFenCharacter(char c, PiecePos pos) {
        return pieceMap.getOrDefault(c, unused -> {
            throw new IllegalArgumentException("Invalid FEN character: " + c);
        }).apply(pos);
    }

    private char getPieceFENSymbol(Piece piece) {
        char symbol = switch (piece.getType()) {
            case PAWN -> 'P';
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case KING -> 'K';
            case QUEEN -> 'Q';
            default -> ' ';
        };
        return piece.isWhite() ? symbol : Character.toLowerCase(symbol);
    }

}
