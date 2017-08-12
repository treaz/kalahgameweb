package com.horiaconstantin.kalah.pojo;

import java.io.Serializable;
import java.util.*;

public class KalahBoard implements Serializable {

    /**
     * the index of the kalah/home pit
     */
    public static final int KALAH_INDEX = 6;
    public static final int LAST_PIT_INDEX = 5;
    private HashMap<Player, int[]> playerPitsMap = new HashMap<>();

    public KalahBoard(){
        playerPitsMap.put(Player.P1, new int[]{6, 6, 6, 6, 6, 6, 0});
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
    }

    public KalahBoard(Map<Player, int[]> playerPitsMap){
        this.playerPitsMap = new HashMap<>(playerPitsMap);
    }

    public int getStoneCountInKalah(Player player) {
        return playerPitsMap.get(player)[KALAH_INDEX];
    }

    public int[] getPitStonesCountImmutable(Player player) {
        return Arrays.copyOf(playerPitsMap.get(player), playerPitsMap.get(player).length);
    }

    public int countStonesInPit(Player player, int pitIndex){
        return playerPitsMap.get(player)[pitIndex];
    }

    public int getTotalStonesInPits(Player player) {
        int[] playerPits = playerPitsMap.get(player);
        int totalStones = 0;
        for (int i = 0; i< KALAH_INDEX; i++){
            totalStones += playerPits[i];
        }
        return totalStones;
    }

    public void addStonesToKalah(Player player, int numberOfStonesToAdd) {
        int[] playerPits = playerPitsMap.get(player);
        playerPits[KALAH_INDEX] += numberOfStonesToAdd;
    }

    public void emptyPlayerPits(Player player) {
        int[] playerPits = playerPitsMap.get(player);
        for (int i = 0; i< KALAH_INDEX; i++){
            playerPits[i] = 0;
        }
    }

    public void emptyPlayerPit(Player player, int pitIndex) {
        int[] playerPits = playerPitsMap.get(player);
        playerPits[pitIndex] = 0;
    }

    public void putOneStoneInPit(Player player, int pitIndex) {
        int[] playerPits = playerPitsMap.get(player);
        playerPits[pitIndex]++;
    }

    public Set<Player> getPlayers() {
        return playerPitsMap.keySet();
    }
}
