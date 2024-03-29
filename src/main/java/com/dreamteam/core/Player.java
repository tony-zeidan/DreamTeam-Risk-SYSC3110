package com.dreamteam.core;


import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * Class Player represents the user within the {@link GameSingleton}.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @version 1.00
 * @since 1.00
 */
public class Player implements Jsonable {

    /**
     * The name of the player that this object represents.
     */
    private String name;
    /**
     * The colour of the units that the player owns.
     */
    private RiskColour colour;
    /**
     * List of territories that the player owns.
     */
    private Set<Territory> owned;
    /**
     * Contains whether the player is still active in the game.
     */
    private boolean active;
    /**
     * Contains the user input for the amount of dice to use
     */
    private int diceRoll;

    private ImageIcon avatar;

    /**
     * Constructor for instances of Player class with name.
     *
     * @param name The name of the player
     */
    public Player(String name) {
        this.name = name;
        this.colour = null;
        this.active = true;
        owned = new HashSet<>();
    }


    /**
     * Constructor for instances of main.com.dreamteam.core.Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public Player(String name, RiskColour colour) {
        this.name = name;
        this.colour = colour;
        this.active = true;
        owned = new HashSet<>();
        setAvatar();
    }

    /**
     * Retrieves the name of the player.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public RiskColour getColour() {
        return colour;
    }

    /**
     * Sets the colour of the units that the player owns.
     *
     * @param colour The colour of this player
     */
    public void setColour(RiskColour colour) {
        this.colour = colour;
        setAvatar();
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

    /**
     * Retrieves a list of all territories owned by the player.
     *
     * @return The list of owned territories
     */
    public Set<Territory> getOwnedTerritories() {
        return owned;
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
        return String.format("%s of %s", name, colour);
    }

    /**
     * Determines whether the player owns the given main.com.dreamteam.core.Territory.
     *
     * @param territory The territory to check
     * @return Whether the player owns the territory (t/f)
     */
    public boolean ownsTerritory(Territory territory) {
        return owned.contains(territory);
    }

    /**
     * setter for amount of dice the user wants to use
     *
     * @param numOfDice int number of dice to use
     */
    public void setDiceRoll(int numOfDice) {
        diceRoll = numOfDice;
    }

    /**
     * Getter fof the amount of dice the user wants to use.
     *
     * @return int the amount of dice to use.
     */
    public int getDiceRoll() {
        return diceRoll;
    }

    private void setAvatar() {
        try {
            String path = "player_icons/" + colour.getName().toLowerCase()+".png";
            System.out.println(path);
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            if (stream!=null) {
                Image unscaled = ImageIO.read(stream);
                avatar = new ImageIcon(unscaled.getScaledInstance(30,30,Image.SCALE_DEFAULT));
                System.out.println("Avatar loaded.");
            } else {
                avatar = null;
            }
        } catch (IOException e) {
            avatar = null;
        }
    }

    /**
     * Retrieve the image of the player's avatar.
     *
     * @return An image icon of the player's avatar.
     */
    public ImageIcon getAvatar() {
        return avatar;
    }

    /**
     * Serialize to a JSON formatted string.
     *
     * @return a string, formatted in JSON, that represents the Jsonable.
     */
    @Override
    public String toJson() {
        JsonObject json = new JsonObject();
        if (this instanceof AIPlayer)
            json.put("isAI", "true");
        else
            json.put("isAI", "false");
        json.put("name", name);
        json.put("colour",colour.getName());
        json.put("owned", owned);
        json.put("active",active);
        json.put("selectedDie",diceRoll+"");
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
