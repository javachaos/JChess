package com.github.javachaos.jchess.gamelogic.ai.trees;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final String fen;

    private MinimaxAIPlayer player;

    int score;

    List<Node> children;

    public Node(MinimaxAIPlayer player, String fen) {
        this.fen = fen;
        this.player = player;
        children = new ArrayList<>();
    }

    public Node(Board board) {
        this.player = (MinimaxAIPlayer) board.getAI();
        this.fen = board.getFenString();
        children = new ArrayList<>();
    }

    public Board getBoard() {
        return new ChessBoard(player, fen);
    }

    public void addChild(Node n) {
        children.add(n);
    }

    public Node[] getChildren() {
        return children.toArray(new Node[0]);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}
