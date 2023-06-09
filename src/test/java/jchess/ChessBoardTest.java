package jchess;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests a simple 8x8 chess board.
 */
public class ChessBoardTest {

    private static ChessBoard board;

    @BeforeAll
    public static void createBoard() {
        board = new ChessBoard(new MinimaxAIPlayer(Player.BLACK));
        board.start();
    }

    @Test
    void testResetBoard() throws JChessException {
        board.movePiece(new PiecePos('a', '2'), new PiecePos('a', '3'));
        board.movePiece(new PiecePos('b', '2'), new PiecePos('b', '4'));
        GSM.instance().undo(board);//Works
        GSM.instance().redo(board);//Works
        board.reset();//Works
        testBlackPieces();
        testWhitePieces();
        assertEquals(
                GSM.GameState.NONE,
                GSM.instance().getCurrentState());
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
}
