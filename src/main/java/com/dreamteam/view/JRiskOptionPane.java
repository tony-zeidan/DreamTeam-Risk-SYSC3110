package com.dreamteam.view;

import com.dreamteam.core.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains implementations for popup dialogs.
 *
 * @author Kyler Verge
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 */
public class JRiskOptionPane {

    /**
     * The options the user could input while using a die count dialog.
     */
    public static final Object[] dieRollOptions = {
            "One", "Two", "Three"
    };

    /**
     * Shows a dialog prompting the user for input of dice rolls.
     * Gives the user options from the lower bound to the higher boud.
     * ex: lowerBound=1, higherBound=3 (select a die count from 1 to 3).
     *
     * @param frame       The parent frame
     * @param player      The player whose input made this dialog appear
     * @param lowerBound  The lower bound of the dice rolls
     * @param upperBound  The higher bound of the dice rolls
     * @param description String the description of what territory is attacking who, and whether it is a defense or
     *                    attack roll
     * @return The option the user chose (corresponds to constants above)
     */
    public static int showDieCountDialog(JFrame frame, Player player, int lowerBound, int upperBound, String description) {

        //get the exact number of options the user has
        List<Object> op = Arrays.asList(dieRollOptions);
        Object[] options = op.subList(lowerBound - 1, upperBound).toArray();

        int input = JOptionPane.showOptionDialog(frame,
                description + " How many die will you roll?",
                player.getName() + "'s die count",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options, options[0]);
        return input + 1;
    }

    /**
     * Basic JOptionPane implementation of the dialog that will be showed
     * when a user commences a fortification.
     *
     * @param frame       The parent frame
     * @param player      The player who commenced this dialog
     * @param moving      The territory that will be losing units
     * @param destination The territory that will be gaining units
     * @param minMove     The minimum amount of units the player has to move
     * @return The user's input to the dialog (number of units)
     */
    public static int showFortifyInput(JFrame frame, Player player, Territory moving, Territory destination, int minMove) {

        Object[] options = new Object[moving.getUnits() - minMove];
        int index = 0;
        for (int i = minMove; i < moving.getUnits() - minMove; i++) {
            options[index] = String.valueOf(i);
            index++;
        }

        try {
            return Integer.parseInt(((String) JOptionPane.showInputDialog(
                    frame, player.getName() + ", how many units will you move?",
                    String.format("FORTIFY: %s to %s [%s]", moving.getName(), destination.getName(), player.getName()),
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, String.valueOf(minMove)
            )));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * A custom implementation of a dialog that will be showed when a user
     * commences a fortification.
     * This implementation uses the JDialog API.
     * Features highly custom components.
     *
     * @param frame       The parent frame
     * @param player      The player who commenced the dialog
     * @param moving      The territory that will lose units
     * @param destination The territory that will gain units
     * @param minMove     The minimum amount of units the player has to move
     * @param cancelable  Determines if the user can cancel this dialog
     * @return The user's input (number of units or -1)
     */
    public static int showFortifyInputDialog(JFrame frame, Player player, Territory moving, Territory destination, int minMove, boolean cancelable) {
        JFortifyInputDialog fort = new JFortifyInputDialog(frame)
                .setPlayer(player)
                .setTerritories(moving, destination)
                .setMinimumMove(minMove)
                .setCancellable(cancelable);
        int r = fort.showInputDialog();
        return r;
    }

    /**
     * Shows a custom dialog of the requesting for a player's name in the game
     * of risk.
     *
     * @param frame      The parent frame
     * @param playerNum  This players number
     * @param playerType The players type format
     * @return The user's input
     */
    public static String showPlayerNameDialog(JFrame frame, int playerNum, String playerType) {
        String[] options = {"OK"};
        JPanel panel = new JPanel();
        JLabel lbl = new JLabel(String.format("%s %s's Name: ", playerType, playerNum));
        JTextField txt = new JTextField(10);
        txt.requestFocus();
        panel.add(lbl);
        panel.add(txt);
        int selectedOption = JOptionPane.showOptionDialog(frame, panel, String.format("%s Name", playerType), JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selectedOption == JOptionPane.OK_OPTION) {
            return txt.getText();
        } else {
            System.exit(0);
        }
        return null;
    }
}
