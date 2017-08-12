package com.horiaconstantin.kalah.web;

import com.horiaconstantin.kalah.game.KalahGame;
import com.horiaconstantin.kalah.exceptions.IllegalMoveException;
import com.horiaconstantin.kalah.game.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class GameController {

    private static final String SESSION_ATTRIBUTE_KALAH_GAME = "kalahGame";

    @RequestMapping("/initGame")
    public String initGame(HttpSession httpSession) {
        KalahGame kalahGame = new KalahGame();
        httpSession.setAttribute(SESSION_ATTRIBUTE_KALAH_GAME, kalahGame);
        return kalahGame.getGameStatusAsString();
    }

    @RequestMapping("/move")
    public String move(
            @RequestParam Player player,
            @RequestParam int pitIndex,
            HttpSession httpSession) {
        KalahGame kalahGame = (KalahGame) httpSession.getAttribute(SESSION_ATTRIBUTE_KALAH_GAME);
        kalahGame.move(player, pitIndex);
        return kalahGame.getGameStatusAsString();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalMoveException.class)
    public String handleError(HttpServletRequest req, Exception ex) {
        return "{\"message\": \""+ex.getMessage()+"\"}";
    }
}
