package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.exceptions.JChessSelfCheckException;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import com.github.javachaos.jchess.utils.ExceptionUtils;

public class WhitesTurnState extends AbstractGameState {

    public WhitesTurnState(ChessGame chessGame) {
        super(chessGame);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof StartState)  && !(game.getPrevState() instanceof BlacksTurnState)
        && !(game.getPrevState() instanceof WhiteCheckState)) {
            invalidState();
        }

        if (waitForWhitesMove()) {
            if (game.isInCheck(Player.WHITE)) {
                game.setState(new WhiteCheckState(game));
            } else {
                game.setState(new BlacksTurnState(game));
            }
        }
    }

    /**
     * In this method we query the controller every 100 milliseconds for a move for white
     * until we have received a move. Once a move is retrieved we attempt to update the model
     * with this move.
     *
     * @return true if whites move is a valid move.
     */
    private boolean waitForWhitesMove() {
        boolean isValid = true;
        logger.debug("Whites turn, waiting for move.");
        Move m = game.getController().getMove(Player.WHITE);
        if (m == null) {
            return false;
        }
        logger.debug("Whites move received, attempting move.");
        game.getController().setMove(null);
        try {
            game.getBoard().movePiece(m.from(), m.to());
            logger.debug("Whites move is valid.");
        } catch (JChessException e) {
            ExceptionUtils.log(e);
            if (e instanceof JChessSelfCheckException) {
                //Placed ourselves into check.
                game.setState(new WhiteCheckState(game));
            } else {//not in check but still an invalid move
                game.setState(new WhitesTurnState(game));
            }
            isValid = false;
        }
        return isValid;
    }

}

