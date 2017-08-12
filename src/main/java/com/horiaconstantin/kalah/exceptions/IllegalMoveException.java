package com.horiaconstantin.kalah.exceptions;

/**
 * Exception indicating that a player attempted to
 * - move when it has not his/her turn
 * - move from the opponent's pit or his own kalah
 * - move after the game has ended
 * - pick up stones from an empty pit
 */
public class IllegalMoveException extends RuntimeException {
    public IllegalMoveException(String message) {
        super(message);
    }
}
