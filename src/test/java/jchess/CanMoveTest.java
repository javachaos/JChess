package jchess;

import com.github.javachaos.jchess.JChessController;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CanMoveTest {

    private static ChessBoard generateNewBoard(Piece b) {
        ChessBoard chessBoard = new ChessBoard(new MinimaxAIPlayer(Player.BLACK, new ChessGame(new JChessController())));
        chessBoard.addPiece(b);
        Optional<Piece> p = chessBoard.getPiece('a', '1');
        p.ifPresent(piece -> assertEquals(piece, b));
        return chessBoard;
    }

    private static void testAllPositions(List<PiecePos> validMoves, ChessBoard chessBoard, Piece b) {

        List<PiecePos> allPositions = chessBoard.getAllPositions();
        //Test all possible moves.
        allPositions.forEach(pos -> {
            if (validMoves.contains(pos)) {
                assertTrue(b.canMove(chessBoard, pos));
            } else {
                assertFalse(b.canMove(chessBoard, pos));
            }
        });
    }

    @Test
    void testCanMove() {
        ChessBoard chessBoard;
        Piece p;
        List<PiecePos> validBishopMoves = List.of(new PiecePos('b', '2'),
                new PiecePos('c', '3'),
                new PiecePos('d', '4'),
                new PiecePos('e', '5'),
                new PiecePos('f', '6'),
                new PiecePos('g', '7'),
                new PiecePos('h', '8'));
        p = new Bishop(Player.WHITE, 'a', '1');
        chessBoard = generateNewBoard(p);
        testAllPositions(validBishopMoves, chessBoard, p);

        p = new King(Player.WHITE, 'a', '1');
        chessBoard = generateNewBoard(p);
        testAllPositions(validBishopMoves, chessBoard, p);

        p = new Queen(Player.WHITE, 'a', '1');
        chessBoard = generateNewBoard(p);
        testAllPositions(validBishopMoves, chessBoard, p);

        p = new Rook(Player.WHITE, 'a', '1');
        chessBoard = generateNewBoard(p);
        testAllPositions(validBishopMoves, chessBoard, p);

        p = new Pawn(Player.WHITE, 'a', '1');
        chessBoard = generateNewBoard(p);
        testAllPositions(validBishopMoves, chessBoard, p);

    }
}
