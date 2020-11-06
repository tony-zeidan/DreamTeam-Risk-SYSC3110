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

    @Test
    public void getColoursStatus(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        assertEquals(RiskColour.RED, ethan.getColour());
        assertEquals(RiskColour.BLUE, anthony.getColour());
        assertEquals(RiskColour.YELLOW, tony.getColour());
        //Test passes if order of players is not shuffled in setUpGame()
    }
}