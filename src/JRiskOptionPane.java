import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains implementations for popup dialogs.
 * These are just examples.
 * @deprecated
 */
public class JRiskOptionPane {

    public static int ONE_DICE = 0;
    public static int TWO_DIE = 1;
    public static int THREE_DIE = 2;


    public static final Object[] dieRollOptions = {
            "One", "Two", "Three"
    };

    /**
     * Shows a dialog prompting the user for input of dice rolls.
     * Gives the user options from the lower bound to the higher boud.
     * ex: lowerBound=1, higherBound=3 (select a die count from 1 to 3).
     *
     * @param frame The parent frame
     * @param player The player whose input made this dialog appear
     * @param lowerBound The lower bound of the dice rolls
     * @param upperBound The higher bound of the dice rolls
     * @return The option the user chose (corresponds to constants above)
     */
    public static int showDieCountDialog(JFrame frame,Player player, int lowerBound, int upperBound) {

        //get the exact number of options the user has
        List<Object> op = Arrays.asList(dieRollOptions);
        Object[] options = op.subList(lowerBound-1,upperBound).toArray();
        int middle = options.length-1;

        int input = JOptionPane.showOptionDialog(frame,
                "How many die will you roll?",
                player.getName()+"'s die count",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,options[middle]);
        System.out.println(input);
        return input;
    }

    public static int showFortifyInputDialog(JFrame frame,Player player,Territory territory1,Territory territory2,int minMove) {
        JFortifyInputDialog fort = new JFortifyInputDialog(frame)
                .setPlayer(player)
                .setTerritories(territory1,territory2)
                .setMinimumMove(minMove);
        int[] r = fort.showInputDialog();
        System.out.println(r[0]+":"+r[1]);
        return 1;
    }


    public static void main(String[] args) {
        //int r = JRiskInputPane.showDieCountDialog(null,new Player("Tony","RED"),1,3);
        Player p1 = new Player("Tony","RED");
        Territory t1 = new Territory("EARTH");
        t1.setUnits(500);
        Territory t2 = new Territory("MARS");
        t2.setUnits(10);
        //System.out.println(showFortifyInputDialog(null,p1,t1,t2,3));
        showDieCountDialog(null,p1,1,3);
    }
}
