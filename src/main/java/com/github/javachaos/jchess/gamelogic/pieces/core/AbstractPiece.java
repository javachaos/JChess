package com.github.javachaos.jchess.gamelogic.pieces.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javachaos.jchess.gamelogic.Board;

public abstract class AbstractPiece implements Piece {

    public enum Player {
        BLACK,
        WHITE
    }
    public enum PieceType {
        PAWN,
        ROOK,
        BISHOP,
        KNIGHT,
        KING,
        QUEEN
    }
    protected PiecePos pos;
    protected final Player color;
    protected boolean isCaptured;

    public AbstractPiece(Player p, char x, char y) {
        this.color = p;
        this.pos = new PiecePos(x, y);
    }

    public AbstractPiece(Player p, PiecePos pp) {
        this(p, pp.x(), pp.y());
    }

    /**
     * Get the pieces between from and to.
     *
     * @param from the from pos
     * @param to the to pos
     *
     * @return the list of pieces between from and to positions
     */
    protected List<Piece> getPiecesLateral(Board b, PiecePos from, PiecePos to) {
        List<Piece> pieces = new ArrayList<>();
        //check if this is a horizontal or vertical scan
        if (from.x() == to.x()) {
            IntStream.rangeClosed(from.y(), to.y())
                    .forEachOrdered(v -> b.getPiece(
                            new PiecePos(from.x(), (char)v))
                            .ifPresent(pieces::add));
            IntStream.rangeClosed(to.y(), from.y())
                    .forEachOrdered(v -> b.getPiece(
                                    new PiecePos(from.x(), (char)v))
                            .ifPresent(pieces::add));
        }
        if (from.y() == to.y()) {
            IntStream.rangeClosed(from.x(), to.x())
                    .forEachOrdered(v -> b.getPiece(
                                    new PiecePos((char)v, from.y()))
                            .ifPresent(pieces::add));
            IntStream.rangeClosed(to.x(), from.x())
                    .forEachOrdered(v -> b.getPiece(
                                    new PiecePos((char)v, from.y()))
                            .ifPresent(pieces::add));
        }

        return pieces;
    }

    protected List<Piece> getPiecesDiagonal(Board b, PiecePos from, PiecePos to) {
        List<Piece> pieces = new ArrayList<>();
        int dx = Integer.compare(to.x() - from.x(), 0);
        int dy = Integer.compare(to.y() - from.y(), 0);
        int x = from.x() + dx;
        int y = from.y() + dy;
        while (x != to.x() && y != to.y()) {
            b.getPiece(new PiecePos((char) x, (char) y)).ifPresent(pieces::add);
            x += dx;
            y += dy;
        }
        b.getPiece(to).ifPresent(pieces::add);
        return pieces;
    }

    @Override
    public void move(char x, char y) {
        this.pos = new PiecePos(x, y);
    }

    @Override
    public void move(PiecePos p) {
        this.move(p.x(), p.y());
    }

    @Override
    public void capture() {
        isCaptured = true;
    }

    @Override
    public void resurrect() {
        isCaptured = false;
    }

    @Override
    public Player getPlayer() {
        return color;
    }

    @Override
    public Player getOpponent() {
        return isBlack() ? Player.WHITE : Player.BLACK;
    }

    @Override
    public boolean isBlack() {
        return color == Player.BLACK;
    }
    @Override
    public boolean isWhite() {
        return color == Player.WHITE;
    }
    @Override
    public PiecePos getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Piece p) {
            return p.getType() == this.getType()
                && p.getPlayer() == this.getPlayer()
                && p.getPos().equals(getPos());
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return (isBlack() ? "B_" : "W_") +
                getType().name() +
                getPos();
    }

}
