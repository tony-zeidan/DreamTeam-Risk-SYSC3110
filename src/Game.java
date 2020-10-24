import java.util.*;

/**
 * Class Game implements the main functionality of the RISK game.
 *
 * @author Tony Zeidan (Yo)
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @since 1.00
 * @version 1.00
 */
public class Game {

    private List<Player> players;
    //private WorldMap map;

    public Game() {
        players = new ArrayList<>(6);

        //player configs...
        //map.setUp(players);

        //fix mapping
        //map = null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
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

    public static void main(String[] args) {

        /*TODO: Remember to ask the current player if they would like to fortify his/her position.
           The current player can fortify one territory only. Must be adjacent */

        Game g1 = new Game();
        g1.addPlayer(new Player("Tony","RED"));
        g1.addPlayer(new Player("Ethan","BLUE"));
        g1.addPlayer(new Player("Anthony","YELLOW"));
        g1.addPlayer(new Player("Verge","GREEN"));
        g1.reorderPlayers();
        WorldMap w1 = new WorldMap();
        w1.setUp(g1.getPlayers());
        w1.printMap();

        System.out.println("Works");
    }

    /** Simulates the battle sequence between a territory attacking an adjacent territory. The attacker
     * is required to select a number of dice to attack with provided he/she meets the minimum unit requirements
     *
     * @param attacking The territory containing units that will be used in the attack
     * @param defending The territory being attacked
     */
    private void Battle(Territory attacking,Territory defending) {

        Scanner aCommand = new Scanner(System.in);

        String attInput;
        String defInput;

        String attName = attacking.getOwner().getName();
        String defName = defending.getOwner().getName();

        int attDice;
        int defDice;

        boolean retreat = false;

        //Continue the attack step until either side loses all of its units or the attacker decides to retreat
        while (!retreat) {

            System.out.println(attName + " is attacking. "+attName+", would you like to attack or retreat?\n");
            attInput = aCommand.nextLine();

            //If the command is to attack or retreat
            if(attInput.equalsIgnoreCase("attack")){

                /*Check the number of units contained in the attacking territory. The attacker must have
                at least two units in their territory; one unit attacks the defended territory while the
                other unit continues to occupy the attacker's territory.
                 */
                if(attacking.getUnits() == 2){
                    //Attack with one attacking dice if the attacking territory contains exactly two units
                    attDice = 1;
                    System.out.println("You are attacking with 1 attack dice!");
                }else if(attacking.getUnits() == 3){
                    /*If the attacking territory contains exactly three units, ask the attacker if he/she would
                    like to use 1 or 2 attacking dice*/
                    System.out.println(attName+ ", would you like to attack with 1 or 2 dice?\n");
                    attInput = aCommand.nextLine();

                    //Check for invalid input
                    try{
                        attDice = Integer.parseInt(attInput);
                        attDice = (attDice > 3 || attDice < 1)? 2: attDice;
                    } catch (NumberFormatException e){
                        //Default choice is two attacking dice if input is invalid
                        attDice = 2;
                    }
                }else{
                    /*If the attacking territory contains four or more units before the next attack, ask the attacker
                    if he/she would like to use 1,2 or 3 attacking dice*/
                    System.out.println(attName+ ", would you like to attack with 1, 2 or 3 dice?\n");
                    attInput = aCommand.nextLine();

                    //check for invalid input
                    try{
                        attDice = Integer.parseInt(attInput);
                        attDice = (attDice > 4 || attDice < 1)? 3: attDice;
                    } catch (NumberFormatException e){
                        //Default choice is three attacking dice if input is invalid
                        attDice = 3;
                    }
                }

                /*Check the number of units contained in the defending territory. If there is only one unit in
                the defending territory, the defender only has the option to roll one die. If there is more than
                one unit in the defending territory, the defender may chose to roll either one die or two dice
                for the attack.
                 */
                if (defending.getUnits() == 1){
                    //Defender must roll only one die
                    defDice = 1;
                    System.out.println("You are defending with 1 defense dice!");
                }else{
                    //Defender choses to roll one die or two dice
                    System.out.println(defName+ ", would you like to defend with 1 or 2 dice?\n");
                    defInput = aCommand.nextLine();

                    //Check for invalid input
                    try{
                        defDice = Integer.parseInt(defInput);
                        defDice = (defDice > 2 || defDice < 1)? 2: defDice;
                    } catch (NumberFormatException e){
                        //Default choice is two dice if input is invalid
                        defDice = 2;
                    }
                }

                //Proceed to the attack phase
                Attack(attDice,defDice);

            }else if(attInput.equalsIgnoreCase("retreat")){
                //Attacker choses to retreat from the battle
                retreat = true;
            }
        }

        //Declare the battle to be officially resolved
        System.out.println("Battle is over between "+attName+ " and "+defending.getOwner().getName());
    }

    /**
     * The player has the ability to attack other territories owned
     * by other players
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
     */
    private int[] Attack(int attackRolls, int defendRolls) {

        //random acts as die
        Random rand = new Random();

        //two primitive integer arrays to store random rolls
        int[] attackDice = new int[attackRolls];
        int[] defendDice = new int[defendRolls];

        //roll dice (random integer) for both parties and display simultaneously
        System.out.print("Attacking Rolls:   |");
        for (int i = 0; i < attackRolls; i++) {
            attackDice[i] = rand.nextInt(6)+1;
            System.out.print(" " + attackDice[i] + " |");
        }
        System.out.print("\nDefending Rolls:  |");
        for (int i = 0; i< defendRolls; i++) {
            defendDice[i] = rand.nextInt(6)+1;
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
        for (int i = attackRolls-1; i >= 0; i--) {
            for (int j = defendRolls-(attackRolls-i); j >= 0; j--) {
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
        return new int[]{attackLost,defendLost};
    }

    /** Fortify more units into one territory from an adjacent territory, such
     * that the current player owns both territories. At least one unit must
     * be left behind in the initial territory.
     *
     * @param initialT The territory that will move units out
     * @param finalT The territory that will add units
     */
    private void fortifyPosition(Territory initialT, Territory finalT){
        Scanner command = new Scanner(System.in);

        String input;

        int numUnits;

        System.out.println("How many troops would you like to move from "+initialT.getName()+" to "+finalT.getName()+"?");
        input = command.nextLine();

        //Check if input is a valid number of units to move
        try{
            numUnits = Integer.parseInt(input);
            numUnits = (numUnits > initialT.getUnits() - 1 || numUnits < 1)? 1: numUnits;
        } catch (NumberFormatException e){
            //Default is to move only one troop
            numUnits = 1;
        }

        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);
    }
}
