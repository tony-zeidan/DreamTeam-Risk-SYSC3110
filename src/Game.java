import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public static void main(String[] args) {
        System.out.println("Works");
    }

    public void Battle(Player attacker,Territory attacking,Territory defending) {

        Scanner aCommand = new Scanner(System.in);

        String input = "";

        String attName = attacker.getName();

        int numAttack = 0;
        int numDefense = 0;

        boolean retreat = false;
        while (!retreat) {

            System.out.println(attName + " is attacking. "+attName+", would you like to attack or retreat?\n");

            input = aCommand.nextLine();

            if(input.equalsIgnoreCase("attack")){
                System.out.println("How many attacking dice would you like to use: ");
                if(input.equalsIgnoreCase("3")){
                    numAttack = 3;
                }else if(input.equalsIgnoreCase("2")){
                    numAttack = 2;
                }else if(input.equalsIgnoreCase("1")){
                    numAttack = 1;
                }
                attacker.attack(numAttack,numDefense);

            }else if(input.equalsIgnoreCase("retreat")){
                retreat = true;
            }
        }

        System.out.println("Battle is over between "+attName+ " and "+defending.getOwner().getName());
    }
}
