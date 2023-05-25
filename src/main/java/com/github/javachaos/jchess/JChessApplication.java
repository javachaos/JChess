package com.github.javachaos.jchess;

import javax.swing.SwingUtilities;

import com.github.javachaos.jchess.gui.SwingGui;
import com.github.javachaos.jchess.logic.ChessBoard;

public class JChessApplication {
	private final SwingGui gui;
    private final ChessBoard cb = new ChessBoard();
	JChessApplication() {
    	gui = new SwingGui();
    }

    public void show() {
    	gui.setSize(800, 800);
    	gui.setResizable(false);
    	gui.setVisible(true);
    	gui.draw(cb);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JChessApplication().show());
    }
}