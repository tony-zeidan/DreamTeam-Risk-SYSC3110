package main.core;

import java.awt.*;

/**
 * This enumeration represents the different phases of the RISK game.
 * The game generally flows in a cyclical motion, providing a great opportunity
 * to implement this flow with methods.
 */
public enum GamePhase {
    /**
     * This phase represents the very beginning of the game, only used once.
     */
    START_GAME("Start of Game Phase", Color.BLACK,
            "The very start of the game, nothing has begun yet."),
    /**
     * This phase of the game represents the duration at the beginning of a turn
     * in which a player will be able to place bonus troops on their territories.
     */
    BONUS_TROUPE("Bonus Troupe Phase", Color.GREEN,
            "Place any bonus units you have by clicking on territories."),
    /**
     * This phase of the game represents the duration after the Bonus Troupe phase
     * in which a player will be able to attack other territories.
     */
    ATTACK("Attack Phase", Color.RED,
            "Attack any territories you wish by clicking on territories."),
    /**
     * This phase of the game represents the duration after the Attack phase
     * (after clicking end turn) in which a player will be able to make one final
     * fortification before their turn officially ends.
     */
    MOVE_UNITS("Move Units Phase", Color.BLUE,
            "Move units before the end of your turn!");

    /**
     * Stores the string representation of the phase
     */
    private String name;
    /**
     * Stores the color representation of the phase.
     */
    private Color colour;
    /**
     * Stores the description of the phase.
     */
    private String description;

    /**
     * Constructor for instances of this enumeration.
     * Construct the new game phase with the given string and color representations.
     *
     * @param name        The name of the game phase
     * @param colour      The color representation of the phase
     * @param description A description of what occurs during the phase
     */
    GamePhase(String name, Color colour, String description) {
        this.name = name;
        this.colour = colour;
        this.description = description;
    }

    /**
     * Retrieves the colour representation of the phase.
     *
     * @return The phase colour
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Retrieves a string representation of the phase containing the name
     * and description.
     *
     * @return A string representation of the phase
     */
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
