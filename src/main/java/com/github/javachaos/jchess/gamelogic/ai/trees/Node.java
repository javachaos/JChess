package com.github.javachaos.jchess.gamelogic.ai.trees;

import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;

import java.util.LinkedHashSet;
import java.util.Set;

public class Node {

    private final String fen;

    private final Player activePlayer;

    private final int score;

    private final Move lastMove;

    private final Set<Node> children;

    public Node(String fen, int score, Player activePlayer, Move lastMove) {
        this.score = score;
        this.fen = fen;
        this.activePlayer = activePlayer;
        this.children = new LinkedHashSet<>();
        this.lastMove = lastMove;
    }

    public void addChild(Node n) {
        children.add(n);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Node[] getChildren() {
        return children.toArray(new Node[0]);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String getFen() {
        return fen;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

}
