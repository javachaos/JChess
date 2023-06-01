package com.github.javachaos.jchess.gui;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.github.javachaos.jchess.utils.ImageLoader;

public class PieceLabel extends JLabel {

    private static final long serialVersionUID = 2806743855183337753L;
    private final transient ImageLoader imgLoader;
    private char piece = '.';

    public PieceLabel(ImageLoader imgLoader) {
        super();
        this.imgLoader = imgLoader;
        setFont(new Font("Lucida Grande", Font.PLAIN, 8));
    }

    public void setPiece(char p) {
    	this.piece = p;
        ImageIcon originalImg;
        if (p == '.') {
        	setIcon(null);
        } else {
	        originalImg = imgLoader.getImageForPiece(p);
	        setIcon(originalImg);
        }
    }

	public char getPiece() {
		return piece;
	}
}
