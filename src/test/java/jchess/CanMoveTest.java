package jchess;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CanMoveTest {

    @Test
    void testCanMove() {
        Board board;
        Piece p;
        List<PiecePos> validBishopMoves = List.of(new PiecePos('b', '2'),
                new PiecePos('c', '3'),
                new PiecePos('d', '4'),
                new PiecePos('e', '5'),
                new PiecePos('f', '6'),
                new PiecePos('g', '7'),
                new PiecePos('h', '8'));
        p = new Bishop(AbstractPiece.Player.WHITE, 'a', '1');
        board = generateNewBoard(p);
        testAllPositions(validBishopMoves, board, p);

        p = new King(AbstractPiece.Player.WHITE, 'a', '1');
        board = generateNewBoard(p);
        testAllPositions(validBishopMoves, board, p);

        p = new Queen(AbstractPiece.Player.WHITE, 'a', '1');
        board = generateNewBoard(p);
        testAllPositions(validBishopMoves, board, p);

        p = new Rook(AbstractPiece.Player.WHITE, 'a', '1');
        board = generateNewBoard(p);
        testAllPositions(validBishopMoves, board, p);

        p = new Pawn(AbstractPiece.Player.WHITE, 'a', '1');
        board = generateNewBoard(p);
        testAllPositions(validBishopMoves, board, p);

    }

    private static Board generateNewBoard(Piece b) {
        Board board = new Board();
        board.addPiece(b);
        Optional<Piece> p = board.getPiece('a', '1');
        p.ifPresent(piece -> assertEquals(piece, b));
        return board;
    }

    private static void testAllPositions(List<PiecePos> validMoves, Board board, Piece b) {

        List<PiecePos> allPositions = board.getAllPositions();
        //Test all possible moves.
        allPositions.forEach(pos -> {
            if (validMoves.contains(pos)) {
                assertTrue(b.canMove(board, pos));
            } else {
                assertFalse(b.canMove(board, pos));
            }
        });
    }
}
