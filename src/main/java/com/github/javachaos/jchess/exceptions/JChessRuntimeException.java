package com.github.javachaos.jchess.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JChessRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 855644582626801423L;
	
    private static final Logger LOGGER = LogManager.getLogger(JChessRuntimeException.class);
    public JChessRuntimeException(Exception e) {
        super(e);
        LOGGER.error(e.getMessage());
    }
}
