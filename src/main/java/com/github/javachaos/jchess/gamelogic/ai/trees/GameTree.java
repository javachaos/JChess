package com.github.javachaos.jchess.gamelogic.ai.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.ai.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;

public class GameTree {
    private static final int MAX_DEPTH = 2;
    
    private HashMap<String, Integer> transpositionTable = new HashMap<>();

    private Node currentTree;

    private boolean treePresent;

    private final ChessGame game;

    public GameTree(ChessGame game) {
        this.game = game;
        currentTree = null;
    }

    /**
     * Create a new game tree for the board cb
     *
     * @param cb the chessboard
     * @param p the player we are creating this tree for.
     */
    private void updateTree(Board cb, Player p) {
        Node n = new Node(cb.getFenString(), cb.boardScore(p), p, cb.getLastMove());
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
        if (depth <= 0) {
            return root;
        }
        Board copy = game.getBoard().deepCopy();
        for (Move m : getAllPossibleMoves(copy, p)) {
	        try {
	            copy.movePiece(m.from(), m.to());
	        } catch (JChessException e) {
	            //ExceptionUtils.log(e);
	            continue;
	        }
            Node childNode = new Node(copy.getFenString(), copy.boardScore(p), p, m);
            root.addChild(childNode);
            buildTree(childNode,
                    p == Player.WHITE ? Player.BLACK : Player.WHITE, depth - 1);
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
                bestMove = child.getLastMove();
            }
        }
        return bestMove;
    }
    
    private int minimax(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        // Check if the position is already in the transposition table
        String positionKey = node.getFen(); // Use a unique identifier for the position (e.g., FEN string)
        if (transpositionTable.containsKey(positionKey)) {
            return transpositionTable.get(positionKey);
        }

        if (depth == 0 || node.isLeaf()) {
            int score = new ChessBoard(null, positionKey).boardScore(game.getBoard().getAI().getColor());
            node.setScore(score); // Store the score in the current node
            transpositionTable.put(positionKey, score); // Store the score in the transposition table
            return score;
        }
        int bestScore;
        if (maximizingPlayer) {
            bestScore = Integer.MIN_VALUE;
            for (Node child : node.getChildren()) {
                int score = minimax(child, depth - 1, alpha, beta, false);
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (Node child : node.getChildren()) {
                int score = minimax(child, depth - 1, alpha, beta, true);
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        node.setScore(bestScore); // Store the best score in the current node
        return bestScore;
    }


    public Move getAIMove(Board b, Player p) {
        if (currentTree == null) {
            MinimaxAIPlayer.LOGGER.info("Current game tree null.");
            updateTree(b, p);
        }
        Node n = new Node(
        		b.getFenString(),
        		b.boardScore(b.getAI().getColor()),
        		p,
        		b.getLastMove());
        currentTree = buildTree(n, p, MAX_DEPTH);
		return getBestMove(currentTree);
    }
}
