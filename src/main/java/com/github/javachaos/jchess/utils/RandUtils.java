package com.github.javachaos.jchess.utils;

import java.util.*;

@SuppressWarnings("unused")
public class RandUtils {
    private static final Random random = Random.from(new SplittableRandom());

    private RandUtils() {}

    public static char[][] getRandomBoard() {
        char[][] board = new char[8][8];
        for (char[] c : board) {
            Arrays.fill(c, '.');
        }

        // List of available pieces to choose from
        Deque<Character> availablePieces = new ArrayDeque<>();
        availablePieces.add('K');  // White King
        availablePieces.add('Q');  // White Queen
        availablePieces.add('R');  // White Rook
        availablePieces.add('R');  // White Rook
        availablePieces.add('B');  // White Bishop
        availablePieces.add('B');  // White Bishop
        availablePieces.add('N');  // White Knight
        availablePieces.add('N');  // White Knight
        availablePieces.add('k');  // Black King
        availablePieces.add('q');  // Black Queen
        availablePieces.add('r');  // Black Rook
        availablePieces.add('r');  // Black Rook
        availablePieces.add('b');  // Black Bishop
        availablePieces.add('b');  // Black Bishop
        availablePieces.add('n');  // Black Knight
        availablePieces.add('n');  // Black Knight

        availablePieces.add('p');  // Black Pawn
        availablePieces.add('p');  // Black Pawn
        availablePieces.add('p');  // Black Pawn
        availablePieces.add('p');  // Black Pawn

        availablePieces.add('p');  // Black Pawn
        availablePieces.add('p');  // Black Pawn
        availablePieces.add('p');  // Black Pawn
        availablePieces.add('p');  // Black Pawn

        availablePieces.add('P');  // White Pawn
        availablePieces.add('P');  // White Pawn
        availablePieces.add('P');  // White Pawn
        availablePieces.add('P');  // White Pawn

        availablePieces.add('P');  // White Pawn
        availablePieces.add('P');  // White Pawn
        availablePieces.add('P');  // White Pawn
        availablePieces.add('P');  // White Pawn

        int randStopValue = random.nextInt(32);
        // Place the non-pawn pieces randomly on the board
        for (char piece : availablePieces) {
            if (randStopValue-- < 0) {
                break;
            }
            int row = random.nextInt(8);
            int col = random.nextInt(8);
            if (board[row][col] == '.') {  // Check if the square is empty
                board[row][col] = availablePieces.pop();
            }
        }

        return board;
    }
}
