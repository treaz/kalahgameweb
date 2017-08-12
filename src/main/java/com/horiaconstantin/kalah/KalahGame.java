package com.horiaconstantin.kalah;

import com.horiaconstantin.kalah.exceptions.GameStateException;
import com.horiaconstantin.kalah.exceptions.IllegalMoveException;
import com.horiaconstantin.kalah.pojo.KalahBoard;
import com.horiaconstantin.kalah.pojo.Player;

import java.io.Serializable;

import static com.horiaconstantin.kalah.pojo.KalahBoard.KALAH_INDEX;

public class KalahGame implements Serializable {

    public static final String GAME_STILL_RUNNING = "there is no winner yet, the game is ongoing";
    public static final String GAME_OVER = "Game over, start a new game to be able to move again.";
    public static final String NOT_YOUR_TURN = "It's not your turn ";
    public static final String INVALID_PIT_NUMBER = "Cannot move from pit number ";
    public static final String NO_STONES_IN_PIT = "There are no stones in pit number ";
    private KalahBoard board;
    private Player playerToMove = Player.P1;
    private Player winner = null;

    public KalahGame() {
        board = new KalahBoard();
    }

    public KalahGame(KalahBoard board) {
        this.board = board;
    }

    public KalahBoard getKalahBoard() {
        return board;
    }

    /**
     * @param player   the player that is executing his turn
     * @param pitIndex the index (0 based) of the pit from which the playing player will be moving the stones
     * @return
     */
    public KalahBoard move(Player player, int pitIndex) {
        checkLegalMove(player, pitIndex);

        PlayerTurnStatus ps = getPlayerTurnStatus(player, pitIndex);

        while (ps.hasStonesInHand()) {
            ps.changeDepositPitLaneIfNeeded();
            if (ps.canPlaceStoneInCurrentPit()) {
                board.putOneStoneInPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex());
                ps.decrementStonesInHand();
            }
            applyRuleOfPlacingLastStoneInOwnEmptyPit(ps);
            ps.moveToNextPit();
        }
        if (ps.getCurrentPitIndex() != KALAH_INDEX + 1) {
            playerToMove = player.getNext();
        }

        computeWinner();
        return board;
    }

    private void applyRuleOfPlacingLastStoneInOwnEmptyPit(PlayerTurnStatus ps) {
        if (lastStonePlacedInEmptyPit(ps)) {
            int stoneCountInOpposingPit = board.countStonesInPit(ps.getNextPlayer(), ps.getOpposingPitIndex());
            board.addStonesToKalah(ps.getCurrentPlayer(), board.countStonesInPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex()) + stoneCountInOpposingPit);

            board.emptyPlayerPit(ps.getNextPlayer(), ps.getOpposingPitIndex());
            board.emptyPlayerPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex());
        }
    }

    private boolean lastStonePlacedInEmptyPit(PlayerTurnStatus ps) {
        return ps.placedLastStoneInOwnPit() && board.countStonesInPit(ps.getCurrentPlayer(), ps.getCurrentPitIndex()) == 1;
    }

    private void checkLegalMove(Player player, int pitIndex) {
        if (winner != null) {
            throw new IllegalMoveException(winner+" WON! "+GAME_OVER);
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

    private PlayerTurnStatus getPlayerTurnStatus(Player player, int pitIndex) {
        int stonesPickedUpFromPit = board.countStonesInPit(player, pitIndex);
        board.emptyPlayerPit(player, pitIndex);
        return new PlayerTurnStatus(pitIndex, stonesPickedUpFromPit, player);
    }

    private void computeWinner() {
        Player otherPlayer = playerToMove.getNext();
        int totalStonesInPlayerToMovePits = board.getTotalStonesInPits(playerToMove);
        int totalStonesInOtherPlayerPits = board.getTotalStonesInPits(otherPlayer);
        if (totalStonesInPlayerToMovePits == 0) {
            board.addStonesToKalah(otherPlayer, totalStonesInOtherPlayerPits);
            board.emptyPlayerPits(otherPlayer);
            if (board.getStoneCountInKalah(playerToMove) > board.getStoneCountInKalah(otherPlayer)) {
                winner = playerToMove;
            } else if (board.getStoneCountInKalah(playerToMove) < board.getStoneCountInKalah(otherPlayer)) {
                winner = otherPlayer;
            } else {
                winner = Player.NONE;
            }
        }
    }

    public Player getPlayerToMove() {
        return playerToMove;
    }

    public void setPlayerToMove(Player playerToMove) {
        this.playerToMove = playerToMove;
    }

    public Player getWinner() {
        if (winner == null) {
            throw new GameStateException(GAME_STILL_RUNNING);
        }
        return winner;
    }
}
