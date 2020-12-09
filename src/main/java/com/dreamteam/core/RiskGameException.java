package com.dreamteam.core;

/**
 * This class represents a specific exception that can be thrown in the game of RISK.
 * This exception is thrown only when an invalid map is loaded, but it could be used for more.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 */
public class RiskGameException extends RuntimeException {
    /**
     * Constructs a new exception.
     *
     * @param message The error message
     */
    public RiskGameException(String message) {
        super(message);
    }
}
