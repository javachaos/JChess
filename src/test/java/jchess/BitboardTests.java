package jchess;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import com.github.javachaos.jchess.logic.ChessBoard;
import com.github.javachaos.jchess.utils.BitUtils;

@SuppressWarnings("all")
public class BitboardTests {
	
	@Test
	void testChessBoard() {
		ChessBoard cb = new ChessBoard();
		cb.printOccupancy();
		cb.printBoard();
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
