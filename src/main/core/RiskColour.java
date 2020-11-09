package main.core;

import java.awt.*;

public enum RiskColour {
    RED(new Color(125, 1, 22),"RED"),
    GRAY(Color.GRAY,"GRAY"),
    BLUE(Color.BLUE,"BLUE"),
    YELLOW(new Color(191, 176, 12),"YELLOW"),
    BLACK(new Color(15,0,0),"BLACK"),
    GREEN(new Color(10, 142, 1),"GREEN");

    private Color colour;
    private String name;

    private RiskColour(Color colour, String name) {
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
