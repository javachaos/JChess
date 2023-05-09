package com.github.javachaos.jchess.gamelogic.ai.trees;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.utils.ExceptionUtils;

import java.util.*;

public class GameTree {
    private static final int MAX_DEPTH = 3;

    private Node currentTree;

    private boolean treePresent;

    public GameTree() {
        //Unused
    }

    /**
     * Create a new game tree for the board cb
     *
     * @param cb the chessboard
     * @param p the player we are creating this tree for.
     */
    private void updateTree(Board cb, Player p) {
        Node n = new Node(cb);
        currentTree = buildTree(n, p, MAX_DEPTH);
        treePresent = true;
    }

    private int calculateDepth(Node node, int depth) {
        if (node.isLeaf()) {
            return depth;
        } else {
            int maxDepth = depth;
            for (Node child : node.getChildren()) {
                int childDepth = calculateDepth(child, depth + 1);
                if (childDepth > maxDepth) {
                    maxDepth = childDepth;
                }
            }
            return maxDepth;
        }
    }

    /**
     * Compute the depth of the current tree
     * @return the depth of this Game Tree
     */
    public int calculateDepth() {
        return calculateDepth(currentTree, 0);
    }


    public boolean isTreePresent() {
        return treePresent;
    }

    private Node buildTree(Node root, Player p, int depth) {
        Board board = GSM.instance().getBoard();
        if (depth <= 0
        || GSM.instance().getCurrentState() == GSM.GameState.STALEMATE
        || GSM.instance().getCurrentState() == GSM.GameState.CHECKMATE) {
            return root;
        } else {
            Board copy = board.deepCopy();
            for (Move m : getAllPossibleMoves(copy, p)) {
                try {
                    copy.movePiece(m.from(), m.to());
                } catch (JChessException e) {
                    ExceptionUtils.log(e);
                    continue;
                }
                Node childNode = new Node(copy);
                root.addChild(childNode);
                root.addChild(
                        buildTree(childNode,
                                p == Player.WHITE ? Player.BLACK : Player.WHITE, depth - 1));
                GSM.instance().undo();
            }
        }
        return root;
    }

    private List<Move> getAllPossibleMoves(Board b, Player color) {
        List<Move> moves = new ArrayList<>();
        for (Piece p : b.getPieces(color)) {
            b.getPotentialMoves(p.getPos()).forEach(pot -> {
                AbstractPiece.PieceType type = AbstractPiece.PieceType.NONE;
                Optional<Piece> op = b.getPiece(pot);
                if (op.isPresent()) {
                    type = op.get().getType();
                }
                moves.add(new Move(p.getPos(), pot, type, color));
            });
        }
        return moves;
    }

    private Move getBestMove(Node root) {
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (Node child : root.getChildren()) {
            int score = minimax(child, MAX_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getBoard().getLastMove();
            }
        }
        return bestMove;
    }

    private int minimax(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || node.isLeaf()) {
            return node.getBoard().boardScore(GSM.instance().getAI().getColor());
        }
        if (maximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;
            for (Node child : node.getChildren()) {
                int score = minimax(child, depth - 1, alpha, beta, false);
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (Node child : node.getChildren()) {
                int score = minimax(child, depth - 1, alpha, beta, true);
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        }
    }

    public Move getAIMove(Board b, Player p) {
        if (currentTree == null) {
            MinimaxAIPlayer.LOGGER.info("Current game tree null.");
            updateTree(b, p);
        }
        return getBestMove(currentTree);
    }
}
