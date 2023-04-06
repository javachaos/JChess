package com.github.javachaos.jchess.exceptions;

import java.io.Serial;

public class JChessException extends Exception {
    @Serial
    private static final long serialVersionUID = 1170632340035743290L;

    public JChessException(String msg) {
        super(msg);
    }
}
