package com.dreamteam.core;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;

/**
 * This class represents the custom colours made for our version of RISK
 * A colour has a string name that corresponds to its colour
 *
 * @author Tony Zeidan
 * @author Kyler Verge
 * @author Anthony Dooley
 * @author Ethan Chase
 */
public enum RiskColour {
    /**
     * The risk colour red.
     */
    RED(new Color(125, 1, 22), "RED"),
    /**
     * The risk colour gray.
     */
    GRAY(Color.GRAY, "GRAY"),
    /**
     * The risk colour blue.
     */
    BLUE(Color.BLUE, "BLUE"),
    /**
     * The risk colour yellow.
     */
    YELLOW(new Color(191, 176, 12), "YELLOW"),
    /**
     * The risk colour black.
     */
    BLACK(new Color(15, 0, 0), "BLACK"),
    /**
     * The risk colour green.
     */
    GREEN(new Color(10, 142, 1), "GREEN");

    /**
     * the colour value
     */
    private Color colour;
    /**
     * the colour name
     */
    private String name;

    /**
     * Assigns to a color and its colour name
     *
     * @param colour The awt colour
     * @param name   The colour's name
     */
    RiskColour(Color colour, String name) {
        this.colour = colour;
        this.name = name;
    }

    /**
     * getter for colour
     *
     * @return Colour
     */
    public Color getValue() {
        return colour;
    }

    /**
     * getter for colour name
     *
     * @return String
     */
    public String getName() {
        return name;
    }
}
