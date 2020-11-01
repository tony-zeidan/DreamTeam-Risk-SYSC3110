package main.view;

import main.core.Player;

import java.awt.*;
import java.util.EventObject;

public class RiskEvent extends EventObject {
    private Point point1;
    private int units1;
    private Player owner1;
    private Point point2;
    private int units2;
    private Player owner2;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public RiskEvent(Object source, Point point1, int units1, Player owner1,Point point2, int units2, Player owner2) {
        super(source);
        this.point1 = point1;
        this.units1 = units1;
        this.owner1 = owner1;
        this.point2 = point2;
        this.units2 = units2;
        this.owner2 = owner2;
    }
    public Point getPoint1() {
        return point1;
    }

    public int getUnits1() {
        return units1;
    }

    public Player getOwner1() {
        return owner1;
    }

    public Point getPoint2() {
        return point2;
    }

    public int getUnits2() {
        return units2;
    }

    public Player getOwner2() {
        return owner2;
    }
}
