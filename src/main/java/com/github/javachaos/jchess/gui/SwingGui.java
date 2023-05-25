package com.github.javachaos.jchess.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.Serial;

import javax.swing.*;

import com.github.javachaos.jchess.logic.ChessBoard;
import com.github.javachaos.jchess.utils.Constants;
import com.github.javachaos.jchess.utils.ImageLoader;

public class SwingGui extends JFrame {

	private final ImagePanel board = new ImagePanel(Constants.CHECKERBOARD_IMG);
	private final String[] files = {"A", "B", "C", "D", "E", "F", "G", "H"};
	private final String[] ranks = {"8", "7", "6", "5", "4", "3", "2", "1"};
	private final PieceLabel[][] squares = new PieceLabel[files.length][ranks.length];

	private boolean showLabels = false;
	
	public SwingGui() {
		ImageLoader imgLoader = new ImageLoader();
		
    	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JToolBar bottomBar = new JToolBar();
		getContentPane().add(bottomBar, BorderLayout.SOUTH);
		
		JLabel progressBarLabel = new JLabel("Progress:");
		bottomBar.add(progressBarLabel);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		bottomBar.add(progressBar);
		
		JToolBar topBar = new JToolBar();
		getContentPane().add(topBar, BorderLayout.NORTH);
		
		JButton newGameBtn = new JButton("New Game");
		topBar.add(newGameBtn);
		
		JButton undoBtn = new JButton("Undo");
		topBar.add(undoBtn);
		
		JButton redoBtn = new JButton("Redo");
		topBar.add(redoBtn);
		
		JButton exitBtn = new JButton("Exit");
		exitBtn.addActionListener(e -> showExitConfirmationDialog(this));
		topBar.add(exitBtn);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		topBar.add(horizontalGlue);
		
		JLabel gameStateLabel = new JLabel("Game State:                                ");
		gameStateLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		topBar.add(gameStateLabel);
		
		JButton toggleLabelsBtn = new JButton("Toggle Labels");
		toggleLabelsBtn.addActionListener(this::toggleLabels);
		topBar.add(toggleLabelsBtn);
		
		board.setLayout(new GridLayout(8, 8, 0, 0));
		getContentPane().add(board);
		
		for (int j = 0; j < files.length; j++) {
		    for (int i = 0; i < ranks.length; i++) {
		        squares[i][j] = new PieceLabel(imgLoader);
		        squares[i][j].setHorizontalAlignment(SwingConstants.CENTER);
		        squares[i][j].setVerticalAlignment(SwingConstants.CENTER);
		        squares[i][j].setName(files[i] + ranks[j]);
		        board.add(squares[i][j]);
		    }
		}
	}
	
	private void toggleLabels(ActionEvent e) {
		for (int j = 0; j < files.length; j++) {
		    for (int i = 0; i < ranks.length; i++) {
		    	if (!showLabels) {
		            squares[i][j].setText(squares[i][j].getName());
		    	} else {
			        squares[i][j].setText("");
		    	}
		    }
		}
        board.revalidate();
        board.repaint();
        showLabels = !showLabels;
	}

	/**
	 * Given a fen string, draw the pieces on the board.
	 */
	public void draw(final ChessBoard chessboard) {
		char[][] pieces = chessboard.toCharArray();
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8;  i++) {
			if (pieces[j][i] == '.') {
				continue;
			}
	        squares[i][j].setPiece(pieces[j][i]);
            ImageIcon originalIcon = (ImageIcon) squares[i][j].getIcon();
            Image originalImage = originalIcon.getImage();
            // Calculate the desired size
            int labelWidth = squares[i][j].getWidth() - 16;
            int labelHeight = squares[i][j].getHeight() - 16;
            // Create a scaled version of the image
            Image scaledImage = originalImage.getScaledInstance(
            		labelWidth, labelHeight, Image.SCALE_SMOOTH);
            // Create a new ImageIcon from the scaled image
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            squares[i][j].setIcon(scaledIcon);
		}
		}
        board.revalidate();
        board.repaint();
	}

	@Serial
	private static final long serialVersionUID = 2960246382559537774L;
	
    private static void showExitConfirmationDialog(JFrame frame) {
        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to exit the application?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

}
