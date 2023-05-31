package com.github.javachaos.jchess.gui;

import static com.github.javachaos.jchess.utils.Constants.BOARD_SIZE;
import static com.github.javachaos.jchess.utils.Constants.CHECKERBOARD_IMG;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javachaos.jchess.logic.ChessBoard;
import com.github.javachaos.jchess.moves.Move;
import com.github.javachaos.jchess.utils.ImageLoader;

public class SwingGui extends JFrame implements MouseListener {
	
	private static final Logger LOGGER = LogManager.getLogger(SwingGui.class);

    @Serial
    private static final long serialVersionUID = 2960246382559537774L;
    
    private String nextMove = "";
    private final ImagePanel board = new ImagePanel(CHECKERBOARD_IMG);
    private final String[] files = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private final String[] ranks = {"8", "7", "6", "5", "4", "3", "2", "1"};
    private final PieceLabel[][] squares = new PieceLabel[files.length][ranks.length];
    private boolean showLabels = false;
    private final transient ChessBoard cb;
    private static final String GAME_STATE_LABEL = "Game State:";
    private final JLabel gameStateLabel = new JLabel(GAME_STATE_LABEL);

    public SwingGui() {
    	cb = new ChessBoard();
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

        gameStateLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
        topBar.add(gameStateLabel);

        JButton toggleLabelsBtn = new JButton("Toggle Labels");
        toggleLabelsBtn.addActionListener(this::toggleLabels);
        topBar.add(toggleLabelsBtn);

        board.setLayout(new GridLayout(8, 8, 0, 0));
        getContentPane().add(board);
        addMouseListener(this);
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
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        draw(cb);
    }

    /**
     * Given a fen string, draw the pieces on the board.
     */
    public void draw(final ChessBoard chessboard) {
        char[][] pieces = chessboard.toCharArray();
        for (int j = 0; j < BOARD_SIZE; j++) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (pieces[j][i] == '.') {
                    squares[i][j].setPiece(pieces[j][i]);
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
    }

	public void mouseEvent(char piece, String name) {
		String fromTo = name.toLowerCase();
		LOGGER.info("{} {}", piece, fromTo);
		gameStateLabel.setText(GAME_STATE_LABEL + " " + fromTo);
		nextMove += fromTo;
		if (nextMove.length() == 4) {
			if (cb.makeMove(Move.fromString(nextMove))) {
			  gameStateLabel.setText("Move: " + cb.getLastMove().toString());
			} else {
			  gameStateLabel.setText("Move invalid: " + nextMove);
			}
			nextMove = "";
		}
		highlightSquare(fromTo.substring(0,2));
		validate();
		repaint();
	}

	private void highlightSquare(String name) {
        for (int j = 0; j < BOARD_SIZE; j++) {
            for (int i = 0; i < BOARD_SIZE; i++) {
            	squares[i][j].setOpaque(false);
                board.repaint();
            	if (squares[i][j].getName().equalsIgnoreCase(name)) {
            		squares[i][j].setOpaque(true);
            		squares[i][j].setForeground(Color.DARK_GRAY);
                    board.repaint();
            	}
            }
        }
        
	}

	@Override
	public void mouseClicked(MouseEvent e) {
        //Unused
	}

	@Override
	public void mousePressed(MouseEvent e) {
        for (int j = 0; j < BOARD_SIZE; j++) {
            for (int i = 0; i < BOARD_SIZE; i++) {
            	Point p = squares[i][j].getMousePosition(true);
            	if (p != null) {
            		mouseEvent(squares[i][j].getPiece(), squares[i][j].getName());
            	}
            }
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		LOGGER.info("released.");
        for (int j = 0; j < BOARD_SIZE; j++) {
            for (int i = 0; i < BOARD_SIZE; i++) {
            	Point p = squares[i][j].getMousePosition(true);
            	if (p != null) {
            		squares[i][j].setOpaque(false);
					validate();
					repaint();
            	}
            }
        }

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		LOGGER.info("entered.");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		LOGGER.info("exited.");
	}

}
