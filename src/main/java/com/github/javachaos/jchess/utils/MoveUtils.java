package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.moves.Move;
import com.github.javachaos.jchess.moves.Pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.javachaos.jchess.utils.BitUtils.*;

@SuppressWarnings("unused")
public class MoveUtils {

    private MoveUtils() {
        //Unused
    }

    static final int BOARD_SIZE = 8;
    static final long RANK_1  = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    static final long RANK_2  = 0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
    static final long RANK_3  = 0b00000000_00000000_11111111_00000000_00000000_00000000_00000000_00000000L;
    static final long RANK_4  = 0b00000000_00000000_00000000_11111111_00000000_00000000_00000000_00000000L;
    static final long RANK_5  = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
    static final long RANK_6  = 0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L;
    static final long RANK_7  = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000L;
    static final long RANK_8  = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_11111111L;
    static final long FILE_A  = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;
    static final long FILE_B  = 0b01000000_01000000_01000000_01000000_01000000_01000000_01000000_01000000L;
    static final long FILE_C  = 0b00100000_00100000_00100000_00100000_00100000_00100000_00100000_00100000L;
    static final long FILE_D  = 0b00010000_00010000_00010000_00010000_00010000_00010000_00010000_00010000L;
    static final long FILE_E  = 0b00001000_00001000_00001000_00001000_00001000_00001000_00001000_00001000L;
    static final long FILE_F  = 0b00000100_00000100_00000100_00000100_00000100_00000100_00000100_00000100L;
    static final long FILE_G  = 0b00000010_00000010_00000010_00000010_00000010_00000010_00000010_00000010L;
    static final long FILE_H  = 0b00000001_00000001_00000001_00000001_00000001_00000001_00000001_00000001L;
    static final long FILE_AB = 0b11000000_11000000_11000000_11000000_11000000_11000000_11000000_11000000L;
    static final long FILE_GH = 0b00000011_00000011_00000011_00000011_00000011_00000011_00000011_00000011L;
    static final long KING_SIDE = 0b00001111_00001111_00001111_00001111_00001111_00001111_00001111_00001111L;
    static final long QUEEN_SIDE = 0b11110000_11110000_11110000_11110000_11110000_11110000_11110000_11110000L;
    static final long NOT_A_FILE = ~FILE_A;
    static final long NOT_H_FILE = ~FILE_H;
    static final long NOT_RANK_8 = ~RANK_8;
    static final long NOT_RANK_1 = ~RANK_1;
    static final int NUM_PIECES = 12;

    static long southOne(long b) {
        return b >> 8;
    }

    static long northOne(long b) {
        return b << 8;
    }

    static long eastOne(long b) {
        return (b << 1) & NOT_A_FILE;
    }

    static long noEaOne(long b) {
        return (b << 9) & NOT_A_FILE;
    }

    static long soEaOne(long b) {
        return (b >> 7) & NOT_A_FILE;
    }

    static long westOne(long b) {
        return (b >> 1) & NOT_H_FILE;
    }

    static long soWeOne(long b) {
        return (b >> 9) & NOT_H_FILE;
    }

    static long noWeOne(long b) {
        return (b << 7) & NOT_H_FILE;
    }

    /**
     * high = ~1 << d4
     *  1 1 1 1 1 1 1 1
     *  1 1 1 1 1 1 1 1
     *  1 1 1 1 1 1 1 1
     *  1 1 1 1 1 1 1 1
     *  . . . . 1 1 1 1
     *  . . . . . . . .
     *  . . . . . . . .
     *  . . . . . . . .
     * @param b single bit position
     * @return the bitboard representing all the squares above b
     */
    static long upper(long b) { return ~1L << b; }

    /**
     * low = (1<<d4)-1
     *  . . . . . . . .
     *  . . . . . . . .
     *  . . . . . . . .
     *  . . . . . . . .
     *  1 1 1 . . . . .
     *  1 1 1 1 1 1 1 1
     *  1 1 1 1 1 1 1 1
     *  1 1 1 1 1 1 1 1
     * @param b single bit position
     * @return the bitboard representing all the squares below b
     */
    static long lower(long b) { return (1L << b) - 1; }

    /**
     * swap n none overlapping bits of bit-index i with j
     * @param b any bitboard
     * @param i,j positions of bit sequences to swap
     * @param n number of consecutive bits to swap
     * @return bitboard b with swapped bit-sequences
     */
    static long swapNBits(long b, int i, int j, int n) {
        long    m = ( 1L << n) - 1;
        long    x = ((b >> i) ^ (b >> j)) & m;
        return  b ^ (x << i) ^ (x << j);
    }

