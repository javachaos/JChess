package com.github.javachaos.jchess.utils;
import com.github.javachaos.jchess.logic.ChessBoard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BitUtilsTest {
    private static final Logger LOGGER = LogManager.getLogger(BitUtilsTest.class);

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
        ChessBoard cb = new ChessBoard();
        cb.printOccupancy();
        cb.printBoard();
        assertArrayEquals(cb.toCharArray(), INIT_BOARD);
        assertArrayEquals(BitUtils.occupancyToCharArray(cb.getOccupancy()), OCCU_BOARD);
    }

    @Test
    public void testPopCount() {
        long one   = 0b00000000_00000000_00000000_00000000_00000000_00000000_00100000L;
        long two   = 0b00000010_00000000_00000000_00000000_00000000_00000000_00100000L;
        long three = 0b00000001_00000001_00000000_00000000_00000000_00000000_00100000L;
        long four  = 0b00000000_00000010_00001100_00000000_00000000_00000000_00100000L;
        long five  = 0b00001000_00010000_00010000_00010000_00000000_00000000_00100000L;
        long six   = 0b01100100_00100000_00000000_00000100_00000000_00000000_00100000L;
        long seven = 0b11111100_00000000_00000000_00000000_00000000_00000000_00100000L;
        long eight = 0b00010000_01000000_00110000_01110000_00000000_00000000_00100000L;
        long nine  = 0b00000111_00000011_00000111_00000000_00000000_00000000_00100000L;
        long ten   = 0b00001111_11110000_00000000_00000001_00000000_00000000_00100000L;
        assertEquals(1, Long.bitCount(one));
        assertEquals(2, Long.bitCount(two));
        assertEquals(3, Long.bitCount(three));
        assertEquals(4, Long.bitCount(four));
        assertEquals(5, Long.bitCount(five));
        assertEquals(6, Long.bitCount(six));
        assertEquals(7, Long.bitCount(seven));
        assertEquals(8, Long.bitCount(eight));
        assertEquals(9, Long.bitCount(nine));
        assertEquals(10, Long.bitCount(ten));
    }

    @Test
    public void testPawnforwardBoard() {
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

        long[] bits = BitUtils.createBitBoard(INIT_BOARD);
        BitUtils.printBitboard(bits[0]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[1]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[2]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[3]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[4]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[5]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[6]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[7]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[8]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[9]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[10]);
        LOGGER.info("");
        BitUtils.printBitboard(bits[11]);
        char[][] BITBOARD = BitUtils.bitsToCharArray(bits);
        BitUtils.printBoard(bits);
        List<BitUtils.Move> movesList = new ArrayList<>();
        BitUtils.updateBoards(bits);
        LOGGER.info("Starting white pawn move generation.");
        ExecUtils.ExecutionResult<List<BitUtils.Move>> r = ExecUtils.measureExecutionTime(
                "White Pawn Move Generation", () -> BitUtils.pawnMovesWhite(bits, movesList));
        LOGGER.info("Total runtime: {} ns", r.nanos());
        LOGGER.info(movesList);
        movesList.clear();

        LOGGER.info("Starting black pawn move generation.");
        ExecUtils.ExecutionResult<List<BitUtils.Move>> b = ExecUtils.measureExecutionTime(
                "Black Pawn Move Generation", () -> BitUtils.pawnMovesBlack(bits, movesList));
        LOGGER.info("Total runtime: {} ns", b.nanos());

        LOGGER.info(movesList);
        assertArrayEquals(BITBOARD, INIT_BOARD);
    }

    @Test
    void testIsOddParity() {
        long odd = 0b00000111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        assertTrue(BitUtils.isOddParity(odd));
        long even = 0b00000111_00100000_00000000_00000000_00000000_00000000_00000000_00000000L;
        assertFalse(BitUtils.isOddParity(even));

        long o = 0b00011111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        assertTrue(BitUtils.isOddParity(o));
        long e = 0b10000111_00100000_00000000_00000000_00000000_00000000_00000000_00000011L;
        assertTrue(BitUtils.isOddParity(e));

        long od = 0b10011111_00000000_00000100_00000010_00000000_00000100_00100000_00000000L;
        ExecUtils.measureExecutionTime("1", () -> BitUtils.isOddParity(od));

    }

}

