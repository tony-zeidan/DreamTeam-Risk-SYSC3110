package Tests.main.core;

import main.core.AIPlayer;
import main.core.Player;
import main.core.RiskColour;
import main.core.Territory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit Testing class, tests some of the AIs functions
 * and ensures AI is working
 *
 * @author Ethan Chase
 * @author Kyler Verge
 * @author Anthony Dooley
 * @author Tony Zeidan
 */

public class AIPlayerTest {
    /**
     * The normal player to test against.
     */
    Player guy;
    /**
     * The AI player to test with.
     */
    AIPlayer robo;
    /**
     * Territory to test with.
     */
    Territory t1;
    /**
     * Territory to test with.
     */
    Territory t2;
    /**
     * Territory to test with.
     */
    Territory t3;
    /**
     * Territory to test with.
     */
    Territory t4;
    /**
     * Territory to test with.
     */
    Territory t5;
    /**
     * Territory to test with.
     */
    Territory t6;

    /**
     * Sets up the object for testing.
     */
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

    /**
     * Test the AI's algorithm for moving units away from a specific territory.
     */
    @Test
    public void testTerritoryMovingUnitsAway()
    {
        this.setup();
        assertEquals(t1,robo.territoryMovingUnitsAway());
    }

    /**
     * Test the AI's algorithm for moving units toward from a specific territory.
     */
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

    /**
     * Test the Ai's ability to move units effectively (according to utility).
     */
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