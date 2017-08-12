package com.horiaconstantin.kalah.game;

import com.horiaconstantin.kalah.exceptions.IllegalMoveException;
import com.horiaconstantin.kalah.game.KalahBoard;
import com.horiaconstantin.kalah.game.KalahGame;
import com.horiaconstantin.kalah.game.Player;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.horiaconstantin.kalah.game.KalahGame.*;

public class KalahGameTest {

    @Test
    public void testInitialGameSetup() {
        KalahGame game = new KalahGame();

        assertThat(game.getKalahBoard().getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{6, 6, 6, 6, 6, 6, 0});
        assertThat(game.getKalahBoard().getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{6, 6, 6, 6, 6, 6, 0});

        assertThat(game.getKalahBoard().getStoneCountInKalah(Player.P1)).isEqualTo(0);
        assertThat(game.getKalahBoard().getStoneCountInKalah(Player.P2)).isEqualTo(0);
    }

    @Test
    public void firstPlayerStartMoveAllowed() {
        KalahGame game = new KalahGame();

        KalahBoard board = game.move(Player.P1, 5);

        assertThat(board.getStoneCountInKalah(Player.P1)).isEqualTo(1);
        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{6, 6, 6, 6, 6, 0, 1});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{7, 7, 7, 7, 7, 6, 0});
        assertThatThrownBy(() -> game.move(Player.P1, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(NOT_YOUR_TURN);
    }

    @Test
    public void playerCanMoveOnlyFromLegalPits() {
        KalahGame game = new KalahGame();

        assertThatThrownBy(() -> game.move(Player.P1, -1)).isInstanceOf(IllegalMoveException.class);
        assertThatThrownBy(() -> game.move(Player.P1, 6)).isInstanceOf(IllegalMoveException.class);
    }

    @Test
    public void secondPlayerStartMoveIllegal() {
        KalahGame game = new KalahGame();
        assertThatThrownBy(() -> game.move(Player.P2, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(NOT_YOUR_TURN);
    }

    @Test
    public void playerCanMoveTwice() {
        KalahGame game = new KalahGame();
        KalahBoard board = game.move(Player.P1, 0);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 7, 7, 7, 7, 7, 1});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{6, 6, 6, 6, 6, 6, 0});

        board = game.move(Player.P1, 1);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 0, 8, 8, 8, 8, 2});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{7, 7, 6, 6, 6, 6, 0});

        assertThatThrownBy(() -> game.move(Player.P1, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(NOT_YOUR_TURN);
    }

    @Test
    public void playerCanMoveOnlyFromPitWithAtLeastOneStone() {
        KalahGame game = new KalahGame();
        game.move(Player.P1, 0);

        assertThatThrownBy(() -> game.move(Player.P1, 0)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(NO_STONES_IN_PIT);
    }

    @Test
    public void erroneousPlayer() {
        KalahGame game = new KalahGame();

        assertThatThrownBy(() -> game.move(null, 0)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(NOT_YOUR_TURN);
        assertThatThrownBy(() -> game.move(Player.NONE, 0)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(NOT_YOUR_TURN);
    }

    @Test
    public void noStonesArePutInOpponentsKalah() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{6, 6, 6, 6, 6, 10, 0});
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        board = game.move(Player.P1, 5);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{7, 7, 7, 6, 6, 0, 1});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{7, 7, 7, 7, 7, 7, 0});
    }

    @Test
    public void playerMoves24StonesFromLastPit() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{6, 6, 6, 6, 6, 24, 0});
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        board = game.move(Player.P1, 5);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{8, 8, 8, 8, 7, 1, 2});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{8, 8, 8, 8, 8, 8, 0});
    }

    @Test
    public void lastStoneLandsInOwnEmptyPit() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
        playerPitsMap.put(Player.P1, new int[]{2, 6, 0, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        board = game.move(Player.P1, 0);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 7, 0, 6, 6, 6, 7});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{6, 6, 6, 0, 6, 6, 0});
    }

    @Test
    public void lastStoneLandsInOwnEmptyPitForPlayer2() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{10, 1, 2, 12, 11, 10, 2});
        playerPitsMap.put(Player.P2, new int[]{9, 9, 0, 1, 1, 0, 4});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        game.move(Player.P1, 2);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{10, 1, 0, 13, 12, 10, 2});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{9, 9, 0, 1, 1, 0, 4});

        game.move(Player.P2, 4);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 1, 0, 13, 12, 10, 2});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{9, 9, 0, 1, 0, 0, 15});
    }

    @Test
    public void lastStoneLandsInOtherPlayerEmptyPit() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{15, 6, 0, 6, 6, 6, 0});
        playerPitsMap.put(Player.P2, new int[]{0, 6, 6, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        board = game.move(Player.P1, 1);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{15, 0, 1, 7, 7, 7, 1});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{1, 6, 6, 6, 6, 6, 0});
    }

    @Test
    public void player1MoveLeadsToPlayer2Winning() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{0, 0, 0, 0, 0, 1, 0});
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        game.move(Player.P1, 5);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 0, 0, 0, 0, 0, 1});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{0, 0, 0, 0, 0, 0, 36});
        assertThat(game.getWinner()).isEqualTo(Player.P2);
        assertThatThrownBy(() -> game.move(Player.P1, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
        assertThatThrownBy(() -> game.move(Player.P2, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
    }

    @Test
    public void player1MoveLeadsToPlayer1Winning() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{0, 0, 0, 0, 0, 1, 70});
        playerPitsMap.put(Player.P2, new int[]{6, 6, 6, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        game.move(Player.P1, 5);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 0, 0, 0, 0, 0, 71});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{0, 0, 0, 0, 0, 0, 36});
        assertThat(game.getWinner()).isEqualTo(Player.P1);
        assertThatThrownBy(() -> game.move(Player.P1, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
        assertThatThrownBy(() -> game.move(Player.P2, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
    }

    @Test
    public void player2MoveLeadsToPlayer2Winning() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{0, 0, 0, 0, 1, 0, 0});
        playerPitsMap.put(Player.P2, new int[]{1, 2, 6, 6, 6, 6, 0});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        game.move(Player.P1, 4);

        game.move(Player.P2, 1);
        assertThat(game.getWinner()).isEqualTo(Player.P2);
        assertThatThrownBy(() -> game.move(Player.P2, 1)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
        assertThatThrownBy(() -> game.move(Player.P1, 1)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
    }

    @Test
    public void player1MoveLeadsToTie() {
        Map<Player, int[]> playerPitsMap = new HashMap<>();
        playerPitsMap.put(Player.P1, new int[]{0, 0, 0, 0, 0, 1, 0});
        playerPitsMap.put(Player.P2, new int[]{0, 0, 0, 0, 0, 0, 1});
        KalahBoard board = new KalahBoard(playerPitsMap);
        KalahGame game = new KalahGame(board);

        game.move(Player.P1, 5);

        assertThat(board.getPitStonesCountImmutable(Player.P1)).isEqualTo(new int[]{0, 0, 0, 0, 0, 0, 1});
        assertThat(board.getPitStonesCountImmutable(Player.P2)).isEqualTo(new int[]{0, 0, 0, 0, 0, 0, 1});
        assertThat(game.getWinner()).isEqualTo(Player.NONE);
        assertThatThrownBy(() -> game.move(Player.P1, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
        assertThatThrownBy(() -> game.move(Player.P2, 5)).isInstanceOf(IllegalMoveException.class).hasMessageContaining(GAME_OVER);
    }

    @Test
    public void getWinnerBeforeEnd() {
        KalahGame game = new KalahGame();

        assertThatThrownBy(game::getWinner).isInstanceOf(RuntimeException.class).hasMessage(GAME_STILL_RUNNING);
    }
}