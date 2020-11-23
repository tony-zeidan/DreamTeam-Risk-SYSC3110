package main.core;
import java.util.*;
/**
 * A class to hold the amount of the continent bonus and if a player holds that, to allow bonus troops placement
 *
 * @author Anthony
 * @author Eathan Chase
 * @author Kyler Verge
 * @author Tony Zeidan
 */
public class Continent {
    /**
     * name of the continent
     */
    private String name;
    /**
     * player that holds the continent, null if no player holds all territories in continent
     */
    private Player ruler;
    /**
     * the amount of bonus troops for holding continent
     */
    private int bonusAmount;
    /**
     * set of all the Territories that the continent holds.
     */
    private Set<Territory> territories;

    /**
     * constructor for the continent class
     * @param name String name of the continent
     * @param bonusAmount int amount of the bonus troops for holding continent
     */
    public Continent(String name, int bonusAmount){
        this.name = name;
        this.bonusAmount = bonusAmount;
        territories = new HashSet<>();
    }

    /**
     * Getter for continent Name
     * @return String name of the continent
     */
    public String getContinentName(){
        return name;
    }

    /**
     * Getter for the amount bonus troops of the continent
     * @return
     */
    public int getBonusRulerAmount(){
        return bonusAmount;
    }

    /**
     * adds the territory that the continent should contain.
     * @param territory the territory that the continent should contain
     */
    public void addContinentTerritory(Territory territory){
        territories.add(territory);
    }

    /**
     * getter for the current owner of the territory
     * @return Player that owns the continent
     */
    public Player getRuler(){
        return ruler;
    }

    /**
     * checks to see if a Player now owns the continent, by owning all its
     * territories, and makes the Player equal to its ruler if it does own the continent.
     */
    public void updateRuler(){
        Iterator<Territory> it = territories.iterator();
        Player firstOwner = it.next().getOwner();
        while(it.hasNext()) {
            Player nextOwner = it.next().getOwner();
            if(firstOwner != nextOwner){
                ruler = null;
                return;
            }
        }
        ruler = firstOwner;
    }

}
