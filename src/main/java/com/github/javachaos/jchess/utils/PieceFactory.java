package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.core.SimplePiece;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;
import com.github.javachaos.jchess.gamelogic.player.Player;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class PieceFactory {

    private PieceFactory() {}

    private static final Map<Character, Function<PiecePos, Piece>> pieceMap = Map.ofEntries(
            Map.entry('r', pos -> new Rook(Player.BLACK, pos.x(), pos.y())),
            Map.entry('n', pos -> new Knight(Player.BLACK, pos.x(), pos.y())),
            Map.entry('b', pos -> new Bishop(Player.BLACK, pos.x(), pos.y())),
            Map.entry('k', pos -> new King(Player.BLACK, pos.x(), pos.y())),
            Map.entry('q', pos -> new Queen(Player.BLACK, pos.x(), pos.y())),
            Map.entry('p', pos -> new Pawn(Player.BLACK, pos.x(), pos.y())),
            Map.entry('R', pos -> new Rook(Player.WHITE, pos.x(), pos.y())),
            Map.entry('N', pos -> new Knight(Player.WHITE, pos.x(), pos.y())),
            Map.entry('B', pos -> new Bishop(Player.WHITE, pos.x(), pos.y())),
            Map.entry('K', pos -> new King(Player.WHITE, pos.x(), pos.y())),
            Map.entry('Q', pos -> new Queen(Player.WHITE, pos.x(), pos.y())),
            Map.entry('P', pos -> new Pawn(Player.WHITE, pos.x(), pos.y()))
    );
    public static Piece createPiece(AbstractPiece.PieceType type, Player color, PiecePos piecePos) {
        Map<AbstractPiece.PieceType, Supplier<Piece>> mapping = Map.of(
                AbstractPiece.PieceType.PAWN, () -> new Pawn(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.ROOK, () -> new Rook(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.BISHOP, () -> new Bishop(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.KNIGHT, () -> new Knight(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.KING, () -> new King(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.QUEEN, () -> new Queen(color, piecePos.x(), piecePos.y())
        );

        return mapping.getOrDefault(type, () -> null).get();
    }
    public static Piece fromSimple(SimplePiece sp) {
        return pieceMap.getOrDefault(sp.c(), unused -> {
            throw new IllegalArgumentException("Invalid FEN character: " + sp.c());
        }).apply(sp.pos());
    }

    public static char getPieceFENSymbol(Piece piece) {
        char symbol = switch (piece.getType()) {
            case PAWN -> 'P';
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case KING -> 'K';
            case QUEEN -> 'Q';
            default -> ' ';
        };
        return piece.isWhite() ? symbol : Character.toLowerCase(symbol);
    }
}
