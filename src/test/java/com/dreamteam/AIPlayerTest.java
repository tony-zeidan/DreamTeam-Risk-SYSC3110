package com.dreamteam;

import com.dreamteam.core.*;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    public void setup() {
        guy = new Player("guy", RiskColour.RED);
        robo = new AIPlayer("Robo", RiskColour.GRAY);

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

    /**
     * Tests the AI's capability to place units strategically.
     */
    @Test
    public void testPlaceUnits() {

        List<Player> players = new ArrayList<>();
        players.add(robo);
        players.add(guy);

        //for this specific test we need a model
        //assume that the test map loads in properly
        GameSingleton gsm = GameSingleton.getGameInstance();
        gsm.setPlayers(players);
        InputStream initialStream = null;
        try {
            initialStream = getClass().getClassLoader().getResourceAsStream("test1.world");
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            File targetFile = new File("src/test/resources/targetFile.tmp");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            gsm.newGame(new ZipFile(targetFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        Algorithm:
        Get the amount of units on all of the AI's territories before and after
        placing 100 units.
        Then check if both results are unequal.
         */
        Map<String,Integer> terrUnits = new HashMap<>();
        for (Territory t : robo.getOwnedTerritories()) {
            terrUnits.put(t.getName(),t.getUnits());
        }

        robo.placeUnits(100,gsm);

        Map<String,Integer> terrUnitsAfter = new HashMap<>();
        for (Territory t : robo.getOwnedTerritories()) {
            terrUnitsAfter.put(t.getName(),t.getUnits());
        }

        assertNotEquals(terrUnits,terrUnitsAfter);
    }

    /**
     * Test the AI's algorithm for moving units away from a specific territory.
     */
    @Test
    public void testTerritoryMovingUnitsAway() {
        this.setup();
        assertEquals(t1, robo.territoryMovingUnitsAway());
    }

    /**
     * Test the AI's algorithm for moving units toward from a specific territory.
     */
    @Test
    public void testTerritoryMovingUnitsTo() {
        this.setup();
        assertEquals(t2, robo.territoryMovingUnitsTo(t1));
        Territory t7 = new Territory("t7");
        Territory t8 = new Territory("t8");
        t7.addNeighbour(t4);
        t8.addNeighbour(t6);
        t7.setOwner(guy);
        t8.setOwner(robo);
        t7.setUnits(5);
        t8.setUnits(1);
        assertEquals(t4, robo.territoryMovingUnitsTo(t1));
    }

    /**
     * Test the Ai's ability to move units effectively (according to utility).
     */
    @Test
    public void testMoveTroops() {
        this.setup();
        robo.moveTroops();
        assertEquals(1, t1.getUnits());
        assertEquals(2, t2.getUnits());
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
        assertEquals(1, t1.getUnits());
        assertEquals(3, t4.getUnits());
    }
}