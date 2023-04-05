package jchess;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.JChessException;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests a simple 8x8 chess board.
 */
public class BoardTest {

    public static final Logger LOGGER = LogManager.getLogger(
            BoardTest.class);

    private static Board BOARD;

    @BeforeAll
    public static void createBoard() {
        try {
            BOARD = new Board();
        } catch (JChessException e) {
            LOGGER.error(e);
        }
    }

    @Test
    public void testResetBoard() throws JChessException {
        BOARD.movePiece(new PiecePos('a', '2'), new PiecePos('a', '3'));
        BOARD.movePiece(new PiecePos('b', '2'), new PiecePos('b', '4'));
        BOARD.printBoardState();
        BOARD.undo();//Works
        BOARD.printBoardState();
        BOARD.redo();//Works
        BOARD.printBoardState();
        BOARD.reset();//Works
        testBlackPieces();
        testWhitePieces();
        assertEquals(
        		BOARD.getCurrentState(),
        		Board.GameState.NONE);
        BOARD.printBoardState();
    }

    /**
     * Chess board.
     *   +------------------------+
     * 8 | r  n  b  q  k  b  n  r |
     * 7 | p  p  p  p  p  p  p  p |
     * 6 | .  .  .  .  .  .  .  . |
     * 5 | .  .  .  .  .  .  .  . |
     * 4 | .  .  .  .  .  .  .  . |
     * 3 | .  .  .  .  .  .  .  . |
     * 2 | P  P  P  P  P  P  P  P |
     * 1 | R  N  B  Q  K  B  N  R |
     *   +------------------------+
     *     a  b  c  d  e  f  g  h
     */
    private void testBlackPieces() {
        Optional<Piece> a8 = BOARD.getPiece('a', '8');//Black ROOK
        Optional<Piece> b8 = BOARD.getPiece('b', '8');//Black KNIGHT
        Optional<Piece> c8 = BOARD.getPiece('c', '8');//Black BISHOP
        Optional<Piece> d8 = BOARD.getPiece('d', '8');//Black QUEEN
        Optional<Piece> e8 = BOARD.getPiece('e', '8');//Black KING
        Optional<Piece> f8 = BOARD.getPiece('f', '8');//Black BISHOP
        Optional<Piece> g8 = BOARD.getPiece('g', '8');//Black KNIGHT
        Optional<Piece> h8 = BOARD.getPiece('h', '8');//Black ROOK
        assertTrue(a8.isPresent());
        assertTrue(b8.isPresent());
        assertTrue(c8.isPresent());
        assertTrue(d8.isPresent());
        assertTrue(e8.isPresent());
        assertTrue(f8.isPresent());
        assertTrue(g8.isPresent());
        assertTrue(h8.isPresent());
        assertEquals(a8.get().getType(), AbstractPiece.PieceType.ROOK);
        assertEquals(b8.get().getType(), AbstractPiece.PieceType.KNIGHT);
        assertEquals(c8.get().getType(), AbstractPiece.PieceType.BISHOP);
        assertEquals(d8.get().getType(), AbstractPiece.PieceType.QUEEN);
        assertEquals(e8.get().getType(), AbstractPiece.PieceType.KING);
        assertEquals(f8.get().getType(), AbstractPiece.PieceType.BISHOP);
        assertEquals(g8.get().getType(), AbstractPiece.PieceType.KNIGHT);
        assertEquals(h8.get().getType(), AbstractPiece.PieceType.ROOK);
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
        Optional<Piece> a7 = BOARD.getPiece('a', '7');
        Optional<Piece> b7 = BOARD.getPiece('b', '7');
        Optional<Piece> c7 = BOARD.getPiece('c', '7');
        Optional<Piece> d7 = BOARD.getPiece('d', '7');
        Optional<Piece> e7 = BOARD.getPiece('e', '7');
        Optional<Piece> f7 = BOARD.getPiece('f', '7');
        Optional<Piece> g7 = BOARD.getPiece('g', '7');
        Optional<Piece> h7 = BOARD.getPiece('h', '7');
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

        assertEquals(a7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(b7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(c7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(d7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(e7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(f7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(g7.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(h7.get().getType(), AbstractPiece.PieceType.PAWN);
    }

    private void testWhitePieces() {
        Optional<Piece> a1 = BOARD.getPiece('a', '1');//White ROOK
        Optional<Piece> b1 = BOARD.getPiece('b', '1');//White KNIGHT
        Optional<Piece> c1 = BOARD.getPiece('c', '1');//White BISHOP
        Optional<Piece> d1 = BOARD.getPiece('d', '1');//White QUEEN
        Optional<Piece> e1 = BOARD.getPiece('e', '1');//White KING
        Optional<Piece> f1 = BOARD.getPiece('f', '1');//White BISHOP
        Optional<Piece> g1 = BOARD.getPiece('g', '1');//White KNIGHT
        Optional<Piece> h1 = BOARD.getPiece('h', '1');//White ROOK
        assertTrue(a1.isPresent());
        assertTrue(b1.isPresent());
        assertTrue(c1.isPresent());
        assertTrue(d1.isPresent());
        assertTrue(e1.isPresent());
        assertTrue(f1.isPresent());
        assertTrue(g1.isPresent());
        assertTrue(h1.isPresent());
        assertEquals(a1.get().getType(), AbstractPiece.PieceType.ROOK);
        assertEquals(b1.get().getType(), AbstractPiece.PieceType.KNIGHT);
        assertEquals(c1.get().getType(), AbstractPiece.PieceType.BISHOP);
        assertEquals(d1.get().getType(), AbstractPiece.PieceType.QUEEN);
        assertEquals(e1.get().getType(), AbstractPiece.PieceType.KING);
        assertEquals(f1.get().getType(), AbstractPiece.PieceType.BISHOP);
        assertEquals(g1.get().getType(), AbstractPiece.PieceType.KNIGHT);
        assertEquals(h1.get().getType(), AbstractPiece.PieceType.ROOK);
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
        Optional<Piece> a2 = BOARD.getPiece('a', '2');
        Optional<Piece> b2 = BOARD.getPiece('b', '2');
        Optional<Piece> c2 = BOARD.getPiece('c', '2');
        Optional<Piece> d2 = BOARD.getPiece('d', '2');
        Optional<Piece> e2 = BOARD.getPiece('e', '2');
        Optional<Piece> f2 = BOARD.getPiece('f', '2');
        Optional<Piece> g2 = BOARD.getPiece('g', '2');
        Optional<Piece> h2 = BOARD.getPiece('h', '2');
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

        assertEquals(a2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(b2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(c2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(d2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(e2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(f2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(g2.get().getType(), AbstractPiece.PieceType.PAWN);
        assertEquals(h2.get().getType(), AbstractPiece.PieceType.PAWN);
    }
}