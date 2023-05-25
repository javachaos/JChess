package jchess;

import com.github.javachaos.jchess.logic.ChessBoard;
import com.github.javachaos.jchess.utils.BitUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
public class BitboardTests {

    @Test
    void testChessBoard() {
        ChessBoard cb = new ChessBoard();
        cb.printOccupancy();
        cb.printBoard();
    }
    
    @Test
    void testPopCount() {
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
    	assertEquals(1, BitUtils.popCount(one));
    	assertEquals(2, BitUtils.popCount(two));
    	assertEquals(3, BitUtils.popCount(three));
    	assertEquals(4, BitUtils.popCount(four));
    	assertEquals(5, BitUtils.popCount(five));
    	assertEquals(6, BitUtils.popCount(six));
    	assertEquals(7, BitUtils.popCount(seven));
    	assertEquals(8, BitUtils.popCount(eight));
    	assertEquals(9, BitUtils.popCount(nine));
    	assertEquals(10, BitUtils.popCount(ten));
    }

    @Test
    void testPawnforwardBoard() {
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
        System.out.println();
        BitUtils.printBitboard(bits[1]);
        System.out.println();
        BitUtils.printBitboard(bits[2]);
        System.out.println();
        BitUtils.printBitboard(bits[3]);
        System.out.println();
        BitUtils.printBitboard(bits[4]);
        System.out.println();
        BitUtils.printBitboard(bits[5]);
        System.out.println();
        BitUtils.printBitboard(bits[6]);
        System.out.println();
        BitUtils.printBitboard(bits[7]);
        System.out.println();
        BitUtils.printBitboard(bits[8]);
        System.out.println();
        BitUtils.printBitboard(bits[9]);
        System.out.println();
        BitUtils.printBitboard(bits[10]);
        System.out.println();
        BitUtils.printBitboard(bits[11]);
        char[][] BITBOARD = BitUtils.bitsToCharArray(bits);
        BitUtils.printBits(bits);

        assertArrayEquals(BITBOARD, INIT_BOARD);
    }

}
