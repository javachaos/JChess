package com.github.javachaos.jchess.logic;

import com.github.javachaos.jchess.utils.BitUtils;

/**
 * Light weight chess board representation, using bitboards.
 *
 * @author fred
 *
 */
public class ChessBoard {
    private long occupancy = 0L;

	private static final char[][] INIT_BOARD = {
		    {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
		    {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
		    {'.', '.', '.', '.', '.', '.', '.', '.'},
		    {'.', '.', '.', '.', '.', '.', '.', '.'},
		    {'.', '.', '.', '.', '.', '.', '.', '.'},
		    {'.', '.', '.', '.', '.', '.', '.', '.'},
		    {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
		    {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
		};
    
	private final long[] bits;

    public ChessBoard(char[][] initialBoard) {
    	bits = BitUtils.createBitBoard(initialBoard);
    	updateOccupancy();
    }

    public ChessBoard() {
    	this(INIT_BOARD);
    }

    private void updateOccupancy() {
		for (long bit : bits) {
			occupancy |= bit;
		}
    }

	@SuppressWarnings("unused")
    public long getOccupancy() {
    	return occupancy;
    }
    
    public void printOccupancy() {
    	BitUtils.printBitboard(occupancy);
    }
    
    public void printBoard() {
    	BitUtils.printBits(bits);
    }
    
    public char[][] toCharArray() {
    	return BitUtils.bitsToCharArray(bits);
    }
   
}