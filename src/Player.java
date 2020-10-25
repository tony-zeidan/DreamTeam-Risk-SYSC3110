import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Player represents the user within the {@link Game}.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
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
     *
     * Possible colours are Red, Green, Blue, Yellow, Black, Grey
     */
    private String colour;

    /**
     * List of territories that the player owns.
     */
    private List<Territory> owned;

    /**
     * True if person still contains units, false otherwise
     */
    private boolean active;

    /**
     * Constructor for instances of Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public Player(String name, String colour) {
        this.name = name;
        this.colour = colour;
        this.active = true;
        owned = new ArrayList<>();
    }

    /**
     * Retrieves the name of the player
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this player instance.
     *
     * @param name The name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public String getColour() {
        return colour;
    }

    /**
     * Set the colour of the units that the player will own.
     *
     * @param colour The colour of the units that the player will own
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Add a territory to this players owned territories.
     *
     * @param territory The new territory this player owns
     */
    public void addTerritory(Territory territory) {
        owned.add(territory);
    }

    /**
     * Remove a territory from this players owned territories.
     *
     * @param territory The territory this player lost
     */
    public void removeTerritory(Territory territory) {
        owned.remove(territory);
    }
    public ArrayList<Territory> getOwnedTerritories()
    {
        return (ArrayList)owned;
    }
    /**
     * Determine if the player is out of the game or not.
     *
     * @return True if player is still active in the game, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether the player has been eliminated from the game or not.
     *
     * @param active The player's active status in the game
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves a string representation of the player.
     *
     * @return A string representation of the player
     */
    @Override
    public String toString() {
        return String.format("%s of %s clan",name,colour);
    }

    /**
     * Prints the string representation of this player without indentation.
     */
    public void print() {
        print("");
    }

    /**
     * Prints the string representation of this player, allowing indentation.
     */
    public void print(String tabs){
        System.out.println(tabs+toString());
    }

    public void printOwned() {
        System.out.println(String.format("%s owns the following territories:",name));
        for (Territory t : owned) {
            System.out.println(t.getName());
        }
    }
}
