package com.horiaconstantin.kalah;

import com.horiaconstantin.kalah.pojo.Player;

import static com.horiaconstantin.kalah.pojo.KalahBoard.*;

/**
 * Holds the state of the current player move
 */
public class PlayerTurnStatus {
    private int indexOfPitInWhichNextStoneWillBePut;

    private int stonesInHand;

    private Player currentPlayer;

    private final Player playingPlayer;

    public PlayerTurnStatus(int indexOfPitInWhichNextStoneWillBePut, int stonesInHand, Player currentPlayer) {
        this.indexOfPitInWhichNextStoneWillBePut = indexOfPitInWhichNextStoneWillBePut;
        this.stonesInHand = stonesInHand;
        this.currentPlayer = currentPlayer;
        this.playingPlayer = currentPlayer;
        moveToNextPit();
    }

    /**
     *
     * @return the index of the pit in which the next stone will be placed
     */
    public int getCurrentPitIndex() {
        return indexOfPitInWhichNextStoneWillBePut;
    }

    /**
     *
     * @return the player on which side stones are placed
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void decrementStonesInHand() {
        stonesInHand--;
    }

    public boolean hasStonesInHand() {
        return stonesInHand > 0;
    }

    /**
     * call this method when you want to change the side on which the stones are put. I.e. player puts stones
     * in his pits, reaches his kalah and still has stones left in his hand, so he needs to put them in the other's
     * player pits
     */
    public void changeDepositPitLaneIfNeeded() {
        if (getCurrentPitIndex() > KALAH_INDEX) {
            currentPlayer = getNextPlayer();
            indexOfPitInWhichNextStoneWillBePut = 0;
        }
    }

    public Player getNextPlayer(){
        return currentPlayer.getNext();
    }

    public void moveToNextPit(){
        indexOfPitInWhichNextStoneWillBePut++;
    }

    public int getOpposingPitIndex(){
        return LAST_PIT_INDEX - getCurrentPitIndex();
    }

    public boolean canPlaceStoneInCurrentPit() {
        return getCurrentPitIndex() != KALAH_INDEX || getCurrentPlayer().equals(playingPlayer);
    }

    public boolean placedLastStoneInOwnPit() {
        return getCurrentPlayer().equals(playingPlayer) && !hasStonesInHand() && getCurrentPitIndex() != KALAH_INDEX;
    }
}
