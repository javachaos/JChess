package jchess;

import com.github.javachaos.jchess.JChessController;
import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests a simple 8x8 chess board.
 */
public class ChessBoardTest {

    private static final Logger LOGGER = LogManager.getLogger(
            ChessBoardTest.class);

    private static ChessBoard board;

    @BeforeAll
    public static void createBoard() {
        board = new ChessBoard(new MinimaxAIPlayer(Player.BLACK, new ChessGame(new JChessController())));
        board.start();
    }

    @Test
    void testResetBoard() throws JChessException {
        board.movePiece(new PiecePos('a', '2'), new PiecePos('a', '3'));
        board.movePiece(new PiecePos('b', '2'), new PiecePos('b', '4'));
        board.reset();//Works
        testBlackPieces();
        testWhitePieces();
    }

    /**
     * Chess board.
     * +------------------------+
     * 8 | r  n  b  q  k  b  n  r |
     * 7 | color  color  color  color  color  color  color  color |
     * 6 | .  .  .  .  .  .  .  . |
     * 5 | .  .  .  .  .  .  .  . |
     * 4 | .  .  .  .  .  .  .  . |
     * 3 | .  .  .  .  .  .  .  . |
     * 2 | P  P  P  P  P  P  P  P |
     * 1 | R  N  B  Q  K  B  N  R |
     * +------------------------+
     * a  b  c  d  e  f  g  h
     */
    private void testBlackPieces() {
        Optional<Piece> a8 = board.getPiece('a', '8');//Black ROOK
        Optional<Piece> b8 = board.getPiece('b', '8');//Black KNIGHT
        Optional<Piece> c8 = board.getPiece('c', '8');//Black BISHOP
        Optional<Piece> d8 = board.getPiece('d', '8');//Black QUEEN
        Optional<Piece> e8 = board.getPiece('e', '8');//Black KING
        Optional<Piece> f8 = board.getPiece('f', '8');//Black BISHOP
        Optional<Piece> g8 = board.getPiece('g', '8');//Black KNIGHT
        Optional<Piece> h8 = board.getPiece('h', '8');//Black ROOK
        assertTrue(a8.isPresent());
        assertTrue(b8.isPresent());
        assertTrue(c8.isPresent());
        assertTrue(d8.isPresent());
        assertTrue(e8.isPresent());
        assertTrue(f8.isPresent());
        assertTrue(g8.isPresent());
        assertTrue(h8.isPresent());
        assertEquals(AbstractPiece.PieceType.ROOK, a8.get().getType());
        assertEquals(AbstractPiece.PieceType.KNIGHT, b8.get().getType());
        assertEquals(AbstractPiece.PieceType.BISHOP, c8.get().getType());
        assertEquals(AbstractPiece.PieceType.QUEEN, d8.get().getType());
        assertEquals(AbstractPiece.PieceType.KING, e8.get().getType());
        assertEquals(AbstractPiece.PieceType.BISHOP, f8.get().getType());
        assertEquals(AbstractPiece.PieceType.KNIGHT, g8.get().getType());
        assertEquals(AbstractPiece.PieceType.ROOK, h8.get().getType());
        assertTrue(a8.get().isBlack());
        assertTrue(b8.get().isBlack());
        assertTrue(c8.get().isBlack());
        assertTrue(d8.get().isBlack());
        assertTrue(e8.get().isBlack());
        assertTrue(f8.get().isBlack());
        assertTrue(g8.get().isBlack());
        assertTrue(h8.get().isBlack());
        testBlackPawns();
    }

