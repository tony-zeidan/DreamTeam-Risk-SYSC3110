import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /** Constructor for the territory object that contains a name and current player.
     *
     * @param name The name of the territory
     */
    public Territory(String name){
        this.name = name;
        units = 0;
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


    /**
     * Retrieves a string representation of the territory.
     *
     * @return A string representation of the territory
     */
    @Override
    public String toString() {
        return String.format("The Territory of %s: \n\tunits: %s",name,units);
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
}
