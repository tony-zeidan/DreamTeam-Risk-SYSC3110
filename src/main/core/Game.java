package main.core;

import main.view.RiskEvent;
import main.view.RiskFrame;
import main.view.RiskGameView;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.LinkedList;

/**
 * Class main.core.Game implements the main functionality of the RISK game.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @version 1.00
 * @since 1.00
 */
public class Game {
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
    public Game(int numOfPlayers, ArrayList<String> names) {

        //initialize map, player list, and scanner
        players = new ArrayList<>(6);
        world = new WorldMap("Earth");
        myAction = new Scanner(System.in);
        currentPlayerInd = 0;
        /*
        We must get the amount of people playing the game.
        Continuously prompt the user for valid information.
         */

        //set the initial amount of active players accordingly
        numActivePlayer = numOfPlayers;

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
        for (int i = 0; i < numOfPlayers; i++) {
            //get this players name

            int randIndex = rand.nextInt(randomColors.size());
            //generate and assign random colours
            Color colour = randomColors.get(randIndex);
            randomColors.remove(randIndex);
            players.add(new Player(names.get(i), colour));
        }
        //shuffle the order of the players
        shufflePlayers();
        world.setUp(players);
    }

    /**
     * Testing constructor of main.core.Game.
     * Creates a new game with two territories.
     *
     * @param test Does nothing
     */
    public Game(String test) {
        players = new ArrayList<>(2);
        players.add(new Player("Jim", Color.RED));
        players.add(new Player("Fred", Color.BLUE));
        world = new WorldMap();
        myAction = new Scanner(System.in);
        numActivePlayer = 2;
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
    public String getStartingPlayer()
    {
        return players.get(0).getName();
    }
    public void makeView(RiskGameView rgv)
    {
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
        currentPlayerInd = (currentPlayerInd+1)%players.size();
        while(!(players.get(currentPlayerInd).isActive())){
            currentPlayerInd = (currentPlayerInd+1)%players.size();
        }
        riskView.handleRiskUpdate(new RiskEvent(this,"Next Turn", players.get(currentPlayerInd).getName()));
    }
    /**
     * Runs the current session corresponding to all the setup that has been done.
     * (Main main.core.Game Loop)
     */
    public void runGame() {

        //print player order at the start of the game.
        System.out.println("The order of players: ");
        for (int i = 0; i < players.size(); i++) {
            System.out.println((i + 1) + " : " + players.get(i).getName() + " ; " + players.get(i).getColour());
        }

        String endStart = "|*----------------------------------------{%s's Turn %s}----------------------------------------*|";

        //main game loop
        boolean finished = false;
        while (!finished) {
            //loop through each player (Turns), until the game is over.
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).isActive()) {
                    Player currentPlayer = players.get(i);

                    //print out number of remaining players and whose turn it is
                    System.out.println(String.format("Remaining Players: %s\n", numActivePlayer));

                    //beginning of turn print
                    System.out.println(String.format(endStart, currentPlayer.getName(), "Begins!"));

                    //While loop for current players turn
                    boolean playerTurn = false;
                    while (!playerTurn) {
                        System.out.println(String.format("\nIt is %s of %s's turn.", players.get(i).getName(), players.get(i).getColour()));
                        //Print out the available commands and asks for a command
                        System.out.println("Commands: attack, worldstate, end");
                        System.out.println("What do you want to do?");
                        String command = myAction.nextLine().toLowerCase();
                        System.out.println(String.format("Selected command: %s\n", command));
                        switch (command) {

                            //Current player selected 'attack' : Begin attack protocol
                            case "attack":
                            /*
                            Attacking Conditions:
                            1) Defending territory must be a neighbour to Attacking territory
                            2) There are 2 units on the attacking territory (taken care of in implementation)
                            3) Attacking owner does not own Defending territory
                            */
                                Territory attacking = null;

                                //keep asking for a territory until we get a valid one
                                while (attacking == null) {
                                    players.get(i).printOwned();
                                    System.out.print("\nattack from where? ");
                                    attacking = getTerritory(myAction.nextLine());
                                    if (attacking == null) {
                                        System.out.println("\nThat territory is not valid, try again.");
                                        attacking = null;
                                    } else if (world.getTerritoryOwner(attacking) != players.get(i)) {
                                        System.out.println("\nYou do not own that territory, try again.");
                                        attacking = null;
                                    }
                                }

                                //we can use this method if we have conquered all neighbouring territories
                                if (!world.ownsAllTerritoryNeighbours(currentPlayer,attacking)) {
                                    System.out.println("");

                                    Territory defending = null;
                                    while (defending == null) {
                                        world.printTerritoryNeighbours(attacking);
                                        System.out.print("\nwho to attack? ");
                                        defending = getTerritory(myAction.nextLine());
                                        if (defending == null) {
                                            System.out.println("\nThat territory is not valid, try again.");
                                            defending = null;
                                        } else if (!world.areNeighbours(attacking,defending)) {
                                            System.out.println("\nThat territory is not a neighbour, try again.");
                                            defending = null;
                                        } else if (currentPlayer.ownsTerritory(attacking) && currentPlayer.ownsTerritory(defending)) {
                                            System.out.println("\nYou can not attack yourself!");
                                            defending = null;
                                        }
                                    }
                                    //battle(attacking, defending);
                                    System.out.println("");
                                } else {
                                    System.out.println("You can not attack as you have conquered all neighbouring territories.\n");
                                }
                                break;

                       /*
                       The current player had ended their turn.
                       1) Print a turn ended message.
                       2) Break the current turn loop and move on to the next player.
                        */
                            case "end":
                                System.out.println("you typed end");
                                playerTurn = true;

                                //end of turn print
                                System.out.println(String.format(endStart, currentPlayer.getName(), "Ends!"));
                                break;

                            case "worldstate":
                                checkWorld();
                                break;
                            //Current player selected an invalid command, lets player know this and asks for a command again.
                            default:
                                System.out.println("Not a valid command");
                                break;

                        }
                        updateIsInactive();
                        //Only one player remains, end the game.
                        if (numActivePlayer == 1) {
                            finished = true;
                            playerTurn = true;
                        }
                    }
                }
            }
        }
        /*
        The game has now ended.
        1) Print the winner of the game
         */
        Player winner = null;
        for (Player p : players) {
            if (p.isActive()) winner = p;
        }
        System.out.println("|*----------------------------------------{GAME OVER}----------------------------------------*|");
        System.out.println(String.format("%s of %s has conquered all of %s! Hooray!", winner.getName(), winner.getColour(), world.getName()));
    }

    /**
     * Prints the current state of the world.
     */
    private void checkWorld() {
        System.out.println(String.format("|--------------------(World State: %s)--------------------|", world.getName()));
        printMap();
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

    /**
     * Simulates the battle sequence between a territory attacking an adjacent territory. The attacker
     * is required to select a number of dice to attack with provided he/she meets the minimum unit requirements
     *
     * @param attacking The territory containing units that will be used in the attack
     * @param defending The territory being attacked
     */
    public void battle(Territory attacking, Territory defending, int attackDie, int defendDie) {
        int[] lost = attack(attackDie, defendDie);
        attacking.removeUnits(lost[0]);
        defending.removeUnits(lost[1]);
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
        if (attacking = true) {
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
     * @param attDice The number of dice that the attacker used (if applicable)
     */
    private static void fortifyPosition(Territory initialT, Territory finalT, int attDice) {

        String input;

        //Number of units to fortify
        int numUnits = 0;

        //True if valid number of units is provided by the player, false otherwise
        boolean fortifyCommand = false;

        //Keep looping until player enters a valid number of units to fortify
        while (!fortifyCommand) {
            System.out.println("How many troops would you like to move from " + initialT.getName() + " to " + finalT.getName() + "?");
            input = myAction.nextLine();

            //Check if player provides a number, not text
            try {
                numUnits = Integer.parseInt(input);
                fortifyCommand = true;
                //Check if number inputted is valid
                if (numUnits > initialT.getUnits() - 1 || numUnits < attDice) {
                    fortifyCommand = false;
                    System.out.println("Invalid number of units! Please enter a valid number of units" +
                            "(remember, after winning an attack, you must move units at least the number of attack dice you rolled");
                }
            } catch (NumberFormatException e) {
                //Input provided is not a number
                fortifyCommand = false;
                System.out.println("Invalid number of units! Please enter a valid number of units");
            }
        }

        //Move the units from the fortifying territory to the fortified territory
        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);
    }

    /**
     * Update the number of players active.
     */
    public void updateIsInactive() {
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