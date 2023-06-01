package com.github.javachaos.jchess.moves;

import com.github.javachaos.jchess.utils.BitUtils;

import java.util.Objects;

@SuppressWarnings("unused")
public class Move implements Comparable<Move> {
	
	private final Pos from;
	private final Pos to;
	private final char promotion;
	
	public Move(Pos from, Pos to, char promotion) {
		this.from = from;
		this.to = to;
		this.promotion = promotion;
	}
	
	
	public Pos from() {
		return from;
	}
	
	public Pos to() {
		return to;
	}
	
	public char promotion() {
		return promotion;
	}
	
    public static Move random() {
        return new Move(Pos.random(), Pos.random(), '.');
    }

    public String toString() {
        return String.valueOf(from) + to + (promotion == '.' ? "" : promotion);
    }

    public long fromBitboard() {
        return 1L << from.index();
    }

    public long toBitboard() {
        return 1L << to.index();
    }

    public Move reverse() {
        return new Move(to, from, '.');
    }

    /**
     * Create a move from a string of length 4-5 characters.
     * This implementation should be quite fast. O(1) * ~20.
     *
     * @param move string representing a move, the move should be valid
     *             if the move is invalid it could lead to buffer overflows
     *             ect, no checking is done in this method
     * @return a move representing the input string
     */
    public static Move fromString(String move) {
        return new Move(
                new Pos(move.charAt(0), move.charAt(1),
                        BitUtils.getIndex(move.charAt(0), move.charAt(1))),
                new Pos(move.charAt(2), move.charAt(3),
                        BitUtils.getIndex(move.charAt(2), move.charAt(3))),
                (move.length() == 5) ? move.charAt(4) : '.');
    }

    @Override
    public int compareTo(Move o) {
        if (promotion != '.' && o.promotion == '.') {
            return 1;
        }
        if (promotion == '.' && o.promotion != '.') {
            return -1;
        }
        return 0;
        //add better move ordering later.
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Move) {
            return o.toString().equals(toString());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, from, promotion);
    }
}
