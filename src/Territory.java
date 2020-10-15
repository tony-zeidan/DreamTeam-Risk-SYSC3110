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
    private ArrayList<Unit> units;
    /**
     * The player that currently owns units in this territory.
     */
    private Player currentPlayer;

    /** Constructor for the territory object that contains a name and current player.
     *
     * @param name The name of the territory
     * @param currentPlayer The player that currently occupies the territory
     */
    public Territory(String name, Player currentPlayer){
        this.name = name;
        this.units = new ArrayList<>();
        this.currentPlayer = currentPlayer;
    }

    /** Get the name of the territory.
     *
     * @return The name of the territory
     */
    public String getName() { return name; }

    /** Add a unit to the territory.
     *
     * @param aUnit Unit to be added to the territory
     */
    public void addUnit(Unit aUnit){ units.add(aUnit); }

    /** Remove a unit from the territory.
     *
     * @param aUnit Unit to be removed from the territory
     */
    public void removeUnit(Unit aUnit){ units.remove(aUnit); }

    /** Get the player that occupies the territory.
     *
     * @return The player that currently occupies the territory.
     */
    public Player getCurrentPlayer() { return currentPlayer; }

    /** Set new player as occupant of the territory.
     *
     * @param currentPlayer The new player that now occupies the territory
     */
    public void setCurrentPlayer(Player currentPlayer) { this.currentPlayer = currentPlayer; }

}
