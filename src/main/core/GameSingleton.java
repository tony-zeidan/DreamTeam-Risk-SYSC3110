package main.core;

import main.view.RiskEvent;
import main.view.RiskEventType;
import main.view.RiskGameView;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.LinkedList;

public class GameSingleton {

    /**
     * Single instance of the game itself
     */
    private static GameSingleton gameInstance;

    /**
     * The list of players that may or may not be active throughout the game.
     *
     * @see Player
     */
    private List<Player> players;

    /**
     * The world that the players will be playing on.
     *
     * @see WorldMap
     */
    private WorldMap world;

    /**
     * Contains the current number of active players.
     */
    private int numActivePlayer;

    /**
     * Scanner for user input.
     */
    private static Scanner myAction;
    private int currentPlayerInd;
    private ArrayList<RiskGameView> riskHandlers;

    /**
     * Default constructor for instances of main.core.Game class.
     * (For now) Creates a new game with the hardcoded map and the players that
     * the user inputs.
     */
    private GameSingleton(List<Player> players) {
        //initialize map, player list, and scanner
        this.players = players;
        world = new WorldMap("Earth");
        myAction = new Scanner(System.in);
        currentPlayerInd = 0;
        riskHandlers = new ArrayList<>();
    }

    //TODO

    /**
     * @param players
     * @return
     */
    public static GameSingleton getGameInstance(List<Player> players) {
        //if an instance doesn't exist, create only one instance
        if (gameInstance == null) {
            gameInstance = new GameSingleton(players);
        }
        return gameInstance;
    }

    /**
     * Sets up the game through setting up initial amount of players
     * assigning random colours, setting up the world map
     * and notifying all event handlers.
     */
    public void setUpGame() {
        //set the initial amount of active players accordingly
        numActivePlayer = players.size();

        /*Check if colours have already been assigned to players.
        If not, then assign random colours to players.
         */

        if (players.get(0).getColour() == null) {
            //six random colors for players
            List<RiskColour> randomColors = new LinkedList<>();
            randomColors.add(RiskColour.RED);
            randomColors.add(RiskColour.GRAY);
            randomColors.add(RiskColour.BLUE);
            randomColors.add(RiskColour.YELLOW);
            randomColors.add(RiskColour.BLACK);
            randomColors.add(RiskColour.GREEN);

            Random rand = new Random();


            /*We must get all player names and generate colours.
            Loop through players and obtain names through user input.
            Randomly assign colours.
            */
            for (Player p : players) {
                //get this players name

                int randIndex = rand.nextInt(randomColors.size());
                //generate and assign random colours
                RiskColour colour = randomColors.get(randIndex);
                p.setColour(colour);
                randomColors.remove(randIndex);
            }
        }

        System.out.println(riskHandlers.size());
        //shuffle the order of the players
        shufflePlayers();
        world.setUp(players);

        notifyHandlers(new RiskEvent(this, RiskEventType.GAME_BEGAN,
                world.getName()));

        notifyMapUpdateAllCoordinates();

        notifyHandlers(new RiskEvent(this, RiskEventType.TURN_BEGAN,
                getCurrentPlayer()));
    }

