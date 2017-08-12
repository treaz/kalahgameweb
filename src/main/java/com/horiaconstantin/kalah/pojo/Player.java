package com.horiaconstantin.kalah.pojo;

public enum Player {
    P1,
    P2,
    NONE;

    public Player getNext() {
        Player nextPlayer = values()[(ordinal()+1) % values().length];
        if (nextPlayer.equals(NONE)){
            nextPlayer = nextPlayer.getNext();
        }
        return nextPlayer;
    }
}
