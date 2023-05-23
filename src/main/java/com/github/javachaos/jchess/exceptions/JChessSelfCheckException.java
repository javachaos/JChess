package com.github.javachaos.jchess.exceptions;

public class JChessSelfCheckException extends JChessException {
    private static final long serialVersionUID = 1275422254045403384L;

	public JChessSelfCheckException(String msg) {
        super(msg);
    }
}
