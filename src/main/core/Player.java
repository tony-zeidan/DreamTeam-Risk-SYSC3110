package main.core;

import java.util.*;
import java.util.List;

/**
 * Class main.core.Player represents the user within the {@link GameSingleton}.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @version 1.00
 * @since 1.00
 */
public class Player {

    /**
     * The name of the player that this object represents.
     */
    private String name;

    /**
     * The colour of the units that the player owns.
     */
    private RiskColour colour;

    /**
     * List of territories that the player owns.
     */
    private Set<Territory> owned;

    /**
     * Contains whether the player is still active in the game.
     */
    private boolean active;

    /**
     * Constructor for instances of Player class with name.
     *
     * @param name The name of the player
     */
    public Player(String name) {
        this.name = name;
        this.colour = null;
        this.active = true;
        owned = new HashSet<>();
    }


    /**
     * Constructor for instances of main.core.Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public Player(String name, RiskColour colour) {
        this.name = name;
        this.colour = colour;
        this.active = true;
        owned = new HashSet<>();
    }

    /**
     * Retrieves the name of the player.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public RiskColour getColour() {
        return colour;
    }

    /**
     * Sets the colour of the units that the player owns.
     */
    public void setColour(RiskColour colour) {
        this.colour = colour;
    }

    /**
     * Add a territory to this players owned territories.
     *
     * @param territory The new territory this player owns
     */
    public void addTerritory(Territory territory) {
        owned.add(territory);
    }

    /**
     * Remove a territory from this players owned territories.
     *
     * @param territory The territory this player lost
     */
    public void removeTerritory(Territory territory) {
        owned.remove(territory);
    }

    /**
     * Retrieves a list of all territories owned by the player.
     *
     * @return The list of owned territories
     */
    public Set<Territory> getOwnedTerritories() {
        return owned;
    }

    /**
     * Determine if the player is out of the game or not.
     *
     * @return True if player is still active in the game, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether the player has been eliminated from the game or not.
     *
     * @param active The player's active status in the game
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves a string representation of the player.
     *
     * @return A string representation of the player
     */
    @Override
    public String toString() {
        return String.format("%s of %s", name, colour);
    }

    /**
     * Determines whether the player owns the given main.core.Territory.
     *
     * @param territory The territory to check
     * @return Whether the player owns the territory (t/f)
     */
    public boolean ownsTerritory(Territory territory) {
        return owned.contains(territory);
    }

}
