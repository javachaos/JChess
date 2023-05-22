package com.github.javachaos.jchess.utils;

import java.io.File;

public class Constants {

    public static final File SAVE_DIR = new File(System.getProperty("user.home") + "/.jchess_saves");

    private Constants() {
        //Unused
    }

    public static final String UNDO_SAVEFILE = "./jchess_undo.json";
    public static final String REDO_SAVEFILE = "./jchess_redo.json";

    public static final String PNG = ".png";
    public static final String IMG_DIR = "/img/";
}
