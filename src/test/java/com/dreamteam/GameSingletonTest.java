package com.dreamteam;

import com.dreamteam.core.*;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

/**
 * JUnit Testing class, tests some of the games functions
 * and ensures everything is working as expected
 *
 * @author Ethan Chase
 * @author Kyler Verge
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class GameSingletonTest {

    /**
     * Single instance of the game model
     */
    private GameSingleton gsm;

    /**
     * List of players in the game
     */
    private List<Player> players;

    /**
     * Custom world of the game
     */
    private WorldMap wmp;

    /**
     * Sets up list of players in a single Game of RISK in order
     * to test methods in the Game model class.
     */
    @Before
    public void setUp() {
        players = new ArrayList<>();
        players.add(new Player("Ethan", RiskColour.YELLOW));
        players.add(new Player("Anthony", RiskColour.RED));
        players.add(new Player("Kyler", RiskColour.BLUE));
        players.add(new Player("Tony", RiskColour.GREEN));
        gsm = GameSingleton.getGameInstance();
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
        wmp = gsm.getWorld();
    }

    @Test
    public void testLoadGame(){
        gsm.clean();
        players.add(new Player("Ethan", RiskColour.YELLOW));
        players.add(new Player("Anthony", RiskColour.RED));
        players.add(new Player("Kyler", RiskColour.BLUE));
        players.add(new Player("Tony", RiskColour.GREEN));

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
        wmp = gsm.getWorld();

        Territory tempTerr = wmp.getTerritory("Test1");
        assertEquals("Test1", tempTerr.getName());

        tempTerr = wmp.getTerritory("Test2");
        assertEquals("Test2", tempTerr.getName());

        tempTerr = wmp.getTerritory("Test3");
        assertEquals("Test3", tempTerr.getName());

        tempTerr = wmp.getTerritory("Test4");
        assertEquals("Test4", tempTerr.getName());

        tempTerr = wmp.getTerritory("Test5");
        assertEquals("Test5", tempTerr.getName());

        tempTerr = wmp.getTerritory("Test6");
        assertEquals(null, tempTerr);
    }

    @Test
    public void testSaveGame(){

    }

    /**
     * Test getNumActivePlayer():
     * <p>
     * Check how many players are not yet eliminated from the game
     * through getNumActivePlayer() in GameSingleton.java
     */
    @Test
    public void testGetNumActivePlayer() {
        assertEquals(4, gsm.getNumActivePlayer());
    }

    /**
     * Test getMaxBattleDie()
     * <p>
     * Calls getMaxBattleDie() in GameSingleton.java to check if it
     * returns the maximum amount of dice the attacker/defender can roll
     * based on the number of units used in the attack sequence.
     */
    @Test
    public void testGetMaxBattleDie() {
        //Attacking, 3 Units -> 2 Dice
        assertEquals(2, gsm.getMaxBattleDie(3, true));
        //Attacking 4 Units -> 3 Dice
        assertEquals(3, gsm.getMaxBattleDie(4, true));
        //Attacking 2 Units -> 1 Dice
        assertEquals(1, gsm.getMaxBattleDie(2, true));
        //Defending 2 Units -> 1 Dice
        assertEquals(1, gsm.getMaxBattleDie(2, false));
        //Defending 3 Units -> 2 Dice
        assertEquals(2, gsm.getMaxBattleDie(3, false));
    }

    /**
     * Test moveUnits() in GameSingleton class
     * <p>
     * Creates two territories with different names, owners and number of units.
     * These territories are passed as parameters to moveUnits() in
     * GameSingleton.java with the number of units to move. Checks
     * if the units are moved properly.
     * <p>
     * Also tests the movement of units between two territories that are
     * owned by the current player. This would be part of the movement phase of the
     * game that occurs just before the end of the current player's turn.
     */
    @Test
    public void testMoveUnits() {
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

        gsm.moveUnits(t1, t2, 2);

        assertEquals("Earth", t1.getName());
        assertEquals("Pluto", t2.getName());
        assertEquals(ethan, t1.getOwner());
        assertEquals(ethan, t2.getOwner());
        assertEquals(2, t1.getUnits());
        assertEquals(7, t2.getUnits());

        gsm.moveUnits(t2, t1, 2);

        assertEquals(t1.getOwner(), t2.getOwner());
        assertEquals(4, t1.getUnits());
        assertEquals(5, t2.getUnits());
    }

    /**
     * Test attack() in GameSingleton class
     * <p>
     * Makes sure that the units destroyed in the attack() are limited
     * based on the number of rolls by the attacker and defender.
     * <p>
     * While rolling the maximum number of dice (3 and 2), the attacker cannot possibly
     * lose more than 2 units. Same goes with the defender in this scenario.
     * Otherwise, the attacker and defender cannot lose more than the number
     * of dice they rolled.
     * <p>
     * The attacker and defender cannot have a negative value produced for the
     * number of units lost in the attack().
     */
    @Test
    public void testAttack() {
        int[] lost = gsm.attack(3, 2);
        int highAttackerLost = 2;
        int lowAttackerLost = 0;
        int highDefendingLost = 2;
        int lowDefendingLost = 0;

        System.out.println(lost[0]+"-"+lost[1]);

        assertFalse("Error, too many attacking units lost", highAttackerLost < lost[0]);
        assertFalse("Error, can't lose negative attacking units", lowAttackerLost > lost[0]);
        assertFalse("Error, too many defending units lost", highDefendingLost < lost[1]);
        assertFalse("Error, can't lose negative defending units", lowDefendingLost > lost[1]);

        lost = gsm.attack(2, 2);

        assertFalse("Error, too many attacking units lost", highAttackerLost < lost[0]);
        assertFalse("Error, can't lose negative attacking units", lowAttackerLost > lost[0]);
        assertFalse("Error, too many defending units lost", highDefendingLost < lost[1]);
        assertFalse("Error, can't lose negative defending units", lowDefendingLost > lost[1]);

        lost = gsm.attack(2, 1);
        highAttackerLost = 2;
        highDefendingLost = 1;

        assertFalse("Error, too many attacking units lost", highAttackerLost < lost[0]);
        assertFalse("Error, can't lose negative attacking units", lowAttackerLost > lost[0]);
        assertFalse("Error, too many defending units lost", highDefendingLost < lost[1]);
        assertFalse("Error, can't lose negative defending units", lowDefendingLost > lost[1]);

        lost = gsm.attack(1, 1);
        highAttackerLost = 1;

        assertFalse("Error, too many attacking units lost", highAttackerLost < lost[0]);
        assertFalse("Error, can't lose negative attacking units", lowAttackerLost > lost[0]);
        assertFalse("Error, too many defending units lost", highDefendingLost < lost[1]);
        assertFalse("Error, can't lose negative defending units", lowDefendingLost > lost[1]);

        lost = gsm.attack(1, 2);
        highDefendingLost = 2;

        assertFalse("Error, too many attacking units lost", highAttackerLost < lost[0]);
        assertFalse("Error, can't lose negative attacking units", lowAttackerLost > lost[0]);
        assertFalse("Error, too many defending units lost", highDefendingLost < lost[1]);
        assertFalse("Error, can't lose negative defending units", lowDefendingLost > lost[1]);
    }

    /**
     * Test rollDice() in GameSingleton class
     * <p>
     * Checks that when a set of dice are rolled, that the results
     * are within and including 1 and 6.
     */
    @Test
    public void testRollDie() {
        int[] theRolls = gsm.rollDice(3);
        assertEquals(3, theRolls.length);
        for (int i = 0; i < 3; i++) {
            assertTrue(theRolls[i] > 0);
            assertTrue(theRolls[i] < 7);
        }
    }

    /**
     * Test updateNumActivePlayer() in GameSingleton class
     * <p>
     * Creates two territories with different names, owners and units. Only
     * two of the four total players own territories, so the getNumActivePlayer()
     * should return the proper amount.
     */
    @Test
    public void testUpdateNumActivePlayer() {
        Player ethan = players.get(0);
        Player anthony = players.get(1);

        gsm.setNumActivePlayer(players.size());

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

        gsm.updateNumActivePlayers();
        assertEquals(2, gsm.getNumActivePlayer());

        /* Remove territory from one of the two active players
        Now check if only one player is left.
         */
        anthony.removeTerritory(t2);
        gsm.updateNumActivePlayers();
        assertEquals(false, anthony.isActive());
        assertEquals(1, gsm.getNumActivePlayer());
    }

    /**
     * Test nextPlayer() method in GameSingleton class
     * <p>
     * Starts a new four player game, when the game is starting
     * the order of players is shuffled, tests to see
     * if the nextPlayer method will cycle through
     * all the players when every player presses end turn
     */
    @Test
    public void testNextPlayer() {

        //Shuffles Player Order
        //gsm.setUpGame();

        //Test 1, First Player -> Second Player
        Player first = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(first, gsm.getCurrentPlayer());

        //Test 2 Second Player -> Third Player
        Player second = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(second, gsm.getCurrentPlayer());

        //Test 3 Third Player -> Fourth Player
        Player third = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(third, gsm.getCurrentPlayer());

        //Test 4 Fourth Player -> First Player
        Player fourth = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(fourth, gsm.getCurrentPlayer());

        //Test 5, First Player IS the First Player (Looped through all players)
        assertEquals(first, gsm.getCurrentPlayer());
    }
}