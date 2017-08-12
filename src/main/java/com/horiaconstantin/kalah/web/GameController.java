package com.horiaconstantin.kalah.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.horiaconstantin.kalah.KalahGame;
import com.horiaconstantin.kalah.pojo.Player;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GameController {

    public static final String SEESION_ATTRIBUTE_KALAH_GAME = "kalahGame";

    @RequestMapping("/initGame")
    public String initGame(HttpSession httpSession) throws JsonProcessingException {
        KalahGame kalahGame = new KalahGame();
        httpSession.setAttribute(SEESION_ATTRIBUTE_KALAH_GAME, kalahGame);
        return serializeBoardToString(kalahGame);
    }

    @RequestMapping("/move")
    public String move(
            @RequestParam(value = "player") String player,
            @RequestParam(value = "pitIndex") int pitIndex,
            HttpSession httpSession) throws JsonProcessingException {
        KalahGame kalahGame = (KalahGame) httpSession.getAttribute(SEESION_ATTRIBUTE_KALAH_GAME);
        kalahGame.move(Player.valueOf(player), pitIndex);
        return serializeBoardToString(kalahGame);
    }

    @RequestMapping("/whoseTurnIsIt")
    public String getPlayerToMove(HttpSession httpSession) throws JsonProcessingException {
        KalahGame kalahGame = (KalahGame) httpSession.getAttribute(SEESION_ATTRIBUTE_KALAH_GAME);
        return kalahGame.getPlayerToMove().toString();
    }

    private String serializeBoardToString(KalahGame kalahGame) throws JsonProcessingException {
        Map<String, int[]> serializedBoard = new HashMap<>();
        for (Player player : kalahGame.getKalahBoard().getPlayers()) {
            serializedBoard.put(player.name(), kalahGame.getKalahBoard().getPitStonesCountImmutable(player));
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(serializedBoard);
    }
}
