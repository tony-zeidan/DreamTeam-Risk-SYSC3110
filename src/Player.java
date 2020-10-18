import java.awt.*;

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
     */
    private String colour;

    /** Constructor for the player object that contains a colour.
     *
     * @param colour The colour of units that the player owns
     */
    public Player(String name, String colour){
        this.name = name;
        this.colour = colour;
    }

    public Player(String name) {
        this.name = name;
        colour = null;
    }

    public String getName() {
        return name;
    }

    /** Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public String getColour() {
        return colour;
    }

    /** Set the colour of the units that the player will own.
     *
     * @param colour The colour of the units that the player will own
     */
    public void setColour(String colour) {
        this.colour = colour;
    }
}
