import java.awt.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Class Player represents the user within the {@link Game}.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @since 1.00
 * @version 1.01
 */
public class Player {

    /**
     * The name of the player that this object represents
     */
    private String name;

    /**
     * The colour of the units that the player owns.
     *
     * Possible colours are Red, Green, Blue, Yellow, Black, Grey
     */
    private String colour;


    /**
     * Constructor for instances of Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public Player(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    /**
     * Constructor for instances of Player class.
     *
     * @param name The name of the player that this object represents
     */
    public Player(String name) {
        this.name = name;
        colour = null;
    }

    /**
     * Retrieves the name of the player
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this player instance.
     *
     * @param name The name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public String getColour() {
        return colour;
    }

    /**
     * Set the colour of the units that the player will own.
     *
     * @param colour The colour of the units that the player will own
     */
    public void setColour(String colour) {
        this.colour = colour;
    }
}
