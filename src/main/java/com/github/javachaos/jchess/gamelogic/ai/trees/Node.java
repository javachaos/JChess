package com.github.javachaos.jchess.gamelogic.ai.trees;

import com.github.javachaos.jchess.gamelogic.Board;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private Board board;

    int score;

    List<Node> children;

    public Node(Board board) {
        this.board = board;
        children = new ArrayList<>();
    }

    public Board getBoard() {
        return board;
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
