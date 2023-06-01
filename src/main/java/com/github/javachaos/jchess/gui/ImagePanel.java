package com.github.javachaos.jchess.gui;

import com.github.javachaos.jchess.utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImagePanel extends JPanel {

    private static final long serialVersionUID = -1794897510906937045L;
    private transient BufferedImage backgroundImage;

    public ImagePanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(getImg(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getImg(final String name) {
        String imgName = Constants.IMG_DIR + name + Constants.PNG;
        return getClass().getResourceAsStream(imgName);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
