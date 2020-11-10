package main.core;

import java.awt.*;

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
    RED(new Color(125, 1, 22),"RED"),
    GRAY(Color.GRAY,"GRAY"),
    BLUE(Color.BLUE,"BLUE"),
    YELLOW(new Color(191, 176, 12),"YELLOW"),
    BLACK(new Color(15,0,0),"BLACK"),
    GREEN(new Color(10, 142, 1),"GREEN");

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
     * @param colour
     * @param name color name
     */
    RiskColour(Color colour, String name) {
        this.colour=colour;
        this.name=name;
    }

    /**
     *getter for colour
     * @return Colour
     */
    public Color getValue() { return colour; }

    /**
     * getter for colour name
     * @return String
     */
    public String getName() { return name; }
}