    private void testBlackPawns() {
        //---------- Pawns ------------------
        Optional<Piece> a7 = board.getPiece('a', '7');
        Optional<Piece> b7 = board.getPiece('b', '7');
        Optional<Piece> c7 = board.getPiece('c', '7');
        Optional<Piece> d7 = board.getPiece('d', '7');
        Optional<Piece> e7 = board.getPiece('e', '7');
        Optional<Piece> f7 = board.getPiece('f', '7');
        Optional<Piece> g7 = board.getPiece('g', '7');
        Optional<Piece> h7 = board.getPiece('h', '7');
        assertTrue(a7.isPresent());
        assertTrue(b7.isPresent());
        assertTrue(c7.isPresent());
        assertTrue(d7.isPresent());
        assertTrue(e7.isPresent());
        assertTrue(f7.isPresent());
        assertTrue(g7.isPresent());
        assertTrue(h7.isPresent());

        assertTrue(a7.get().isBlack());
        assertTrue(b7.get().isBlack());
        assertTrue(c7.get().isBlack());
        assertTrue(d7.get().isBlack());
        assertTrue(e7.get().isBlack());
        assertTrue(f7.get().isBlack());
        assertTrue(g7.get().isBlack());
        assertTrue(h7.get().isBlack());

        assertEquals(AbstractPiece.PieceType.PAWN, a7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, b7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, c7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, d7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, e7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, f7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, g7.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, h7.get().getType());
    }

    private void testWhitePieces() {
        Optional<Piece> a1 = board.getPiece('a', '1');//White ROOK
        Optional<Piece> b1 = board.getPiece('b', '1');//White KNIGHT
        Optional<Piece> c1 = board.getPiece('c', '1');//White BISHOP
        Optional<Piece> d1 = board.getPiece('d', '1');//White QUEEN
        Optional<Piece> e1 = board.getPiece('e', '1');//White KING
        Optional<Piece> f1 = board.getPiece('f', '1');//White BISHOP
        Optional<Piece> g1 = board.getPiece('g', '1');//White KNIGHT
        Optional<Piece> h1 = board.getPiece('h', '1');//White ROOK
        assertTrue(a1.isPresent());
        assertTrue(b1.isPresent());
        assertTrue(c1.isPresent());
        assertTrue(d1.isPresent());
        assertTrue(e1.isPresent());
        assertTrue(f1.isPresent());
        assertTrue(g1.isPresent());
        assertTrue(h1.isPresent());
        assertEquals(AbstractPiece.PieceType.ROOK, a1.get().getType());
        assertEquals(AbstractPiece.PieceType.KNIGHT, b1.get().getType());
        assertEquals(AbstractPiece.PieceType.BISHOP, c1.get().getType());
        assertEquals(AbstractPiece.PieceType.QUEEN, d1.get().getType());
        assertEquals(AbstractPiece.PieceType.KING, e1.get().getType());
        assertEquals(AbstractPiece.PieceType.BISHOP, f1.get().getType());
        assertEquals(AbstractPiece.PieceType.KNIGHT, g1.get().getType());
        assertEquals(AbstractPiece.PieceType.ROOK, h1.get().getType());
        assertTrue(a1.get().isWhite());
        assertTrue(b1.get().isWhite());
        assertTrue(c1.get().isWhite());
        assertTrue(d1.get().isWhite());
        assertTrue(e1.get().isWhite());
        assertTrue(f1.get().isWhite());
        assertTrue(g1.get().isWhite());
        assertTrue(h1.get().isWhite());
        testWhitePawns();
    }

    private void testWhitePawns() {
        //---------- Pawns ------------------
        Optional<Piece> a2 = board.getPiece('a', '2');
        Optional<Piece> b2 = board.getPiece('b', '2');
        Optional<Piece> c2 = board.getPiece('c', '2');
        Optional<Piece> d2 = board.getPiece('d', '2');
        Optional<Piece> e2 = board.getPiece('e', '2');
        Optional<Piece> f2 = board.getPiece('f', '2');
        Optional<Piece> g2 = board.getPiece('g', '2');
        Optional<Piece> h2 = board.getPiece('h', '2');
        assertTrue(a2.isPresent());
        assertTrue(b2.isPresent());
        assertTrue(c2.isPresent());
        assertTrue(d2.isPresent());
        assertTrue(e2.isPresent());
        assertTrue(f2.isPresent());
        assertTrue(g2.isPresent());
        assertTrue(h2.isPresent());

        assertTrue(a2.get().isWhite());
        assertTrue(b2.get().isWhite());
        assertTrue(c2.get().isWhite());
        assertTrue(d2.get().isWhite());
        assertTrue(e2.get().isWhite());
        assertTrue(f2.get().isWhite());
        assertTrue(g2.get().isWhite());
        assertTrue(h2.get().isWhite());

        assertEquals(AbstractPiece.PieceType.PAWN, a2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, b2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, c2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, d2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, e2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, f2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, g2.get().getType());
        assertEquals(AbstractPiece.PieceType.PAWN, h2.get().getType());
    }

