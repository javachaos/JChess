package com.github.javachaos.jchess.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageLoader {

	private static final Logger LOGGER = LogManager.getLogger(ImageLoader.class);
	private Map<Character, BufferedImage> images;

	public ImageLoader() {
		loadImages();
	}

	private void loadImages() {
		try {
			images = Map.ofEntries(
					Map.entry('P', ImageIO.read(getImg("pawn_white"))),
					Map.entry('R', ImageIO.read(getImg("rook_white"))),
					Map.entry('N', ImageIO.read(getImg("knight_white"))),
					Map.entry('B', ImageIO.read(getImg("bishop_white"))),
					Map.entry('K', ImageIO.read(getImg("king_white"))),
					Map.entry('Q', ImageIO.read(getImg("queen_white"))),
					Map.entry('p', ImageIO.read(getImg("pawn_black"))),
			    	Map.entry('r', ImageIO.read(getImg("rook_black"))),
					Map.entry('n', ImageIO.read(getImg("knight_black"))),
				    Map.entry('b', ImageIO.read(getImg("bishop_black"))),
					Map.entry('k', ImageIO.read(getImg("king_black"))),
					Map.entry('q', ImageIO.read(getImg("queen_black"))));
		} catch (IOException e) {
			ExceptionUtils.fatalError(ImageLoader.class, e);
		}
	}

	private InputStream getImg(final String name) {
		String imgName = Constants.IMG_DIR + name + Constants.PNG;
		LOGGER.debug("Loading image: {}", imgName);
		return getClass().getResourceAsStream(imgName);
	}

	public ImageIcon getImageForPiece(char c) {
		return new ImageIcon(images.get(c));
	}

}
