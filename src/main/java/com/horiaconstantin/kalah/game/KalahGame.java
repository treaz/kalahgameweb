package com.horiaconstantin.kalah.game;

import com.horiaconstantin.kalah.exceptions.GameStateException;
import com.horiaconstantin.kalah.exceptions.IllegalMoveException;

import java.io.Serializable;

import static com.horiaconstantin.kalah.game.KalahBoard.KALAH_PIT_INDEX;

public class KalahGame implements Serializable {

    static final String GAME_STILL_RUNNING = "there is no winner yet, the game is ongoing";
    static final String GAME_OVER = "Game over, start a new game to be able to move again.";
    static final String NOT_YOUR_TURN = "It's not your turn ";
    static final String INVALID_PIT_NUMBER = "Cannot move from pit number ";
    static final String NO_STONES_IN_PIT = "There are no stones in pit number ";

    private KalahBoard board;
    private Player playerToMove = Player.P1;
    private Player winner = null;

    /**
     * Initializes a new Kalah game. Player 1 always starts
     */
    public KalahGame() {
        board = new KalahBoard();
    }

    KalahGame(KalahBoard board) {
        this.board = board;
    }

    /**
     * @param player   the player that is executing his turn
     * @param pitIndex the index (0 based) of the pit from which the playing player will be moving the stones
     * @return the new state of the KalahBoard
     */
    public KalahBoard move(Player player, int pitIndex) {
        checkLegalMove(player, pitIndex);

        TurnStatus ps = startPlayerTurn(player, pitIndex);

        while (ps.hasStonesAvailable()) {
            ps.changeDepositPitLaneIfNeeded();
            if (ps.canPlaceStoneInCurrentPit()) {
                board.putOneStoneInPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex());
                ps.decrementStonesInHand();
            }
            applyRuleOfPlacingLastStoneInOwnEmptyPit(ps);
            ps.moveToNextPit();
        }
        if (ps.getCurrentPitIndex() != KALAH_PIT_INDEX + 1) {
            playerToMove = player.getNext();
        }

        computeWinner();
        return board;
    }

    /**
     *
     * @return a key-value JSON containing the player names as keys and an array of their pits as values
     */
    public String getGameStatusAsString() {
        return getBoard().getBoardStatusAsString();
    }

    KalahBoard getBoard() {
        return board;
    }

    private void applyRuleOfPlacingLastStoneInOwnEmptyPit(TurnStatus ps) {
        if (lastStonePlacedInEmptyPit(ps)) {
            int stoneCountInOpposingPit = board.countStonesInPit(ps.getNextPlayer(), ps.getOpposingPitIndex());
            board.addStonesToKalah(ps.getCurrentPlayer(),
                    board.countStonesInPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex()) + stoneCountInOpposingPit);

            board.emptyPlayerPit(ps.getNextPlayer(), ps.getOpposingPitIndex());
            board.emptyPlayerPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex());
        }
    }

    private boolean lastStonePlacedInEmptyPit(TurnStatus ps) {
        return ps.placedLastStoneInOwnPit() && board.countStonesInPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex()) == 1;
    }

    /**
     * Verify if the move is allowed according to the game rules
     */
    private void checkLegalMove(Player player, int pitIndex) {
        if (winner != null) {
            throw new IllegalMoveException(winner + " WON! " + GAME_OVER);
        }
        if (pitIndex < 0 || pitIndex > 5) {
            throw new IllegalMoveException(INVALID_PIT_NUMBER + (pitIndex + 1));
        }
        if (!playerToMove.equals(player)) {
            throw new IllegalMoveException(NOT_YOUR_TURN + player);
        }
        if (board.countStonesInPit(player, pitIndex) == 0) {
            throw new IllegalMoveException(NO_STONES_IN_PIT + (pitIndex + 1));
        }
    }

    private TurnStatus startPlayerTurn(Player player, int pitIndex) {
        int stonesPickedUpFromPit = board.countStonesInPit(player, pitIndex);
        board.emptyPlayerPit(player, pitIndex);
        return new TurnStatus(pitIndex, stonesPickedUpFromPit, player);
    }

    /**
     * Called at the end of each turn to decide if someone won
     */
    private void computeWinner() {
        Player otherPlayer = playerToMove.getNext();
        int totalStonesInPlayerToMovePits = board.getTotalStonesInPits(playerToMove);
        int totalStonesInOtherPlayerPits = board.getTotalStonesInPits(otherPlayer);
        if (totalStonesInPlayerToMovePits == 0) {
            board.addStonesToKalah(otherPlayer, totalStonesInOtherPlayerPits);
            board.emptyPlayerPits(otherPlayer);
            if (board.getStoneCountInKalahPitFor(playerToMove) > board.getStoneCountInKalahPitFor(otherPlayer)) {
                winner = playerToMove;
            } else if (board.getStoneCountInKalahPitFor(playerToMove) < board.getStoneCountInKalahPitFor(otherPlayer)) {
                winner = otherPlayer;
            } else {
                winner = Player.NONE;
            }
        }
    }

    Player getWinner() {
        if (winner == null) {
            throw new GameStateException(GAME_STILL_RUNNING);
        }
        return winner;
    }
}
