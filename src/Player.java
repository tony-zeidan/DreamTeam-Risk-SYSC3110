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
     * The colour of the units that the player owns.
     */
    private String colour;

    /** Constructor for the player object that contains a colour.
     *
     * @param colour The colour of units that the player owns
     */
    public Player(String colour){
        this.colour = colour;
    }

    /** Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public String getColour() { return colour; }

    /** Set the colour of the units that the player will own.
     *
     * @param colour The colour of the units that the player will own
     */
    public void setColour(String colour) { this.colour = colour; }

}
