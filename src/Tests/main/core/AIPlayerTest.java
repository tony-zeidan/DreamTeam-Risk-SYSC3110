package main.core;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AIPlayerTest {
    Player guy;
    AIPlayer robo;
    Territory t1;
    Territory t2;
    Territory t3;
    Territory t4;
    Territory t5;
    Territory t6;
    @Before
    public void setup()
    {
        guy = new Player("guy", RiskColour.RED);
        robo = new AIPlayer("Robo",RiskColour.GRAY);

        t1 = new Territory("t1");
        t2 = new Territory("t2");
        t3 = new Territory("t3");
        t4 = new Territory("t4");
        t5 = new Territory("t5");
        t6 = new Territory("t6");
        t1.addNeighbour(t2);
        t1.addNeighbour(t3);
        t2.addNeighbour(t6);
        t3.addNeighbour(t4);
        t3.addNeighbour(t5);
        t4.addNeighbour(t5);
        t5.addNeighbour(t2);
        t1.setOwner(robo);
        t2.setOwner(robo);
        t3.setOwner(robo);
        t4.setOwner(robo);
        t5.setOwner(guy);
        t6.setOwner(guy);
        t1.setUnits(2);
        t2.setUnits(1);
        t3.setUnits(3);
        t4.setUnits(2);
        t5.setUnits(1);
        t6.setUnits(3);
    }
    @Test
    public void testPlaceUnits()
    {
        this.setup();
        assertEquals(8,t1.getUnits()+t2.getUnits()+t3.getUnits()+t4.getUnits());
        //TODO: Fix parameters in the call to pass the model
        //robo.placeUnits(5);
        assertEquals(3,t6.getUnits());
        assertEquals(1,t5.getUnits());
        assertEquals(13,t1.getUnits()+t2.getUnits()+t3.getUnits()+t4.getUnits());

    }
    @Test
    public void testTerritoryMovingUnitsAway()
    {
        this.setup();
        assertEquals(t1,robo.territoryMovingUnitsAway());
    }
    @Test
    public void testTerritoryMovingUnitsTo()
    {
        this.setup();
        assertEquals(t2,robo.territoryMovingUnitsTo(t1));
        Territory t7 = new Territory("t7");
        Territory t8 = new Territory("t8");
        t7.addNeighbour(t4);
        t8.addNeighbour(t6);
        t7.setOwner(guy);
        t8.setOwner(robo);
        t7.setUnits(5);
        t8.setUnits(1);
        assertEquals(t4,robo.territoryMovingUnitsTo(t1));
    }
    @Test
    public void testMoveTroops()
    {
        this.setup();
        robo.moveTroops();
        assertEquals(1,t1.getUnits());
        assertEquals(2,t2.getUnits());
        Territory t7 = new Territory("t7");
        Territory t8 = new Territory("t8");

        this.setup();
        t7.addNeighbour(t4);
        t8.addNeighbour(t6);
        t7.setOwner(guy);
        t8.setOwner(robo);
        t7.setUnits(5);
        t8.setUnits(1);
        robo.moveTroops();
        assertEquals(1,t1.getUnits());
        assertEquals(3,t4.getUnits());
    }
}