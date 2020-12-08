package com.dreamteam.core;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * A class to hold the amount of the continent bonus and if a player holds that, to allow bonus troops placement
 *
 * @author Anthony Dooley
 * @author Eathan Chase
 * @author Kyler Verge
 * @author Tony Zeidan
 */
public class Continent implements Jsonable {
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
     *
     * @param name        String name of the continent
     * @param bonusAmount int amount of the bonus troops for holding continent
     */
    public Continent(String name, int bonusAmount) {
        this.name = name;
        this.bonusAmount = bonusAmount;
        territories = new HashSet<>();
    }

    /**
     * Getter for continent Name
     *
     * @return String name of the continent
     */
    public String getContinentName() {
        return name;
    }

    /**
     * Getter for the amount bonus troops of the continent
     *
     * @return Bonus amount of the continent
     */
    public int getBonusRulerAmount() {
        return bonusAmount;
    }

    /**
     * adds the territory that the continent should contain.
     *
     * @param territory the territory that the continent should contain
     */
    public void addContinentTerritory(Territory territory) {
        territories.add(territory);
    }

    /**
     * getter for the current owner of the territory
     *
     * @return The Player that owns the continent
     */
    public Player getRuler() {
        return ruler;
    }

    /**
     * checks to see if a Player now owns the continent, by owning all its
     * territories, and makes the Player equal to its ruler if it does own the continent.
     */
    public void updateRuler() {
        Iterator<Territory> it = territories.iterator();
        Player firstOwner = it.next().getOwner();
        while (it.hasNext()) {
            Player nextOwner = it.next().getOwner();
            if (firstOwner != nextOwner) {
                ruler = null;
                return;
            }
        }
        ruler = firstOwner;
    }

    /**
     * Serialize to a JSON formatted string.
     *
     * @return a string, formatted in JSON, that represents the Jsonable.
     */
    @Override
    public String toJson() {
        JsonObject json = new JsonObject();
        json.put("name", name);
        json.put("value", bonusAmount+"");
        ArrayList<String> terrNames = new ArrayList<>();
        for (Territory terr: territories)
        {
            terrNames.add(terr.getName());
        }
        JsonArray territoriesJson = new JsonArray();
        territoriesJson.addAll(terrNames);
        json.put("territories",territoriesJson);
        return json.toJson();
    }

    /**
     * Serialize to a JSON formatted stream.
     *
     * @param writable where the resulting JSON text should be sent.
     * @throws IOException when the writable encounters an I/O error.
     */
    @Override
    public void toJson(Writer writable) throws IOException {
        try {
            writable.write(this.toJson());
        } catch (Exception ignored) {
        }
    }
}
