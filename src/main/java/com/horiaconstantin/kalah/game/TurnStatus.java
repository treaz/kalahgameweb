package com.horiaconstantin.kalah.game;

import static com.horiaconstantin.kalah.game.KalahBoard.*;

/**
 * Holds the state of the current player move
 */
class TurnStatus {
    private int indexOfPitInWhichNextStoneWillBePut;

    private int stonesInHand;

    private Player currentPlayer;

    private final Player playingPlayer;

    TurnStatus(int indexOfPitInWhichNextStoneWillBePut, int stonesInHand, Player currentPlayer) {
        this.indexOfPitInWhichNextStoneWillBePut = indexOfPitInWhichNextStoneWillBePut;
        this.stonesInHand = stonesInHand;
        this.currentPlayer = currentPlayer;
        this.playingPlayer = currentPlayer;
        moveToNextPit();
    }

    /**
     * @return the index of the pit in which the next stone will be placed
     */
    int getCurrentPitIndex() {
        return indexOfPitInWhichNextStoneWillBePut;
    }

    /**
     * @return the player on which side stones are placed
     */
    Player getCurrentPlayer() {
        return currentPlayer;
    }

    void decrementStonesInHand() {
        stonesInHand--;
    }

    boolean hasStonesAvailable() {
        return stonesInHand > 0;
    }

    /**
     * call this method when you want to change the side on which the stones are put. I.e. player puts stones
     * in his pits, reaches his kalah and still has stones left in his hand, so he needs to put them in the other's
     * player pits
     */
    void changeDepositPitLaneIfNeeded() {
        if (getCurrentPitIndex() > KALAH_PIT_INDEX) {
            currentPlayer = getNextPlayer();
            indexOfPitInWhichNextStoneWillBePut = 0;
        }
    }

    Player getNextPlayer() {
        return currentPlayer.getNext();
    }

    void moveToNextPit() {
        indexOfPitInWhichNextStoneWillBePut++;
    }

    int getOpposingPitIndex() {
        return LAST_PIT_INDEX - getCurrentPitIndex();
    }

    boolean canPlaceStoneInCurrentPit() {
        return getCurrentPitIndex() != KALAH_PIT_INDEX || getCurrentPlayer().equals(playingPlayer);
    }

    boolean placedLastStoneInOwnPit() {
        return getCurrentPlayer().equals(playingPlayer) && !hasStonesAvailable() && getCurrentPitIndex() != KALAH_PIT_INDEX;
    }
}
