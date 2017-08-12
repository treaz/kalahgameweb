package com.horiaconstantin.kalah.game;

import com.google.gson.Gson;
import com.horiaconstantin.kalah.exceptions.GameStateException;
import com.horiaconstantin.kalah.exceptions.IllegalMoveException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    //TODO add comments to all public methods
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
        if (ps.getCurrentPitIndex() != KALAH_PIT_INDEX + 1) {
            playerToMove = player.getNext();
        }

        computeWinner();
        return board;
    }

    public String getGameStatusAsString() {
        Map<String, int[]> serializedBoard = new HashMap<>();
        for (Player player : getKalahBoard().getPlayers()) {
            serializedBoard.put(player.name(), getKalahBoard().getPitStonesCountImmutable(player));
        }
        Gson gson = new Gson();
        return gson.toJson(serializedBoard);
    }

    KalahBoard getKalahBoard() {
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

    Player getWinner() {
        if (winner == null) {
            throw new GameStateException(GAME_STILL_RUNNING);
        }
        return winner;
    }
}
