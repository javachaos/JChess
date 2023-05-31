package com.github.javachaos.jchess;

import com.github.javachaos.jchess.gui.SwingGui;
import javax.swing.*;

public class JChessApplication {
    private final SwingGui gui;

    JChessApplication() {
        gui = new SwingGui();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JChessApplication().show());
    }

    public void show() {
        gui.setSize(800, 800);
        gui.setResizable(false);
        gui.setVisible(true);
    }
}