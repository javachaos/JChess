package com.github.javachaos.jchess.utils;

import java.io.File;

public class Constants {

    public static final File DEFAULT_SAVE_DIR = new File(System.getProperty("user.home"));
    public static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1";

    private Constants() {
        //Unused
    }

    public static final String PNG = ".png";
    public static final String IMG_DIR = "/img/";
}
