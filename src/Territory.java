import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * The map implementation of the neighbouring territories.
     */
    private Map<String,Territory> neighbours;

    /** Constructor for the territory object that contains a name and current player.
     *
     * @param name The name of the territory
     */
    public Territory(String name){
        this.name = name;
        units = 0;
        owner = null;
        neighbours = new HashMap<>();
    }

    /**
     * Searches the neighbouring territories for one with the specified name.
     *
     * @param name The name to search for
     * @return The territory if found otherwise null
     */
    public Territory searchNeighbours(String name) {
        return neighbours.getOrDefault(name, null);
    }

    /**
     * Add a territory to the list of territories neighbouring this territory.
     *
     * @param territory The new neighbouring territory
     */
    public void addNeighbour(Territory territory) {
        neighbours.put(territory.getName(),territory);
    }

    /** Get the name of the territory.
     *
     * @return The name of the territory
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the amount of units on this region.
     *
     * @return The number of territory units
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the number of units in this territory.
     *
     * @param units The units set on this territory
     */
    public void setUnits(int units) {
        this.units = units;
    }

    /**
     * Adds or removes units from the territory.
     *
     * @param units The amount of units to add
     */
    public void addUnits(int units) {
        this.units += units;
    }

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
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Retrieves a string representation of the territory.
     *
     * @return A string representation of the territory
     */
    @Override
    public String toString() {
        return name+" owned by " + owner.getName()+ " with " + units+" troops";
    }

    /**
     * print method for printing individual territories from toString().
     */
    public void print(){
        System.out.println(toString());
    }
}
