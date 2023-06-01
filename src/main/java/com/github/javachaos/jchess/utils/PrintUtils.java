package com.github.javachaos.jchess.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;

import static com.github.javachaos.jchess.utils.MoveUtils.BOARD_SIZE;

public class PrintUtils {
    private static final Logger LOGGER = LogManager.getLogger(PrintUtils.class);

    private static final Map<Character, Character> charToFancyMap = Map.ofEntries(
            Map.entry('K', Constants.FANCY_W_KING),
            Map.entry('Q', Constants.FANCY_W_QUEEN),
            Map.entry('R', Constants.FANCY_W_ROOK),
            Map.entry('B', Constants.FANCY_W_BISHOP),
            Map.entry('N', Constants.FANCY_W_KNIGHT),
            Map.entry('P', Constants.FANCY_W_PAWN),

            Map.entry('k', Constants.FANCY_B_KING),
            Map.entry('q', Constants.FANCY_B_QUEEN),
            Map.entry('r', Constants.FANCY_B_ROOK),
            Map.entry('b', Constants.FANCY_B_BISHOP),
            Map.entry('n', Constants.FANCY_B_KNIGHT),
            Map.entry('p', Constants.FANCY_B_PAWN),
            Map.entry('.', ' ')
    );
    
    private static final Map<Character, Integer> pieceIdx = Map.ofEntries(
    		Map.entry('P', 0),
    		Map.entry('R', 1),
    		Map.entry('N', 2),
    		Map.entry('B', 3),
    		Map.entry('K', 4),
    		Map.entry('Q', 5),
    		Map.entry('p', 6),
    		Map.entry('r', 7),
    		Map.entry('n', 8),
    		Map.entry('b', 9),
    		Map.entry('k', 10),
    		Map.entry('q', 11)
    );

    private PrintUtils() {
        //Unused
    }

    public static int getPieceIndex(char piece) {
        return pieceIdx.get(piece);
    }

    public static char[][] occupancyToCharArray(long occupancy) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < 64; i++) {
            cb[i / BOARD_SIZE][i % BOARD_SIZE] = '.';
        }
        if (Long.bitCount(occupancy) > 0) {
            while (occupancy != 0) {
                long bit = occupancy & -occupancy;  // Get the least significant set bit
                int index = Long.numberOfTrailingZeros(bit);
                occupancy ^= bit;  // Clear the least significant set bit
                cb[index/BOARD_SIZE][index%BOARD_SIZE] = '@';
            }
        }
        return cb;
    }

    public static char[][] bitsToCharArray(long[] bits, char[][] board) {
        int boardSizeSquared = BOARD_SIZE * BOARD_SIZE;
        for (int i = 0; i < boardSizeSquared; i++) {
            board[i / BOARD_SIZE][i % BOARD_SIZE] = '.';
        }

        char[] pieces = {'P', 'R', 'N', 'B', 'K', 'Q', 'p', 'r', 'n', 'b', 'k', 'q'};
        int numPieces = pieces.length;
        for (int i = 0; i < numPieces; i++) {
            long currentBit = bits[i];
            while (currentBit != 0) {
                long bit = currentBit & -currentBit;  // Get the least significant set bit
                int index = Long.numberOfTrailingZeros(bit);
                currentBit ^= bit;  // Clear the least significant set bit
                board[index / BOARD_SIZE][index % BOARD_SIZE] = pieces[i];
            }
        }
        return board;
    }

    public static void printBitboard(long bitboard) {
        String s = toString(bitboard);
        LOGGER.info(s);
    }
    public static void printBoard(long[] bits) {
        String s = toString(bits);
        LOGGER.info(s);
    }

    public static String toString(long[] bits) {
        char[][] cb = bitsToCharArray(bits, new char[8][8]);
        StringBuilder s = new StringBuilder(System.lineSeparator());
        for (int i = 0; i < BOARD_SIZE; i++) {
            s.append(Arrays.toString(cb[i])).append(System.lineSeparator());
        }
        return s.toString();
    }
    public static String toString(long bitboard) {
        char[][] cb = new char[BOARD_SIZE][BOARD_SIZE];
        for (char[] row : cb) {
            Arrays.fill(row, '.');
        }

        while (bitboard != 0) {
            long bit = bitboard & -bitboard;  // Get the least significant set bit
            int i = Long.numberOfTrailingZeros(bit);
            bitboard ^= bit;  // Clear the least significant set bit
            cb[i / BOARD_SIZE][i % BOARD_SIZE] = '@';
        }
        StringBuilder s = new StringBuilder(System.lineSeparator());
        for (int i = 0; i < BOARD_SIZE; i++) {
            s.append(Arrays.toString(cb[i])).append(System.lineSeparator());
        }
        return s.toString();
    }

    /**
     * Pretty print the chess board bits.
     *
     */
    public static void prettyPrintBoard(long[] bits) {
        char[][] cb = bitsToCharArray(bits, new char[8][8]);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cb[i][j] = charToFancyMap.get(cb[i][j]);
            }
        }
        StringBuilder s = new StringBuilder();
        LOGGER.info("┏━━━━━━━━┓");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                char piece = cb[i][j];
                if ((i + j) % 2 == 0) {
                    s.append(piece);
                } else {
                    s.append("\u001B[47m").append(piece).append("\u001B[0m");
                }
            }
            String n = s.toString();
            LOGGER.info("┃{}┃", n);
            s = new StringBuilder();
        }
        LOGGER.info("┗━━━━━━━━┛");
    }

}
