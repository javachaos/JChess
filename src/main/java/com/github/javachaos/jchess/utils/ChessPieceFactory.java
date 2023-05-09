package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.pieces.impl.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ChessPieceFactory {

    private ChessPieceFactory() {}

    public static Piece createPiece(AbstractPiece.PieceType type, Player color, PiecePos piecePos) {
        Map<AbstractPiece.PieceType, Supplier<Piece>> pieceMap = new EnumMap<>(Map.of(
                AbstractPiece.PieceType.PAWN, () -> new Pawn(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.ROOK, () -> new Rook(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.BISHOP, () -> new Bishop(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.KNIGHT, () -> new Knight(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.KING, () -> new King(color, piecePos.x(), piecePos.y()),
                AbstractPiece.PieceType.QUEEN, () -> new Queen(color, piecePos.x(), piecePos.y())
        ));

        return pieceMap.getOrDefault(type, () -> null).get();
    }
}