    /**
     * @return
     */
    public void notifyMapUpdateAllCoordinates() {
        notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_MAP, world.getAllCoordinates()));
    }

    /**
     * @param territory
     */
    public void notifyMapUpdateAttackingNeighbourCoordinates(Territory territory) {
        notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_MAP, getValidAttackNeighboursOwned(getCurrentPlayer(), territory)));
    }

    /**
     * Create the view for the Risk Game
     *
     * @param rgv A riskgameview
     */
    public void addHandler(RiskGameView rgv) {
        riskHandlers.add(rgv);
    }

    public void removeHandler(RiskGameView rgv) {
        riskHandlers.remove(rgv);
    }

    /**
     * Generates a random order for the players.
     */
    private void shufflePlayers() {
        //need to make a single random field in game class
        Random rand = new Random();

        for (int i = players.size(); i > 0; i--) {
            Player holder = players.get(players.size() - i);
            int chosen = rand.nextInt(i);
            players.set(players.size() - i, players.get(chosen));
            players.set(chosen, holder);
        }
    }

    /**
     * Get the next player who has not yet been eliminated from the game.
     */
    public void nextPlayer() {
        notifyHandlers(new RiskEvent(this,
                RiskEventType.TURN_ENDED, getCurrentPlayer()));

        currentPlayerInd = (currentPlayerInd + 1) % players.size();
        while (!(players.get(currentPlayerInd).isActive())) {
            currentPlayerInd = (currentPlayerInd + 1) % players.size();
        }

        notifyHandlers(new RiskEvent(this,
                RiskEventType.TURN_BEGAN, getCurrentPlayer()));
    }

    /**
     * Get the player who is currently on their turn.
     *
     * @return the player who is currently on their turn
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerInd);
    }

    /**
     * Game has finished and determine the player who won.
     */
    private void endGame() {
        Player winner = null;

        for (Player p : players) {
            if (p.isActive()) winner = p;
        }

        //notify all views that the game is over
        notifyHandlers(new RiskEvent(this,
                RiskEventType.GAME_OVER, winner, world.getName()));
    }

    /**
     * Retrieves a map of all the neighbours around the given territory that can
     * attack the given territory.
     *
     * @param attacker  The player who is currently attacking
     * @param defending The territory being defended
     * @return A map of valid attacking territories owned by the attacker
     */
    public Map<Territory, Point> getValidAttackNeighboursOwned(Player attacker, Territory defending) {

        if (attacker.ownsTerritory(defending)) return null;
        Map<Territory, Point> neighboursOwned = world.getNeighbourNodesOwned(attacker, defending);
        List<Territory> invalid = new ArrayList<>();
        for (Territory t : neighboursOwned.keySet()) {
            if (t.getUnits() == 1) invalid.add(t);
        }
        for (Territory t : invalid) {
            neighboursOwned.remove(t);
        }
        if (neighboursOwned.size() == 0) {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, false));
        } else {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, true));
        }
        return (neighboursOwned.size() > 0) ? neighboursOwned : null;
    }

    /**
     * Represents a battle sequence between a territory owned by the current player and
     * an adjacent territory owned by another player.
     * <p>
     * Completes a single attack and removes units from the territories according to the result of
     * the attack. If, after the attack, the territory owned by a current player loses all but one
     * unit, then declare the attack a victory for the defender and return true. If, after the
     * attack, the territory being attacked contains no more units, then declare the attack a victory
     * for the attacker and return true.
     *
     * @param attacking The territory that supplies the attacking units
     * @param defending The territory that is being attacked
     * @param attackDie The number of dice that the attacker has chosen to roll
     * @param defendDie The number of dice that the defender has chosen to roll
     * @return true if the attacker dominated the defender
     */
    public boolean battle(Territory attacking, Territory defending, int attackDie, int defendDie) {

        Player attacker = attacking.getOwner();
        Player defender = defending.getOwner();

        notifyHandlers(new RiskEvent(this, RiskEventType.ATTACK_COMMENCED,
                attacker, defender));

        int[] lost = attack(attackDie, defendDie);
        attacking.removeUnits(lost[0]);
        defending.removeUnits(lost[1]);

        notifyHandlers(new RiskEvent(this, RiskEventType.ATTACK_COMPLETED,
                attacker, defender, lost));

        if (attacking.getUnits() == 1) {
            notifyHandlers(new RiskEvent(this, RiskEventType.TERRITORY_DEFENDED,
                    attacker, defender, lost));

        } else if (defending.getUnits() == 0) {
            notifyHandlers(new RiskEvent(this, RiskEventType.TERRITORY_DOMINATED,
                    attacker, defender));
            notifyMapUpdateAllCoordinates();
            return true;
        }
        return false;
    }

    /**
     * Represents one smaller conflict between two territories.
     * This method represents the rolling of dice on both parties and the outcome of those rolls.
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
     * @return A pair of integers (position 0: how many units attacker lost, position 1: how many units defender lost)
     */
    private int[] attack(int attackRolls, int defendRolls) {

        //Random Acts as a Die
        Random rand = new Random();

        //Two Primitive Integer Arrays to Store Random Rolls

        //Roll Dice (Random Integer) For Both Parties And Display Simultaneously
        int[] attackDice = rollDice(attackRolls);
        notifyHandlers(new RiskEvent(this, RiskEventType.DIE_ROLLED,
                attackDice));
        int[] defendDice = rollDice(defendRolls);
        notifyHandlers(new RiskEvent(this, RiskEventType.DIE_ROLLED,
                defendDice));

        //Sort Both Rolls in Descending Order
        Arrays.sort(attackDice);
        Arrays.sort(defendDice);

        //Set Counter Variables for Lost Units in the Attack
        int attackLost = 0;
        int defendLost = 0;

        /*
        Logic:
        If both attacking rolls are greater than both defending rolls, then defender loses two units.
        If the top defender roll is equal/greater than the top attacking roll while the second defending roll
            is less than the second attacking roll, then both players lose one unit.
        If both defender rolls are equal to or greater than both attacking rolls, then the attacker loses two
            units.
        If the attacker rolls one dice, then check if that roll is greater than the top defender roll or not
            and remove unit accordingly.
         */
        for (int i = attackRolls - 1; i >= 0; i--) {
            for (int j = defendRolls - (attackRolls - i); j >= 0; j--) {
                if (attackDice[i] > defendDice[j]) {
                    defendLost += 1;
                    break;
                } else {
                    attackLost += 1;
                    break;
                }
            }
        }
        //Return the result of the attack via units lost
        return new int[]{attackLost, defendLost};
    }

    public int[] rollDice(int rolls){

        Random rand = new Random();

        int[] rollers = new int[rolls];

        for(int i=0; i<rolls; i++){
            rollers[i] = rand.nextInt(6) + 1;
        }

        return rollers;
    }

    /**
     * Gets the max amount of dice the attacker/defender can roll
     *
     * @param numUnits  The number of units on the territory
     * @param attacking Whether the player is attacking or defending
     * @return The max number of dice the player can roll
     */
    public int getMaxBattleDie(int numUnits, boolean attacking) {
        //The Player is Attacking
        if (attacking) {
            //Determines Number of Die by the Number of Units on Attacking Territory
            switch (numUnits) {
                case (1):
                    return 0;
                case (2):
                    return 1;
                case (3):
                    return 2;
                default:
                    return 3;
            }
            //The Player is Defending
        } else {
            //Determines Number of Die by the Number of Units on Defending Territory
            switch (numUnits) {
                case (0):
                    return 0;
                case (1):
                    return 1;
                default:
                    return 2;
            }
        }
    }

    /**
     * Fortify more units into one territory from an adjacent territory, such
     * that the current player owns both territories. At least one unit must
     * be left behind in the initial territory. Used after an attack once the
     * territory has been claimed.
     *
     * @param initialT The territory that will move units out
     * @param finalT   The territory that will add units
     * @param numUnits The number of units that the attacker wants to move
     */
    public void fortifyPosition(Territory initialT, Territory finalT, int numUnits) {

        //Move the units from the fortifying territory to the fortified territory
        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);

        Player attacker = initialT.getOwner();
        Player defender = finalT.getOwner();

        //Gives the victor the claimed territory
        finalT.setOwner(attacker);
        //attacker.addTerritory(finalT);
        defender.removeTerritory(finalT);
        //Print a message to confirm the fortify
        notifyHandlers(new RiskEvent(this, RiskEventType.UNITS_MOVED,
                initialT, finalT, numUnits));

        //Check to see if their is only one player remaining
        updateNumActivePlayers();
        if (this.getNumActivePlayer() == 1) {
            endGame();
        }

    }

    /**
     * Update the number of players active.
     */
    public void updateNumActivePlayers() {
        //Check to see if each player has at least one territory of their own, if not they are removed from the game
        int numActive = 0;
        for (Player player : players) {
            if (player.isActive()) {
                if (player.getOwnedTerritories().size() > 0) {
                    numActive += 1;
                } else {
                    //update
                    player.setActive(false);
                }
            }
        }
        numActivePlayer = numActive;
    }

    /**
     * Returns the number of players who have not yet been eliminated.
     *
     * @return The number of active players
     */
    public int getNumActivePlayer() {
        return numActivePlayer;
    }

    /**
     * Notifies the event handlers
     *
     * @param e RiskEvent
     */
    private void notifyHandlers(RiskEvent e) {
        for (RiskGameView rgv : riskHandlers) {
            rgv.handleRiskUpdate(e);
        }
    }
}
