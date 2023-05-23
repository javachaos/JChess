package com.github.javachaos.jchess.gamelogic.states.impl;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.exceptions.JChessSelfCheckException;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.states.core.AbstractGameState;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import com.github.javachaos.jchess.utils.ExceptionUtils;

public class BlacksTurnState extends AbstractGameState {

    protected BlacksTurnState(ChessGame game) {
        super(game);
    }

    @Override
    public void handle() {
        if (!(game.getPrevState() instanceof WhitesTurnState
                || game.getPrevState() instanceof BlacksTurnState) &&
                !(game.getPrevState() instanceof BlackCheckState)) {
            invalidState();
        }
        game.getBoard().setActivePlayer(Player.BLACK);
        if (waitForBlacksMove()) {//if move is valid
            if (game.isInCheck(Player.BLACK)) {
                game.setState(new BlackCheckState(game));
            } else {
                game.setState(new WhitesTurnState(game));
            }
        }
    }

    /**
     * In this method we query the controller every 100 milliseconds for a move for black
     * until we have received a move. Once a move is retrieved we attempt to update the model
     * with this move.
     *
     * @return true if blacks move is a valid move.
     */
    private boolean waitForBlacksMove() {
        boolean isValid = true;
        logger.debug("Blacks turn, waiting for move.");
        Move m = game.getController().getMove(Player.BLACK);
        if (m == null) {
            return false;
        }
        logger.debug("Blacks move received, attempting move.");
        try {
            game.getBoard().movePiece(m.from(), m.to());
            logger.debug("Blacks move is valid.");
        } catch (JChessException e) {
            ExceptionUtils.log(e);
            if (e instanceof JChessSelfCheckException) {
                //Placed ourselves into check.
                game.setState(new BlackCheckState(game));
            } else {//not in check but still an invalid move
                game.setState(new BlacksTurnState(game));
            }
            isValid = false;
        }
        return isValid;
    }

}