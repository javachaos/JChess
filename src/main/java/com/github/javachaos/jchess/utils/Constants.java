package com.github.javachaos.jchess.utils;

import java.io.File;

public class Constants {

    public static final File DEFAULT_SAVE_DIR = new File(System.getProperty("user.home"));

    private Constants() {
        //Unused
    }

    public static final String PNG = ".png";
    public static final String IMG_DIR = "/img/";
}
