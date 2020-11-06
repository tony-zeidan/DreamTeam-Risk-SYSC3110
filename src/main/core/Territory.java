package main.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Class main.core.Territory represents the individual Territories found within the map.
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

    private Player owner;

    private Set<Territory> neighbours;

    /** Constructor for the territory object that contains a name and current player.
     *
     * @param name The name of the territory
     */
    public Territory(String name){
        this.name = name;
        units = 0;
        neighbours = new HashSet<>();
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


    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        owner.addTerritory(this);
    }


    /**
     * Retrieves a string representation of the territory.
     *
     * @return A string representation of the territory
     */
    @Override
    public String toString() {
        return String.format("The main.core.Territory of %s: \n\tunits: %s",name,units);
    }

    public void addNeighbour(Territory territory)
    {
        if(!(neighbours.contains(territory)))
        {
            neighbours.add(territory);
            territory.addNeighbour(this);
        }
    }
    public Set<Territory> getNeighbours()
    {
        return neighbours;
    }
}
