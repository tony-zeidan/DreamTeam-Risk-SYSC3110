package main.core;

import main.view.RiskEvent;
import main.view.RiskEventType;
import main.view.RiskFrame;
import main.view.RiskGameView;

import javax.swing.*;
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
    private RiskGameView riskView;

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
    }

    public static GameSingleton getGameInstance(List<Player> players){
        //if an instance doesn't exist, create only one instance
        if(gameInstance == null){
            gameInstance = new GameSingleton(players);
        }
        return gameInstance;
    }

    public void setUpGame() {
        //set the initial amount of active players accordingly
        numActivePlayer = players.size();

        //six random colors for players
        List<Color> randomColors = new LinkedList<>();
        randomColors.add(Color.RED);
        randomColors.add(Color.GREEN);
        randomColors.add(Color.BLUE);
        randomColors.add(Color.YELLOW);
        randomColors.add(Color.ORANGE);
        randomColors.add(Color.CYAN);

        Random rand = new Random();

        /*
        We must get all player names and generate colours.
        Loop through players and obtain names through user input.
        Randomly assign colours.
         */
        for (Player p : players) {
            //get this players name

            int randIndex = rand.nextInt(randomColors.size());
            //generate and assign random colours
            Color colour = randomColors.get(randIndex);
            p.setColour(colour);
            randomColors.remove(randIndex);
        }
        //shuffle the order of the players
        shufflePlayers();
        world.setUp(players);

        riskView.handleRiskUpdate(new RiskEvent(
                this, "Welcome to RISK, the game has started!",
                RiskEventType.GAME_STARTED));

        riskView.handleRiskUpdate(new RiskEvent(this,
                players.get(currentPlayerInd),
                RiskEventType.TURN_BEGAN));
    }

    public Map<Territory,Point> getAllCoordinates() {
        return world.getAllCoordinates();
    }

    public Map<Territory,Point> getNeighbouringNodes(Territory territory) {
        return world.getNeighbouringNodes(territory);
    }

    public Player getTerritoryOwner(Territory territory) {
        return world.getTerritoryOwner(territory);
    }

    public void makeView(RiskGameView rgv) {
        riskView = rgv;
    }

    /**
     * Retrieves a territory from the map (or null).
     * {@link WorldMap#getTerritory(String)}
     *
     * @param name The name of the territory to search for
     * @return The requested territory (null if not found)
     */
    public Territory getTerritory(String name) {
        return world.getTerritory(name);
    }

    /**
     * Asks the current world to print itself.
     * {@link WorldMap#printMap}
     */
    public void printMap() {
        world.printMap();
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
     * get the next active player
     */
    public void nextPlayer()
    {
        riskView.handleRiskUpdate(new RiskEvent(
                this, players.get(currentPlayerInd),
                RiskEventType.TURN_ENDED
        ));

        currentPlayerInd = (currentPlayerInd+1)%players.size();
        while(!(players.get(currentPlayerInd).isActive())){
            currentPlayerInd = (currentPlayerInd+1)%players.size();
        }

        riskView.handleRiskUpdate(new RiskEvent(
                this, players.get(currentPlayerInd),
                RiskEventType.TURN_BEGAN
        ));
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerInd);
    }


    /**
     * Prints the current state of the world.
     */
    private void checkWorld() {
        System.out.println(String.format("|--------------------(World State: %s)--------------------|", world.getName()));
        printMap();
    }

    /**
     * Game has ended. Print the name and colour of the player who won the game.
     */
    private void endGame(){
        Player winner = null;

        for (Player p : players) {
            if (p.isActive()) winner = p;
        }

        riskView.handleRiskUpdate(new RiskEvent(this,
                String.format("%s of %s has conquered all of %s! Hooray!", winner.getName(), winner.getColour(), world.getName()),
                RiskEventType.GAME_OVER));
    }


    private int getPlayerDieCount(Player player, int lowerBound, int upperBound) {
        if (lowerBound==upperBound) return lowerBound;

        boolean validInput = false;
        int dieCount = 0;
        while (!validInput) {
            System.out.println(String.format("%s how many die would you like to roll with? (%s to %s)",player.getName(),lowerBound,upperBound));
            String attInput = myAction.nextLine();
            try {
                dieCount = Integer.parseInt(attInput);
                validInput = true;
                if (dieCount>upperBound||dieCount<lowerBound) {
                    validInput = false;
                }
            } catch (NumberFormatException e) {
                validInput = false;
            }
        }
        return dieCount;
    }

    public Map<Territory,Point> getAllNodes() {
        return world.getAllCoordinates();
    }

    public Map<Territory,Point> getValidAttackNeighboursOwned(Player attacker, Territory defending) {

        if (attacker.ownsTerritory(defending)) return null;
        Map<Territory,Point> neighboursOwned = world.getNeighbourNodesOwned(attacker,defending);
        List<Territory> invalid = new ArrayList<>();
        for (Territory t : neighboursOwned.keySet()) {
            if (t.getUnits()==1) invalid.add(t);
        }
        for (Territory t : invalid) {
            neighboursOwned.remove(t);
        }
        return (neighboursOwned.size()>0)?neighboursOwned:null;
    }

    public Map<Territory,Point> getNeighboursOwned(Player player,Territory territory) {
        return world.getNeighbourNodesOwned(player,territory);
    }

    /**
     * Simulates the battle sequence between a territory attacking an adjacent territory. The attacker
     * is required to select a number of dice to attack with provided he/she meets the minimum unit requirements
     *
     * @param attacking The territory containing units that will be used in the attack
     * @param defending The territory being attacked
     */
    public boolean battle(Territory attacking, Territory defending, int attackDie, int defendDie) {
        riskView.handleRiskUpdate(new RiskEvent(this,
                "Attack has started between "+world.getTerritoryOwner(attacking).getName()+" and "+world.getTerritoryOwner(defending).getName(),
                RiskEventType.ATTACK_COMMENCED));

        int[] lost = attack(attackDie, defendDie);
        attacking.removeUnits(lost[0]);
        defending.removeUnits(lost[1]);

        riskView.handleRiskUpdate(new RiskEvent(this,
                world.getTerritoryOwner(attacking).getName()+" lost "+lost[0]+" units and "+world.getTerritoryOwner(defending).getName()+" lost "+lost[1]+" units!",
                RiskEventType.ATTACK_COMPLETED));

        if (defending.getUnits()==0) {
            riskView.handleRiskUpdate(new RiskEvent(this,
                    world.getTerritoryOwner(attacking).getName()+" obliterated "+world.getTerritoryOwner(defending).getName(),
                    RiskEventType.TERRITORY_DOMINATION));
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
    private static int[] attack(int attackRolls, int defendRolls) {

        //random acts as die
        Random rand = new Random();

        //two primitive integer arrays to store random rolls
        int[] attackDice = new int[attackRolls];
        int[] defendDice = new int[defendRolls];

        //roll dice (random integer) for both parties and display simultaneously
        System.out.print("Attacking Rolls:   |");
        for (int i = 0; i < attackRolls; i++) {
            attackDice[i] = rand.nextInt(6) + 1;
            System.out.print(" " + attackDice[i] + " |");
        }
        System.out.print("\nDefending Rolls:  |");
        for (int i = 0; i < defendRolls; i++) {
            defendDice[i] = rand.nextInt(6) + 1;
            System.out.print(" " + defendDice[i] + " |");
        }

        //sort both rolls in descending order
        Arrays.sort(attackDice);
        Arrays.sort(defendDice);

        //Set counter variables for lost units in the attack
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

    /**
     *
     */
    public int getMaxBattleDie(int numUnits, boolean attacking) {
        //The Player is Attacking
        if (attacking == true) {
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
     * @param finalT The territory that will add units
     * @param numUnits The number of units that the attacker wants to move
     */
    public void fortifyPosition(Territory initialT, Territory finalT, int numUnits) {

        //Move the units from the fortifying territory to the fortified territory
        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);

        Player attacker = world.getTerritoryOwner(initialT);
        Player defender = world.getTerritoryOwner(finalT);

        attacker.addTerritory(initialT);
        defender.removeTerritory(finalT);

        riskView.handleRiskUpdate(new RiskEvent(this,
                numUnits+" have been moved from "+initialT.getName()+" to "+finalT.getName()+"!",
                RiskEventType.UNITS_MOVED));
    }

    /**
     * Update the number of players active.
     */
    public void checkEliminated() {
        int numActive = 0;
        List<Territory> territories = world.getTerritories();
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
}
