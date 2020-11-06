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
        gsm.setUpGame();
    }

    /**
     * Test getNumActivePlayer():
     *
     * Check how many players not yet eliminated in the game.
     */
    @Test
    public void getNumActivePlayerStatus() {
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
     *
     * NOTE: In order to test getColour from the Player class, you
     * have to run getColoursTest() only. Running the entire
     * GameSingletonTest.java won't work.
     */
    @Test
    public void getColoursTest(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        assertEquals(RiskColour.RED, ethan.getColour());
        assertEquals(RiskColour.BLUE, anthony.getColour());
        assertEquals(RiskColour.YELLOW, tony.getColour());
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
    }

    /**
     * Test getName() in Player class
     *
     * NOTE: In order to test getName from the Player class, you
     * have to run getNameTest() only. Running the entire
     * GameSingletonTest.java won't work.
     */
    @Test
    public void getNameTest(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        assertEquals("Ethan", ethan.getName());
        assertEquals("Anthony", anthony.getName());
        assertEquals("Tony", tony.getName());
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
    }
}