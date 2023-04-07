package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.EnumMap;

public class ImageLoader {

    public static final Logger LOGGER = LogManager.getLogger(
            ImageLoader.class);

    private final EnumMap<AbstractPiece.PieceType,
            Pair<Image, Image>> images = new EnumMap<>(AbstractPiece.PieceType.class);

    public ImageLoader() {
        loadImages();
    }

    private void loadImages() {
        images.put(AbstractPiece.PieceType.PAWN, new Pair<>(
                new Image(getImg("pawn_white")),
                new Image(getImg("pawn_black"))));
        images.put(AbstractPiece.PieceType.BISHOP, new Pair<>(
                new Image(getImg("bishop_white")),
                new Image(getImg("bishop_black"))));
        images.put(AbstractPiece.PieceType.ROOK, new Pair<>(
                new Image(getImg("rook_white")),
                new Image(getImg("rook_black"))));
        images.put(AbstractPiece.PieceType.KNIGHT, new Pair<>(
                new Image(getImg("knight_white")),
                new Image(getImg("knight_black"))));
        images.put(AbstractPiece.PieceType.KING, new Pair<>(
                new Image(getImg("king_white")),
                new Image(getImg("king_black"))));
        images.put(AbstractPiece.PieceType.QUEEN, new Pair<>(
                new Image(getImg("queen_white")),
                new Image(getImg("queen_black"))));
    }

    private InputStream getImg(final String name) {
        String imgName = Constants.IMG_DIR + name + Constants.PNG;
        LOGGER.debug("Loading image: {}", imgName);
        return getClass().getResourceAsStream(imgName);
    }

    public ImageView getImageForPiece(Piece p) {
        if (p.isBlack()) {
            return new ImageView(images.get(p.getType()).getValue());
        } else {
            return new ImageView(images.get(p.getType()).getKey());
        }
    }

}
