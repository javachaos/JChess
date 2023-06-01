package com.github.javachaos.jchess.moves;


import com.github.javachaos.jchess.utils.BitUtils;
import com.github.javachaos.jchess.utils.RandUtils;

public class Pos {
	
	private final char file;
	private final char rank;
	private final int index;
	
	public Pos(char file, char rank, int index) {
		this.file = file;
		this.rank = rank;
		this.index = index;
	}
	
	public char file() {
		return file;
	}
	
	public char rank() {
		return rank;
	}
	
	public int index() {
		return index;
	}
	
    public static Pos random() {
        char f = (char) ('a' + RandUtils.random.nextInt(8));
        char r = (char) ('1' + RandUtils.random.nextInt(8));
        return new Pos(f, r, BitUtils.getIndex(f, r));
    }

    public String toString() {
        return String.valueOf(file) + rank;
    }
}
