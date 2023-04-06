package jchess;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.impl.Bishop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BishopTest {

    private static Board board;

    @BeforeAll
    static void initBoard() {
        board = new Board();
    }
    @Test
    void testCanMove() {
        Piece b = new Bishop(AbstractPiece.Player.WHITE, 'a', '1');
        board.addPiece(b);
        Optional<Piece> p = board.getPiece('a', '1');
        p.ifPresent(piece -> assertEquals(piece, b));
    }
}
