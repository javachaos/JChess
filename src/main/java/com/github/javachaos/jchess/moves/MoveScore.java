package com.github.javachaos.jchess.moves;

public class MoveScore {

	private final Move m;
	private final int score;
    public MoveScore(Move m, int score) {
    	this.m = m;
    	this.score = score;
    }
    
    public Move move() {
    	return m;
    }
    
    public int score() {
    	return score;
    }
}