    @Test
    void testBasicFenStrings() {
        List<String> basicFens =
                List.of(
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
                        "8/8/8/8/8/8/8/8 w - - 0 1",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
        basicFens.forEach(this::testValidFen);
    }

    @Test
    void testComplexBoardFens() {
        List<String> complexFens = List.of(
                "3q1rk1/1b4pp/p3P3/1pp2p2/3p1P2/8/PPP1B1PP/R2Q1RK1 w - - 3 18",
                "rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 3",
                "rnbqk2r/ppp2ppp/3b4/3p4/3Pn3/2N5/PPP2PPP/R1BQKB1R b KQkq - 0 7",
                "8/2p1k3/8/2PpP3/2K2P2/8/8/8 w - d6 0 36",
                "r3k2r/2p2pp1/2p5/3n4/8/2N5/PPP2PPP/R1BQKB1R w KQkq - 1 14",
                "2kr3r/pppb1ppp/1b3n2/1B6/3PN3/2N2P2/PPP3PP/R1B1R1K1 w - - 0 12",
                "rnbq1rk1/1pp2ppp/8/p2p4/1b1P4/2N5/PP2PPPP/R1BQKB1R w KQ - 1 7",
                "rnb1k2r/pp3ppp/2p5/3p4/8/2N2P2/PP2P1PP/R1B1KB1R b KQkq - 0 10",
                "r2q1rk1/pbp2ppp/1p2p3/3pP3/3Pn3/2N1B3/PPPQ1PPP/R3K2R w KQ - 0 13",
                "r1b1kb1r/1ppp1ppp/p1n1pn2/8/2P1P3/2N1BN2/PP1P1PPP/R2QKB1R b KQkq - 0 6",
                "rnbqk2r/pp2ppbp/6p1/3pP3/3P4/8/PPP2PPP/R1BQKBNR w KQkq - 1 7",
                "r3k2r/1bpq1pp1/p1np4/1p2p3/1P2P3/2P5/3Q1PPP/R1B1KBNR w KQkq - 1 14",
                "2kr1b1r/pppbqppp/1bn1pn2/1B6/3PP3/2N2N2/PPP2PPP/R1BQK2R w KQ - 4 8",
                "rnbq1rk1/1p3ppp/p1p1pn2/3p4/1b1P4/2NBPN2/PP3PPP/R1BQ1RK1 b - - 0 10"
        );
        complexFens.forEach(this::testValidFen);
    }

    @Test
    void testEdgeCaseFens() {
        List<String> edgeCases =
                List.of(
                        "8/8/8/8/8/8/8/k7 w - - 50 101",
                        "8/8/8/8/8/8/8/k7 w - - 100 101",
                        "8/8/8/8/8/8/8/4K2R w KQ - 0 1",
                        "8/8/8/8/8/8/8/4K2R w - - 0 1",
                        "8/8/8/8/8/8/8/8 w - a3 0 1");
        edgeCases.forEach(this::testValidFen);
    }

    @Test
    void testInvalidFens() {
        List<String> invalidFens =
                List.of(
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 A",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1 2",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1 2");
        invalidFens.forEach(this::testInvalidFen);
    }


    private void testValidFen(String assumedValid) {
        ChessBoard board = new ChessBoard(new MinimaxAIPlayer(Player.BLACK, new ChessGame(new JChessController())));
        // Apply the FEN string to the board
        board.applyFen(assumedValid);

        LOGGER.info(assumedValid);
        // Assert the FEN string representation of
        // the board matches the assumedValid string
        String fenString = board.getFenString();
        LOGGER.info(fenString);
        assert fenString.equals(assumedValid) : "FEN string does not match";
    }


    private void testInvalidFen(String assumedInvalid) {
        ChessBoard b = new ChessBoard(new MinimaxAIPlayer(Player.BLACK, new ChessGame(new JChessController())));
        assertThrows(IllegalArgumentException.class, () -> b.applyFen(assumedInvalid));
    }
}
