package com.github.javachaos.jchess.gui;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.github.javachaos.jchess.utils.ImageLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PieceLabel extends JLabel {

	@Serial
	private static final long serialVersionUID = 2806743855183337753L;

	private static final Logger LOGGER = LogManager.getLogger(PieceLabel.class);

	private final transient ImageLoader imgLoader;
	
	public PieceLabel(ImageLoader imgLoader) {
		super();
		this.imgLoader = imgLoader;
		setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
				LOGGER.info("{} clicked!", getName());
		    }
		});
	}
	public void setPiece(char p) {
        ImageIcon originalImg;
		originalImg = imgLoader.getImageForPiece(p);
		setIcon(originalImg);
	}
	
}
