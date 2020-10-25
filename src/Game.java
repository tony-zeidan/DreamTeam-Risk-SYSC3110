import java.util.*;

/**
 * Class Game implements the main functionality of the RISK game.
 *
 * @author Tony Zeidan (Yo)
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
     * @see Player
     */
    private List<Player> players;

    /**
     * The world that the players will be playing on.
     * @see WorldMap
     */
    private WorldMap world;

    private static Scanner myAction;

    /**
     * Default constructor for instances of Game class.
     * (For now) Creates a new game with the hardcoded map and the players that
     * the user inputs.
     */
    public Game() {
        players = new ArrayList<>(6);
        world = new WorldMap();
        myAction = new Scanner(System.in);
        System.out.println("Please input the number of players (max-6 min-2):");
        int numPlayers = Integer.parseInt(myAction.nextLine());
        while (numPlayers > 6 || numPlayers < 2)
        {
            System.out.println("Please input a correct amount of players (max-6 min-2):");
            numPlayers = Integer.parseInt(myAction.nextLine());
        }
        for (int i= 0; i<numPlayers;i++)
        {
            System.out.println(String.format("Player %s Name:",i+1));
            String playerName = myAction.nextLine();
            System.out.println(String.format("Player %s Colour:",i+1));
            String color = myAction.nextLine();
            players.add(new Player(playerName, color));
        }
        reorderPlayers();
        world.setUp(players);
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
     * generates a random order for the players
     */
    private void reorderPlayers()
    {
        //need to make a single random field in game class
        Random rand = new Random();

        for (int i = players.size(); i >0;i--)
        {
            Player holder = players.get(players.size()-i);
            int chosen  = rand.nextInt(i);
            players.set(players.size()-i, players.get(chosen));
            players.set(chosen, holder);
        }
    }

    /**
     * Runs the current session corresponding to all the setup that has been done.
     * (Main Game Loop)
     */
    public void runGame() {

        //Create a scanner object that scans the current players action
        Scanner myAction = new Scanner(System.in);

        System.out.println("The order of players: ");

        //Tells the Player order before starting the game
        for (int i = 0; i < players.size(); i++){
            System.out.println((i + 1) + " : " + players.get(i).getName() + " ; " + players.get(i).getColour());
        }

        //The games loop
        boolean finished = false;
        while (!finished) {
            //Loop through each player (Turns), until the game is over.
            for (int i = 0; i < players.size(); i++) {
                //Print out number of remaining players and whose turn it is
                System.out.println(String.format("Remaining Players: %s\n",players.size()));    //TODO: change this to amount of active players
                //While loop for current players turn
                boolean playerTurn = false;
                while (!playerTurn) {
                    System.out.println(String.format("It is %s of %s's turn.",players.get(i).getName(),players.get(i).getColour()));
                    //Print out the available commands and asks for a command
                    System.out.println("Commands: attack, check, end, kys");
                    System.out.println("What do you want to do?");
                    String command = myAction.nextLine().toLowerCase();
                    System.out.println(String.format("Selected command: %s\n",command));
                    switch (command) {
                        //Current player selected 'attack' : Begin attack protocol
                        case "attack":
                            /*
                            Attacking Conditions:
                            1) Defending territory must be a neighbour to Attacking territory
                            2) There are 2 units on the attacking territory (taken care of in implementation)
                            3) Attacking owner does not own Defending territory
                            */
                            players.get(i).printOwned();

                            Territory attacking = null;

                            //keep asking for a territory until we get a valid one
                            while (attacking==null) {
                                System.out.print("\nattack from where? ");
                                attacking = getTerritory(myAction.nextLine());
                                if (attacking==null) {
                                    System.out.println("That territory is not valid, try again.");
                                    attacking = null;
                                } else if (attacking.getOwner()!=players.get(i)) {
                                    System.out.println("You do not own that territory, try again.");
                                    attacking = null;
                                }
                            }

                            //we can use this method if we have conquered all neighbouring territories
                            if (!attacking.ownsAllNeighbours()) {
                                System.out.println("");
                                attacking.printValidNeighbours(false);

                                Territory defending = null;
                                while (defending == null) {
                                    System.out.print("\nwho to attack? ");
                                    defending = getTerritory(myAction.nextLine());
                                    if (defending == null) {
                                        System.out.println("That territory is not valid, try again.");
                                        defending = null;
                                    } else if (!attacking.isNeighbour(defending)) {
                                        System.out.println("That territory is not a neighbour, try again.");
                                        defending = null;
                                    } else if (attacking.getOwner()==defending.getOwner()) {
                                        System.out.println("You can not attack yourself!");
                                        defending = null;
                                    }
                                }
                                battle(attacking, defending);
                            } else {
                                System.out.println("You can not attack as you have conquered all neighbouring territories.\n");
                            }
                            break;

                        //Current player selected 'end' : Ends Players turn.
                        case "end":
                            System.out.println("you typed end");
                            playerTurn = true;
                            break;

                        //Current player selected 'check' : Prints current state of the world map
                        case "check":
                            printMap();
                            break;

                        //Not for submission. please delete this.
                        case "kys":
                            players.remove(i);
                            playerTurn = true;
                            break;

                        //Current player selected an invalid command, lets player know this and asks for a command again.
                        default:
                            System.out.println("Not a valid command");
                            break;

                    }
                    //Only one player remains, end the game.
                    if(players.size() == 1){
                        finished = true;
                    }
                }
            }
        }

        //The game has ended
        System.out.println("The game has ended.");
    }



    /**
     * Simulates the battle sequence between a territory attacking an adjacent territory. The attacker
     * is required to select a number of dice to attack with provided he/she meets the minimum unit requirements
     *
     * @param attacking The territory containing units that will be used in the attack
     * @param defending The territory being attacked
     */
    private static void battle(Territory attacking, Territory defending) {

        System.out.println(String.format("|------------------(Battle Commenced - %s vs. %s)------------------|",attacking.getName(),defending.getName()));
        String end = "|------------------(Battle %s - %s)------------------|";
        String attInput;
        String defInput;

        String attName = attacking.getOwner().getName();
        String defName = defending.getOwner().getName();

        int attDice = 0;
        int defDice;

        boolean retreat = false;

        //Continue the attack step until either side loses all of its units or the attacker decides to retreat
        while (!retreat) {

            System.out.println(String.format("%s's Units: %s\n%s's Units: %s",attName,attacking.getUnits(),defName,defending.getUnits()));
            if (defending.getUnits()==0) {
                System.out.println(String.format("%s dominates over %s!",attacking.getName(),defending.getName()));
                System.out.println(String.format(end,"Ended",attacking.getName()+" Wins!"));
                defending.setOwner(attacking.getOwner());
                fortifyPosition(attacking,defending,attDice);
                break;
            } else if (attacking.getUnits()==1) {
                System.out.println(String.format("%s drives off the attacker!",defName));
                System.out.println(String.format(end,"Ended",defending.getName()+" Wins!"));
                break;
            }

            System.out.println(String.format("\n%s is attacking. %s, would you like to attack or retreat?",attName,attName));
            attInput = myAction.nextLine().toLowerCase();

            //If the command is to attack or retreat
            if (attInput.equals("attack")) {

                /*Check the number of units contained in the attacking territory. The attacker must have
                at least two units in their territory; one unit attacks the defended territory while the
                other unit continues to occupy the attacker's territory.
                 */
                if (attacking.getUnits() == 2) {
                    //Attack with one attacking dice if the attacking territory contains exactly two units
                    attDice = 1;
                    System.out.println("You are attacking with 1 attack dice!");
                } else if (attacking.getUnits() == 3) {
                    /*If the attacking territory contains exactly three units, ask the attacker if he/she would
                    like to use 1 or 2 attacking dice*/
                    System.out.println(attName + ", would you like to attack with 1 or 2 dice?");
                    attInput = myAction.nextLine();

                    //Check for invalid input
                    try {
                        attDice = Integer.parseInt(attInput);
                        attDice = (attDice > 3 || attDice < 1) ? 2 : attDice;
                    } catch (NumberFormatException e) {
                        //Default choice is two attacking dice if input is invalid
                        attDice = 2;
                    }
                } else {
                    /*If the attacking territory contains four or more units before the next attack, ask the attacker
                    if he/she would like to use 1,2 or 3 attacking dice*/
                    System.out.println(attName + ", would you like to attack with 1, 2 or 3 dice?");
                    attInput = myAction.nextLine();

                    //check for invalid input
                    try {
                        attDice = Integer.parseInt(attInput);
                        attDice = (attDice > 4 || attDice < 1) ? 3 : attDice;
                    } catch (NumberFormatException e) {
                        //Default choice is three attacking dice if input is invalid
                        attDice = 3;
                    }
                }

                /*Check the number of units contained in the defending territory. If there is only one unit in
                the defending territory, the defender only has the option to roll one die. If there is more than
                one unit in the defending territory, the defender may chose to roll either one die or two dice
                for the attack.
                 */
                if (defending.getUnits() == 1) {
                    //Defender must roll only one die
                    defDice = 1;
                    System.out.println("You are defending with 1 defense dice!");
                } else {
                    //Defender choses to roll one die or two dice
                    System.out.println(defName + ", would you like to defend with 1 or 2 dice?");
                    defInput = myAction.nextLine();

                    //Check for invalid input
                    try {
                        defDice = Integer.parseInt(defInput);
                        defDice = (defDice > 2 || defDice < 1) ? 2 : defDice;
                    } catch (NumberFormatException e) {
                        //Default choice is two dice if input is invalid
                        defDice = 2;
                    }
                }
                //Proceed to the attack phase
                int[] lost = attack(attDice, defDice);
                attacking.removeUnits(lost[0]);
                defending.removeUnits(lost[1]);
                System.out.println("");

            } else if (attInput.equals("retreat")) {
                //Attacker chooses to retreat from the battle
                retreat = true;
            }
        }

        //Declare the battle to be officially resolved
        System.out.println("Battle is over between " + attName + " and " + defending.getOwner().getName());
    }

    /**
     * The player has the ability to attack other territories owned
     * by other players
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
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
     * Fortify more units into one territory from an adjacent territory, such
     * that the current player owns both territories. At least one unit must
     * be left behind in the initial territory. Used after an attack once the
     * territory has been claimed.
     *
     * @param initialT The territory that will move units out
     * @param finalT   The territory that will add units
     */
    private static void fortifyPosition(Territory initialT, Territory finalT, int attDice) {

        String input;
        int numUnits = 0;

        boolean fortifyCommand = false;
        while(!fortifyCommand){
            System.out.println("How many troops would you like to move from " + initialT.getName() + " to " + finalT.getName() + "?");
            input = myAction.nextLine();
            //Check if input is a valid number of units to move
            try {
                numUnits = Integer.parseInt(input);
                fortifyCommand = true;
                if (numUnits > initialT.getUnits()-1 || numUnits < attDice){
                    fortifyCommand = false;
                    System.out.println("Invalid number of units! Please enter a valid number of units" +
                            "(remember, after winning an attack, you must move units at least the number of attack dice you rolled");
                }
            } catch (NumberFormatException e) {
                fortifyCommand = false;
                System.out.println("Invalid number of units! Please enter a valid number of units");
            }
        }

        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);
    }

    /**
     *
     * @return int the number of active players left
     */
    public int updateIsInactive()
    {
        int numActive = 0;
        ArrayList<Territory> territories = world.getTerritories();
        for(Player player:players)
        {
            if (player.isActive())
            {
                if(player.getOwnedTerritories().size()>0)
                {
                    numActive += 1;
                }
                else
                {
                    //update
                    player.setActive(false);
                }
            }
        }
        return numActive;
    }

    /**
     * This main method represents the main game loop.
     */
    public static void main(String[] args) {

        Game g1 = new Game();
        g1.runGame();
    }
}
