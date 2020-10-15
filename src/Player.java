import java.awt.*;

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
     * The name of the player that this object represents
     */
    private String name;

    /**
     * The colour of the units that the player owns.
     */
    private Color colour;


    /** Constructor for instances of Player class with name and colour.
     *
     * @param name The name of the player
     * @param colour The colour of the units that the player owns
     */
    public Player(String name, Color colour){
        this.name = name;
        this.colour = colour;
    }

    /**
     * Constructor for instances of Player class.
     *
     * @param name The name of the player that this object represents
     */
    public Player(String name) {
        this.name = name;
        colour = null;
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

    /** Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public Color getColour() {
        return colour;
    }

    /** Set the colour of the units that the player will own.
     *
     * @param colour The colour of the units that the player will own
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

    /** The player has the ability to attack other territories owned
     *  by other players
     *
     * @param territoryAtk The territory the current player is on and attacking from
     * @param territoryDef The territory the player wishes to attack
     * @param atkUnits The number of units the attackers territory has
     * @param defUnits The number of units the defenders territory has
     */
    public void attack(Territory territoryAtk, Territory territoryDef){
       //Test Cases for not being able to attack
        //The attacker owns the defending territory
        if(territoryAtk.getOwner() == territoryDef.getOwner()){
            //Cancel Attack
        }
        //The attackers territory only has one unit
        else if (territoryAtk.getUnits() == 1) {
            //Cancel Attack
        }
        //The defending territory isn't neighbouring the attacking
        else if (){
            //Cancel Attack
        }
        //Everything seems good to go, time to fight!
        else{
            //A lot of tedious checks on number of units and dices...

        }
    }
}
