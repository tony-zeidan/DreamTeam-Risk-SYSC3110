import java.util.ArrayList;

/**
 * Class Territory represents the individual Territories found within the map.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @since 1.00
 * @version 1.01
 */
public class Territory {

    /**
     * The name of the territory.
     */
    private String name;
    /**
     * The units that occupy this territory.
     */
    private int units;
    /**
     * The player that currently owns units in this territory.
     */
    private Player owner;

    /** Constructor for the territory object that contains a name and current player.
     *
     * @param name The name of the territory
     */
    public Territory(String name){
        this.name = name;
        this.units = 0;
        this.owner = null;
    }

    /** Get the name of the territory.
     *
     * @return The name of the territory
     */
    public String getName() { return name; }

    /** Get the player that occupies the territory.
     *
     * @return The player that currently occupies the territory.
     */
    public Player getOwner() {
        return owner;
    }

    /** Set new player as occupant of the territory.
     *
     * @param owner The new player that now occupies the territory
     */
    public void setCurrentPlayer(Player owner) {
        this.owner = owner;
    }
}
