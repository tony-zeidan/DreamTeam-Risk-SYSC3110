package main.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameSingletonTest {

    private GameSingleton gsm;

    ArrayList<Player> players;

    @Before
    public void setUp(){
        players = new ArrayList<>();
        players.add(new Player("Ethan", RiskColour.RED));
        players.add(new Player("Anthony", RiskColour.BLUE));
        players.add(new Player("Tony", RiskColour.YELLOW));
        gsm = GameSingleton.getGameInstance(players);
    }

    @Test
    public void getNumActivePlayer() {
        assertEquals(3, gsm.getNumActivePlayer());
    }
}