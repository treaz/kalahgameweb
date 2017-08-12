package com.horiaconstantin.kalah.game;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.*;

class KalahBoard implements Serializable {

    /**
     * the index of the kalah/home pit
     */
    static final int KALAH_PIT_INDEX = 6;

    static final int LAST_PIT_INDEX = KALAH_PIT_INDEX - 1;
    /**
     * the number of stones that are place at the beginning of the game in each pit
     */
    private static final int START_STONES = 6;
    private HashMap<Player, int[]> playerPitsMap = new HashMap<>();
    private Gson gson = new Gson();

    KalahBoard() {
        playerPitsMap.put(Player.P1, getStartLinePits());
        playerPitsMap.put(Player.P2, getStartLinePits());
    }

    KalahBoard(Map<Player, int[]> playerPitsMap) {
        this.playerPitsMap = new HashMap<>(playerPitsMap);
    }

    private int[] getStartLinePits(){
        return new int[]{START_STONES, START_STONES, START_STONES, START_STONES, START_STONES, START_STONES, 0};
    }

    /**
     * @return the number of stones present in the player's kalah/home pit
     */
    int getStoneCountInKalahPitFor(Player player) {
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

    private Set<Player> getPlayers() {
        return playerPitsMap.keySet();
    }

    String getBoardStatusAsString() {
        Map<String, int[]> board = new HashMap<>();
        for (Player player : getPlayers()) {
            board.put(player.name(), getPitStonesCountImmutable(player));
        }
        return gson.toJson(board);
    }
}
