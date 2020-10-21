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

    public Player[] getPlayers() {
        Player[] pl = new Player[players.size()];
        pl = players.toArray(pl);
        return pl;
    }

    public static void main(String[] args) {
        Game g1 = new Game();
        g1.addPlayer(new Player("Tony","RED"));
        g1.addPlayer(new Player("Ethan","BLUE"));
        g1.addPlayer(new Player("Anthony","YELLOW"));
        g1.addPlayer(new Player("Verge","GREEN"));
        WorldMap w1 = new WorldMap();
        w1.setUp(g1.getPlayers());
        w1.createTerritories();

        w1.printMap();

        System.out.println("Works");
    }

    public void Battle(Player attacker,Territory attacking,Territory defending) {

        Scanner aCommand = new Scanner(System.in);

        String attInput;
        String defInput;

        String attName = attacker.getName();
        String defName = defending.getOwner().getName();

        int attDice;
        int defDice;

        boolean retreat = false;
        while (!retreat) {

            System.out.println(attName + " is attacking. "+attName+", would you like to attack or retreat?\n");

            attInput = aCommand.nextLine();

            if(attInput.equalsIgnoreCase("attack")){

                if(attacking.getUnits() == 1){
                    //Attack with 1 attacking dice
                    attDice = 1;
                    System.out.println("You are attacking with 1 attack dice!");
                }else if(attacking.getUnits() == 2){
                    /*If the attacking territory contains exactly two units, ask the attacker if he/she would
                    like to use 1 or 2 attacking dice*/
                    System.out.println(attName+ ", would you like to attack with 1 or 2 dice?\n");
                    attInput = aCommand.nextLine();
                    try{
                        attDice = Integer.parseInt(attInput);
                        attDice = (attDice > 2 || attDice < 1)? 2: attDice;
                    } catch (NumberFormatException e){
                        //Default choice is 2 attacking dice
                        attDice = 2;
                    }
                }else{
                    /*If the attacking territory contains three or more units, ask the attacker if he/she would
                    like to use 1,2 or 3 attacking dice*/
                    System.out.println(attName+ ", would you like to attack with 1, 2 or 3 dice?\n");
                    attInput = aCommand.nextLine();
                    try{
                        attDice = Integer.parseInt(attInput);
                        attDice = (attDice > 3 || attDice < 1)? 3: attDice;
                    } catch (NumberFormatException e){
                        //Default choice is 3 attacking dice
                        attDice = 3;
                    }
                }

                if (defending.getUnits() == 1){
                    defDice = 1;
                    System.out.println("You are defending with 1 defense dice!");
                }else{
                    System.out.println(defName+ ", would you like to defend with 1 or 2 dice?\n");
                    defInput = aCommand.nextLine();
                    try{
                        defDice = Integer.parseInt(defInput);
                        defDice = (defDice > 2 || defDice < 1)? 2: defDice;
                    } catch (NumberFormatException e){
                        defDice = 2;
                    }
                }

                //Attack!
                attack(attDice,defDice);

            }else if(attInput.equalsIgnoreCase("retreat")){
                //Retreat from the battle
                retreat = true;
            }
        }

        System.out.println("Battle is over between "+attName+ " and "+defending.getOwner().getName());
    }

    /**
     * The player has the ability to attack other territories owned
     * by other players
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
     */
    public int[] attack(int attackRolls, int defendRolls) {

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
}
