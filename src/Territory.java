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
     * Adds units from the territory.
     *
     * @param units The amount of units to add
     */
    public void addUnits(int units) {
        this.units += units;
    }

    /**
     * Removes units from the territory.
     *
     * @param units The amount of units to remove
     */
    public void removeUnits(int units){
        this.units -= units;
    }
    public ArrayList<Territory> getNieghbours()
    {
        return new ArrayList<Territory>(neighbours.values());
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

    public boolean isNeighbour(Territory territory) {
        return neighbours.containsValue(territory);
    }

    public boolean ownsAllNeighbours() {
        for (Territory t : neighbours.values()) {
            if (owner!=t.getOwner()) return false;
        }
        return true;
    }

    /**
     * Retrieves a string representation of the territory.
     *
     * @return A string representation of the territory
     */
    @Override
    public String toString() {
        return String.format("The Territory of %s:\n\towner: %s \n\tunits: %s",name,owner,units);
    }

    /**
     * Prints the string representation of this territory without indentation.
     */
    public void print() {
        print("");
    }

    /**
     * Prints the string representation of this territory, allowing indentation.
     */
    public void print(String tabs){
        System.out.println(tabs+toString());
    }

    public void printNeighbours() {
        for (Territory t : neighbours.values()) {
            System.out.print(String.format("%s (%s), ",t.getName(),t.getOwner().getColour()));
        }
    }
}
