/**
 * Class Unit represents the main Units within the {@link Game}.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @since 1.00
 * @version 1.01
 */
public class Unit {

    /**
     * The colour of the unit.
     */
    private String colour;

    /** Constructor for the unit object that contains a specific colour.
     *
     * @param colour The colour of the unit
     */
    public Unit(String colour){
        this.colour = colour;
    }

    /** Get the colour of the unit.
     *
     * @return The colour of the unit
     */
    public String getColour() { return colour; }

    /** Set the colour of the unit.
     *
     * @param colour The colour of the unit
     */
    public void setColour(String colour) { this.colour = colour; }
}
