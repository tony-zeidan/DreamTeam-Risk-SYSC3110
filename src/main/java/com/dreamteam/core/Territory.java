package com.dreamteam.core;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class Territory represents the individual Territories found within the map.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 * @version 1.01
 * @since 1.00
 */
public class Territory implements Jsonable {

    /**
     * The name of the territory.
     */
    private String name;
    /**
     * The units that occupy this territory.
     */
    private int units;
    /**
     * The current Player that owns the Territory
     */
    private Player owner;
    /**
     * The Neighbouring Territories of the Territory
     */
    private Set<Territory> neighbours;

    /**
     * Constructor for the territory object that contains a name and current player.
     *
     * @param name The name of the territory
     */
    public Territory(String name) {
        this.name = name;
        units = 0;
        neighbours = new HashSet<>();
    }

    /**
     * Get the name of the territory.
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
    public void removeUnits(int units) {
        this.units -= units;
    }

    /**
     * Retrieves the owner for this territory.
     *
     * @return The owner
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this territory.
     *
     * @param owner The owner
     */
    public void setOwner(Player owner) {
        if (this.owner != null) {
            Player prevOwner = this.owner;
            prevOwner.removeTerritory(this);
        }
        this.owner = owner;
        owner.addTerritory(this);
    }

    /**
     * Adds a neighbouring territory to this one.
     *
     * @param territory The territory to add as a neighbour
     */
    public void addNeighbour(Territory territory) {
        if (!(neighbours.contains(territory))) {
            neighbours.add(territory);
            territory.addNeighbour(this);
        }
    }

    /**
     * Retrieves a collection of this territories neighbours.
     *
     * @return The neighbours of this territory
     */
    public Set<Territory> getNeighbours() {
        return neighbours;
    }

    /**
     * Retrieves a string representation of the territory.
     *
     * @return A string representation of the territory
     */
    @Override
    public String toString() {
        return String.format("Territory of %s: \n\tunits: %s", name, units);
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
        json.put("units", units+"");
        return json.toJson();
    }
    public JsonArray toJsonBuildMap() {
        JsonArray neighJson = new JsonArray();
        List<String> neighbourNames = new ArrayList<>();
        for (Territory t : neighbours) {
            neighbourNames.add(t.getName());
        }
        neighJson.addAll(neighbourNames);
        return neighJson;
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