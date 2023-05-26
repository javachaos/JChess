package com.github.javachaos.jchess.utils;
import com.github.javachaos.jchess.logic.ChessBoard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', '.', 'p', 'p', 'p'},
                {'.', '.', '.', '.', 'p', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.', '.', '.', '.'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
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
        long[] times = new long[2];
        List<BitUtils.Move> movesList = new ArrayList<>();
        BitUtils.MoveSet moves = assertTimeout(Duration.of(1, ChronoUnit.MILLIS),
                () -> {
                    times[0] = System.nanoTime();
                    BitUtils.MoveSet m = BitUtils.pawnMovesWhite(bits);
                    times[1] = System.nanoTime();
                    return m;
                });
        LOGGER.info("Pawn move generation took {} ns.", times[1] - times[0]);
        long start = System.nanoTime();
        int s = Long.numberOfTrailingZeros(moves.occupancy());
        int e = Long.numberOfLeadingZeros(moves.occupancy());

        for (int i = s; i < 64 - e; i++) {
            if (((moves.occupancy() >>i) & 1L) == 1) {
                movesList.add(moves.moves()[i]);
            }
        }
        long end = System.nanoTime();
        LOGGER.info("Filtering out nulls took {} ns.", end - start);
        LOGGER.info("Total time took {} ns.", end - start + times[1] - times[0]);
        LOGGER.info(movesList);
        assertArrayEquals(BITBOARD, INIT_BOARD);
    }

}

