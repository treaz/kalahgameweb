package com.horiaconstantin.kalah.game;

import java.io.Serializable;
import java.util.*;

class KalahBoard implements Serializable {

    /**
     * the index of the kalah/home pit
     */
    static final int KALAH_PIT_INDEX = 6;
    static final int LAST_PIT_INDEX = 5;
    private HashMap<Player, int[]> playerPitsMap = new HashMap<>();

    KalahBoard() {
        playerPitsMap.put(Player.P1, new int[]{6, 6, 6, 6, 6, 6, 0});
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
    }

    KalahBoard(Map<Player, int[]> playerPitsMap) {
        this.playerPitsMap = new HashMap<>(playerPitsMap);
    }

    /**
     * @return the number of stones present in the player's kalah/home pit
     */
    int getStoneCountInKalah(Player player) {
        return playerPitsMap.get(player)[KALAH_PIT_INDEX];
    }

    int[] getPitStonesCountImmutable(Player player) {
        return Arrays.copyOf(playerPitsMap.get(player), playerPitsMap.get(player).length);
    }

    int countStonesInPit(Player player, int pitIndex) {
        return playerPitsMap.get(player)[pitIndex];
    }

    int getTotalStonesInPits(Player player) {
        int[] playerPits = playerPitsMap.get(player);
        int totalStones = 0;
        for (int i = 0; i < KALAH_PIT_INDEX; i++) {
            totalStones += playerPits[i];
        }
        return totalStones;
    }

    void addStonesToKalah(Player player, int numberOfStonesToAdd) {
        int[] playerPits = playerPitsMap.get(player);
        playerPits[KALAH_PIT_INDEX] += numberOfStonesToAdd;
    }

    void emptyPlayerPits(Player player) {
        int[] playerPits = playerPitsMap.get(player);
        for (int i = 0; i < KALAH_PIT_INDEX; i++) {
            playerPits[i] = 0;
        }
    }

    void emptyPlayerPit(Player player, int pitIndex) {
        int[] playerPits = playerPitsMap.get(player);
        playerPits[pitIndex] = 0;
    }

    void putOneStoneInPit(Player player, int pitIndex) {
        int[] playerPits = playerPitsMap.get(player);
        playerPits[pitIndex]++;
    }

    Set<Player> getPlayers() {
        return playerPitsMap.keySet();
    }
}
