package com.github.javachaos.jchess.gamelogic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JChessException extends Exception {
    private static final long serialVersionUID = 1170632340035743290L;
	public static final Logger LOGGER = LogManager.getLogger(
            JChessException.class);

    public JChessException(String msg) {
        super(msg);
    }
}
