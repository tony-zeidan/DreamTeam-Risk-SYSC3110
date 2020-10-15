import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Class Player represents the user within the {@link Game}.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @since 1.00
 * @version 1.01
 */
public class Player {

    /**
     * The name of the player that this object represents
     */
    private String name;

    /**
     * The colour of the units that the player owns.
     */
    private Color colour;


    /**
     * Constructor for instances of Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public Player(String name, Color colour) {
        this.name = name;
        this.colour = colour;
    }

    /**
     * Constructor for instances of Player class.
     *
     * @param name The name of the player that this object represents
     */
    public Player(String name) {
        this.name = name;
        colour = null;
    }

    /**
     * Retrieves the name of the player
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this player instance.
     *
     * @param name The name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the colour of the units that the player owns.
     *
     * @return The colour of the units that the player owns.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Set the colour of the units that the player will own.
     *
     * @param colour The colour of the units that the player will own
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

    private void reverse(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
    }

    /**
     * The player has the ability to attack other territories owned
     * by other players
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
     */
    public int[] attack(int attackRolls, int defendRolls) {
        Random rand = new Random();

        //generate random rolls for both attacker and defender
        int[] attackDice = new int[]{rand.nextInt(6) + 1, rand.nextInt(6) + 1, rand.nextInt(6) + 1};
        int[] defendDice = new int[]{rand.nextInt(6) + 1, rand.nextInt(6) + 1};

        //sort both rolls by descending order
        Arrays.sort(attackDice);
        reverse(attackDice);
        Arrays.sort(defendDice);
        reverse(defendDice);

        //printing

        for (int i = 0; i < attackRolls; i++) {
            System.out.print(attackDice[i] + " ");
        }
        System.out.println("");
        for (int j = 0; j < defendRolls; j++) {
            System.out.print(defendDice[j] + " ");
        }
        System.out.println("");

        int attackLost = 0;
        int defendLost = 0;

        for (int i = 0; i < attackRolls; i++) {
            for (int j = i; j < defendRolls; j++) {
                if (attackDice[i] > defendDice[j]) {
                    defendLost += 1;
                    break;
                } else {
                    attackLost += 1;
                    break;
                }
            }
        }
        //printing
        System.out.println("Attack Lost: "+attackLost + "\nDefending Lost: " + defendLost);
        return new int[]{attackLost,defendLost};
    }

    //testing
    public static void main(String[] args) {
        Player p1 = new Player("");
        p1.attack(3,2);
    }
}
