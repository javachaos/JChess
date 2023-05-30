package com.github.javachaos.jchess.utils;
import com.github.javachaos.jchess.logic.ChessBoard;
import com.github.javachaos.jchess.moves.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BitUtilsTest {
    private static final Logger LOGGER = LogManager.getLogger(BitUtilsTest.class);

    @Test
    public void testOccupancyToCharArray() {
        LOGGER.info("------------------   Testing OccupancyToCharArray. BEGIN   ------------------");
        char[][] INIT_BOARD = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},//8
                {'p', 'p', 'p', 'p', '.', 'p', 'p', 'p'},//7
                {'.', '.', '.', '.', 'p', '.', '.', '.'},//6
                {'.', '.', '.', '.', '.', '.', '.', '.'},//5
                {'.', '.', '.', '.', '.', '.', 'p', '.'},//4
                {'P', 'P', 'P', 'P', 'P', 'P', '.', '.'},//3
                {'.', '.', '.', '.', '.', '.', 'P', 'P'},//2
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'} //1
                //A    B    C    D    E    F    G    H
        };
        char[][] OCCU_BOARD = {
                {'@', '@', '@', '@', '@', '@', '@', '@'},//8
                {'@', '@', '@', '@', '.', '@', '@', '@'},//7
                {'.', '.', '.', '.', '@', '.', '.', '.'},//6
                {'.', '.', '.', '.', '.', '.', '.', '.'},//5
                {'.', '.', '.', '.', '.', '.', '@', '.'},//4
                {'@', '@', '@', '@', '@', '@', '.', '.'},//3
                {'.', '.', '.', '.', '.', '.', '@', '@'},//2
                {'@', '@', '@', '@', '@', '@', '@', '@'} //1
                //A    B    C    D    E    F    G    H
        };

        ChessBoard cb = new ChessBoard(INIT_BOARD);
        assertArrayEquals(cb.toCharArray(), INIT_BOARD);
        ExecUtils.ExecutionResult<char[][]> r = ExecUtils.measureExecutionTime(
                "OccupancyToCharArray", () -> PrintUtils.occupancyToCharArray(cb.getOccupancy()), 10);
        assertArrayEquals(r.result(), OCCU_BOARD);
        assertTrue(r.nanos() < TimeUnit.MICROSECONDS.toNanos(100));

        ExecUtils.ExecutionResult<char[][]> k = ExecUtils.measureExecutionTime(
                "OccupancyToCharArray", () -> PrintUtils.occupancyToCharArray(cb.getOccupancy()), 10);
        assertArrayEquals(k.result(), OCCU_BOARD);
        assertTrue(k.nanos() < TimeUnit.MICROSECONDS.toNanos(100));

        cb.prettyPrintBoard();

        LOGGER.info("------------------   Testing OccupancyToCharArray. END   ------------------");
    }

    @Test
    public void testChessBoard() {
        char[][] INIT_BOARD = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        char[][] OCCU_BOARD = {
                {'@', '@', '@', '@', '@', '@', '@', '@'},
                {'@', '@', '@', '@', '@', '@', '@', '@'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'@', '@', '@', '@', '@', '@', '@', '@'},
                {'@', '@', '@', '@', '@', '@', '@', '@'}
        };
        LOGGER.info("------------------   Testing ChessBoard. BEGIN   ----------------");
        ChessBoard cb = new ChessBoard();
        cb.printOccupancy();
        cb.printBoard();
        assertArrayEquals(cb.toCharArray(), INIT_BOARD);
        assertArrayEquals(PrintUtils.occupancyToCharArray(cb.getOccupancy()), OCCU_BOARD);
        LOGGER.info("------------------   Testing ChessBoard. END   -------------------");
    }

    @Test
    public void testBitsToCharArray() {
        LOGGER.info("------------------   Testing BitsToCharArray. BEGIN   ------------------");
        char[][] INIT_BOARD = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        ChessBoard cb = new ChessBoard();
        ExecUtils.ExecutionResult<char[][]> r = ExecUtils.measureExecutionTime("bitsToCharArray",
                () -> PrintUtils.bitsToCharArray(cb.getBits(), new char[8][8]), 10);
        assertTrue(r.nanos() < TimeUnit.MICROSECONDS.toNanos(100));
        assertArrayEquals(r.result(), INIT_BOARD);
        LOGGER.info("------------------   Testing BitsToCharArray. END   ------------------");
    }

    @Test
    public void testEnpassant() {
        char[][] BIT_BOARD = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},//8
                {'p', 'p', '.', 'p', '.', 'p', 'p', 'p'},//7
                {'.', '.', '.', '.', 'p', '.', '.', '.'},//6
                {'.', '.', '.', '.', '.', '.', '.', '.'},//5
                {'.', '.', 'p', 'P', '.', 'P', 'p', '.'},//4
                {'P', 'P', 'P', '.', 'P', '.', '.', '.'},//3
                {'.', '.', '.', '.', '.', '.', 'P', 'P'},//2
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'} //1
                //A    B    C    D    E    F    G    H
        };

        long[] bits = BitUtils.createBitBoard(BIT_BOARD);
        BitUtils.updateBoards(bits);

        LOGGER.info("Starting white pawn move generation.");
        ExecUtils.ExecutionResult<List<Move>> r = ExecUtils.measureExecutionTime(
                "White Pawn Move Generation", () -> BitUtils.pawnMovesWhite(bits, Move.fromString("e7e6")), 10);
        LOGGER.info(r.result());
        LOGGER.info("Starting black pawn move generation.");
        ExecUtils.ExecutionResult<List<Move>> r1 = ExecUtils.measureExecutionTime(
                "Black Pawn Move Generation", () -> BitUtils.pawnMovesBlack(bits, Move.fromString("d2d4")), 10);
        LOGGER.info(r1.result());
        assertEquals(List.of(
                Move.fromString("a7a6"),
                Move.fromString("b7b6"),
                Move.fromString("d7d6"),
                Move.fromString("f7f6"),
                Move.fromString("g7g6"),
                Move.fromString("h7h6"),
                Move.fromString("a7a5"),
                Move.fromString("b7b5"),
                Move.fromString("d7d5"),
                Move.fromString("e6e5"),
                Move.fromString("f7f5"),
                Move.fromString("g7g5"),
                Move.fromString("h7h5"),
                Move.fromString("c4b3"),
                Move.fromString("c4d3"),
                Move.fromString("g4g3")), r1.result());
    }

    @Test
    public void testPawnForwardBoard() {
        LOGGER.info("------------------   Testing PawnMoveGeneration. BEGIN   ------------------");
        char[][] INIT_BOARD = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},//8
                {'p', 'p', 'p', 'p', '.', 'p', 'p', 'p'},//7
                {'.', '.', '.', '.', 'p', '.', '.', '.'},//6
                {'.', '.', '.', '.', '.', '.', '.', '.'},//5
                {'.', '.', '.', '.', '.', 'P', 'p', '.'},//4
                {'P', 'P', 'P', 'P', 'P', '.', '.', '.'},//3
                {'.', '.', '.', '.', '.', '.', 'P', 'P'},//2
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'} //1
                //A    B    C    D    E    F    G    H
        };

        long[] bits = BitUtils.createBitBoard(INIT_BOARD);
        PrintUtils.printBitboard(bits[0]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[1]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[2]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[3]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[4]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[5]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[6]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[7]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[8]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[9]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[10]);
        LOGGER.info("");
        PrintUtils.printBitboard(bits[11]);
        PrintUtils.printBoard(bits);
        BitUtils.updateBoards(bits);
        LOGGER.info("Starting white pawn move generation.");
        ExecUtils.ExecutionResult<List<Move>> r = ExecUtils.measureExecutionTime(
                "White Pawn Move Generation", () -> BitUtils.pawnMovesWhite(bits, Move.fromString("e7e6")), 10);
        LOGGER.info(r.result());
        assertEquals(List.of(
                Move.fromString("f4f5"),
                Move.fromString("a3a4"),
                Move.fromString("b3b4"),
                Move.fromString("c3c4"),
                Move.fromString("d3d4"),
                Move.fromString("e3e4"),
                Move.fromString("h2h4"),
                Move.fromString("g2g3"),
                Move.fromString("h2h3")), r.result());
       // assertTrue(r.nanos() < TimeUnit.MICROSECONDS.toNanos(100));

        LOGGER.info("Starting black pawn move generation.");
        ExecUtils.ExecutionResult<List<Move>> r1 = ExecUtils.measureExecutionTime(
                "Black Pawn Move Generation", () -> BitUtils.pawnMovesBlack(bits, Move.fromString("f2f4")), 10);
        LOGGER.info(r1.result());
        assertEquals(List.of(
                Move.fromString("a7a6"),
                Move.fromString("b7b6"),
                Move.fromString("c7c6"),
                Move.fromString("d7d6"),
                Move.fromString("f7f6"),
                Move.fromString("g7g6"),
                Move.fromString("h7h6"),
                Move.fromString("a7a5"),
                Move.fromString("b7b5"),
                Move.fromString("c7c5"),
                Move.fromString("d7d5"),
                Move.fromString("e6e5"),
                Move.fromString("f7f5"),
                Move.fromString("g7g5"),
                Move.fromString("h7h5"),
                Move.fromString("g4f3"),
                Move.fromString("g4g3")), r1.result());
        //assertTrue(r1.nanos() < TimeUnit.MICROSECONDS.toNanos(100));

        LOGGER.info("------------------   Testing PawnMoveGeneration. END   ------------------");
    }

    @Test
    void testIsOddParity() {
        LOGGER.info("------------------   Testing IsOddParity. BEGIN   ------------------");
        long odd = 0b00000111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        assertTrue(BitUtils.isOddParity(odd));
        long even = 0b00000111_00100000_00000000_00000000_00000000_00000000_00000000_00000000L;
        assertFalse(BitUtils.isOddParity(even));

        long o = 0b00011111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        assertTrue(BitUtils.isOddParity(o));
        long e = 0b10000111_00100000_00000000_00000000_00000000_00000000_00000000_00000011L;
        assertTrue(BitUtils.isOddParity(e));

        long od = 0b10011111_00000000_00000100_00000010_00000000_00000100_00100000_00000000L;
        ExecUtils.measureExecutionTime("isOddParity", () -> BitUtils.isOddParity(od), 10);
        ExecUtils.measureExecutionTime("isOddParity", () -> BitUtils.isOddParity(o), 10);
        ExecUtils.measureExecutionTime("isOddParity", () -> BitUtils.isOddParity(e), 10);
        ExecUtils.measureExecutionTime("isOddParity", () -> BitUtils.isOddParity(odd), 10);
        ExecUtils.measureExecutionTime("isOddParity", () -> BitUtils.isOddParity(even), 10);

        LOGGER.info("------------------   Testing IsOddParity. END   ------------------");

    }

}

