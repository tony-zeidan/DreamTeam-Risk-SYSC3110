package main.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameSingletonTest {

    private GameSingleton gsm;

    List<Player> players;

    @Before
    public void setUp(){
        players = new ArrayList<>();
        players.add(new Player("Ethan", RiskColour.RED));
        players.add(new Player("Anthony", RiskColour.BLUE));
        players.add(new Player("Tony", RiskColour.YELLOW));
        gsm = GameSingleton.getGameInstance(players);
    }

    /**
     * Test getNumActivePlayer():
     *
     * Check how many players not yet eliminated in the game.
     */
    @Test
    public void getNumActivePlayerStatus() {
        gsm.setUpGame();
        assertEquals(3, gsm.getNumActivePlayer());
    }

    /**
     * Test getMaxBattleDie()
     */
    @Test
    public void getMaxBattleDieStatus(){
        assertEquals(2, gsm.getMaxBattleDie(3,true));
    }

    /**
     * Test getColour() in Player class
     */
    @Test
    public void getColoursTest(){
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        gsm.setUpGame();
        assertEquals(RiskColour.RED, ethan.getColour());
        assertEquals(RiskColour.BLUE, anthony.getColour());
        assertEquals(RiskColour.YELLOW, tony.getColour());
    }

    /**
     * Test getName() in Player class
     */
    @Test
    public void getNameTest(){
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        gsm.setUpGame();
        assertEquals("Ethan", ethan.getName());
        assertEquals("Anthony", anthony.getName());
        assertEquals("Tony", tony.getName());
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
    }

    /**
     * Test fortifyPosition() in GameSingleton class
     */
    @Test
    public void testFortifyPosition(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);

        Territory t1 = new Territory("Earth");
        t1.setUnits(4);
        assertEquals(4, t1.getUnits());
        t1.setOwner(ethan);
        assertEquals(ethan, t1.getOwner());

        Territory t2 = new Territory("Pluto");
        t2.setUnits(5);
        assertEquals(5, t2.getUnits());
        t2.setOwner(anthony);
        assertEquals(anthony, t2.getOwner());

        gsm.fortifyPosition(t1,t2,2);

        assertEquals("Earth",t1.getName());
        assertEquals("Pluto",t2.getName());
        assertEquals(ethan,t1.getOwner());
        assertEquals(ethan,t2.getOwner());
        assertEquals(2,t1.getUnits());
        assertEquals(7,t2.getUnits());
    }

    /**
     * Test rollDice() in GameSingleton class
     */
    @Test
    public void testRollDie(){
        int[] theRolls = gsm.rollDice(3);
        assertEquals(3, theRolls.length);
        for(int i=0;i<3;i++){
            assertTrue(theRolls[i] > 0);
            assertTrue(theRolls[i] < 7);
        }
    }

    @Test
    public void testUpdateNumActivePlayer(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);

        Territory t1 = new Territory("Earth");
        t1.setUnits(4);
        assertEquals(4, t1.getUnits());
        t1.setOwner(ethan);
        assertEquals(ethan, t1.getOwner());

        Territory t2 = new Territory("Pluto");
        for(int i=0;i<3;i++){
            players.get(i).setActive(true);
        }

        gsm.setNumActivePlayer(players.size());
        players.get(1).setActive(false);
        assertEquals(false,players.get(1).isActive());
        gsm.updateNumActivePlayers();
        assertEquals(2, gsm.getNumActivePlayer());

        //players.get(0).setActive(false);
        assertEquals(false,players.get(0).isActive());
        gsm.updateNumActivePlayers();
        assertEquals(1, gsm.getNumActivePlayer());

        gsm.endGame();
        assertEquals(1, gsm.getNumActivePlayer());
    }
}