    /**
     * swap any none overlapping pairs of bits
     *   that are delta places apart
     * @param b any bitboard
     * @param mask has a 1 on the least significant position
     *             for each pair supposed to be swapped
     * @param delta of pairwise swapped bits
     * @return bitboard b with bits swapped
     */
    static long deltaSwap(long b, long mask, int delta) {
        long x = (b ^ (b >> delta)) & mask;
        return   x ^ (x << delta)  ^ b;
    }

    /**
     * Given file and rank, return the piece on the board (if any) at that location.
     * @param file the col or x [a-h]
     * @param rank the row or y [1-8]
     * @return the piece at rank/file on the board
     */
    static long getPiece(long[] bits, char file, char rank) {
        long fileMask = FILES[file - 'a'];
        long rankMask = RANKS[rank - '1'];
        long locationMask = fileMask & rankMask;

        for (long piece : bits) {
            if ((piece & locationMask) != 0) {
                return piece;
            }
        }

        return 0;
    }

    /**
     * Given a move, get the piece on the board at the from-square.
     * -1 if there is no piece.
     * @param m the move
     * @return piece index [0-11]
     */
    static int getPiece(long[] bits, Move m) {
        for (int i = 0; i < NUM_PIECES; i++) {
            long rowMaskFrom = RANKS[m.from().rank() - '1'];
            long colMaskFrom = FILES['h' - m.from().file()];

            long rowMaskTo = RANKS[m.to().rank() - '1'];
            long colMaskTo = FILES['h' - m.to().file()];

            long resultFrom = (rowMaskFrom & colMaskFrom) & bits[i];
            long resultTo = (rowMaskTo & colMaskTo) & bits[i];

            if (resultFrom != 0 || resultTo != 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the character representation of the piece at the from-square of move m.
     *
     * @param bits the bitboard representation of the chess board
     * @param m the move
     * @return the character at the from-square of move m
     */
    static char getPieceChar(long[] bits, Move m) {
        int index = getPiece(bits, m);
        if (index == -1) {
            return '.';
        }
        return indexToPiece(index);
    }

    static char indexToPiece(int index) {
        char[] pieces = {'P', 'R', 'N', 'B', 'K', 'Q', 'p', 'r', 'n', 'b', 'k', 'q'};
        return pieces[index];
    }


    public static int getIndex(char file, char rank) {
        int s;
        s = (Character.toUpperCase(file)) - 65;
        s += 8 * (56 - rank);
        return s;
    }

    static Pos indexToPos(int rank, int file, int index) {
        if (rank < 0) {
            rank = 0;
        }
        if (file < 0) {
            file = 0;
        }
        return new Pos((char) ('a' + file), (char) ('8' - rank), index);
    }

    /**
     * Given an array of moves and the moveOccupancy bitboard
     * return a list of all non-null moves from move array moves.
     * All set bits in moveOccupancy correspond to index's of the moves
     * in moves.
     *
     * @param moves the sparse list of moves (contains nulls)
     * @param moveOccupancy the bitboard indexing all the moves in moves array
     * @return the moves as a List of moves with no nulls.
     */
    static List<Move> getMoves(Move[] moves, long moveOccupancy) {
        int numMoves = Long.bitCount(moveOccupancy);
        if (numMoves > 0) {
            List<Move> moveList = new ArrayList<>(numMoves);
            while (moveOccupancy != 0) {
                long bit = moveOccupancy & -moveOccupancy;  // Get the least significant set bit
                int index = Long.numberOfTrailingZeros(bit);
                moveOccupancy ^= bit;  // Clear the least significant set bit
                moveList.add(moves[index]);
            }
            return moveList;
        }
        return Collections.emptyList();
    }

    /**
     * Perform a move m on a board bits.
     *
     * @param bits the bitboard array for each differnt piece [0-11]
     * @param m the current move
     * @param lastMove the last move
     * @param turn is it whites turn (true for white, false for black)
     * @return true if the move is valid false otherwise
     */
    public static boolean doMove(long[] bits, Move m, Move lastMove, int turn) {
        if (getAllPossibleMoves(bits, lastMove, turn).contains(m)) {
            //test
            BitUtils.updateBoards(bits, m);
            BitUtils.lastMove = m;
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    static void undoMove(long[] bits, Move m) {
        //implement/test
    }


}
