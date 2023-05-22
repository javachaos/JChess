package com.github.javachaos.jchess.exceptions;

import java.io.Serial;

public class JChessRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1982347384732834L;

    public JChessRuntimeException(String msg) {
        super(msg);
    }
}
