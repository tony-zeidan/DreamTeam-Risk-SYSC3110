import java.util.HashMap;
import java.util.Map;

/**
 * Class Territory represents the individual Territories found within the map.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
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
        Player oldOwner= this.owner;
        if(oldOwner != null)
        {
            oldOwner.removeTerritory(this);
        }
        this.owner = owner;
        this.owner.addTerritory(this);
    }

    /**
     * Determines if a territory is a neighbour to this one.
     *
     * @param territory The territory to check
     * @return Whether the given territory is a neighbour
     */
    public boolean isNeighbour(Territory territory) {
        return neighbours.containsValue(territory);
    }

    /**
     * Determines if another territory is allied (same owner) as this one.
     *
     * @param territory The territory to check
     * @return Whether the same person owns both territories
     */
    public boolean isAlly(Territory territory) {
        return (owner== territory.getOwner());
    }

    /**
     * Determines if this territory and all ones around it are conquered by
     * the same player.
     *
     * @return Whether the player has conquered all neighbouring territories
     */
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
     *
     * @param tabs A string representing the tabulation (\t)
     */
    public void print(String tabs){
        System.out.println(tabs+toString());
    }

    /**
     * Prints the neighbours of this territory according to the ally boolean.
     * If true, the method will print only allied neighbours.
     * If false, the method will print only enemy neighbours.
     * {@link Territory#isAlly(Territory)}
     *
     * @param ally Whether to print allies (true) or enemies (false)
     */
    public void printValidNeighbours(boolean ally) {
        System.out.println(String.format("%s's Neighbours (ally=%s):",name,ally));
        for (Territory t : neighbours.values()) {
            if (isAlly(t)==ally) {
                System.out.print(String.format("%s (%s), ",t.getName(),t.getOwner().getColour()));
            }
        }
    }
}
