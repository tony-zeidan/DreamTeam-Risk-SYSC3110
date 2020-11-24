package com.dreamteam.view;

import com.dreamteam.core.Player;
import com.dreamteam.core.Territory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class represents a dialog that will be shown in the game of RISK when
 * a player attempts to fortify (move units) or after a successful attack.
 * <p>
 * This is a very custom dialog that required the use of the JDialog api,
 * instead of the commonly used JOptionPane.
 *
 * @author Tony Zeidan
 * @author Anthony Dooley
 * @author Ethan Chase
 * @author Kyler Verge
 */
public class JFortifyInputDialog extends JDialog implements ActionListener, ChangeListener {

    /**
     * The initial amount of units stored in the moving territory.
     */
    private int initialUnits1;

    /**
     * The initial amount of units stored in the destination territory.
     */
    private int initialUnits2;

    /**
     * Label for containing the text of units in moving territory (after move).
     */
    private JLabel territoryUnits1;

    /**
     * Label for containing the text of units in destination territory (after move).
     */
    private JLabel territoryUnits2;

    /**
     * Text field for shortcutting slider.
     */
    private JTextField shortcutUnits;

    /**
     * Slider model for the JSlider representing moved units.
     */
    private BoundedRangeModel sliderModel;

    /**
     * Stores the player that made the dialog appear.
     */
    private Player player;

    /**
     * The minimum amount of units the player has to move (default set to 1).
     */
    private int minimumMove;

    /**
     * The territory object, whose units will move after this dialog completes.
     */
    private Territory moving;

    /**
     * The territory object, who will receive units after this dialog completes.
     */
    private Territory destination;

    /**
     * Stores the value that the user selected.
     */
    private int selectedValue;

    /**
     * Stores whether the player is able to use the command option.
     */
    private boolean canCancel;

    /**
     * Constructor for instances of main.com.dreamteam.view.JFortifyInputDialog.
     * Creates a new dialog whose parent frame is the one specified.
     *
     * @param frame The parent frame
     */
    public JFortifyInputDialog(JFrame frame) {
        super(frame, true);
        minimumMove = 1;
        moving = null;
        destination = null;
        sliderModel = null;
        shortcutUnits = null;
        territoryUnits1 = null;
        territoryUnits2 = null;
        initialUnits1 = -1;
        initialUnits2 = -1;
        selectedValue = -1;
        canCancel = true;
    }

    /**
     * Sets the territories that will act as the origin and destination of this move.
     *
     * @param moving      The territory losing units
     * @param destination The territory gaining units
     * @return The same instance of the class
     */
    public JFortifyInputDialog setTerritories(Territory moving, Territory destination) {
        this.moving = moving;
        initialUnits1 = moving.getUnits();
        this.destination = destination;
        initialUnits2 = destination.getUnits();
        return this;
    }

    /**
     * Sets the minimum amount of units for this move.
     *
     * @param minMove The minimum units
     * @return The same instance of the class
     */
    public JFortifyInputDialog setMinimumMove(int minMove) {
        this.minimumMove = minMove;
        return this;
    }

    /**
     * Sets the player who commenced this dialog.
     *
     * @param player The fortifying player
     * @return The same instance of the class.
     */
    public JFortifyInputDialog setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Sets whether the dialog can be cancelled.
     *
     * @param cancel Whether the dialog will be cancellable
     * @return The same instance of the class
     */
    public JFortifyInputDialog setCancellable(boolean cancel) {
        canCancel = cancel;
        return this;
    }

    /**
     * Sets up all of the components that will be used in the dialog, according to
     * the values that should have already been set.
     */
    private void composeDialog() {

        //check for
        if (moving == null) {
            throw new RuntimeException("Risk Fortify Dialog: No moving territory set");
        } else if (destination == null) {
            throw new RuntimeException("Risk Fortify Dialog: No destination territory set");
        } else if (player == null) {
            //set the title of the dialog
            setTitle(String.format("FORTIFY: %s to %s", moving.getName(), destination.getName()));
        } else {
            //set the title of the dialog
            setTitle(String.format("FORTIFY: %s to %s [%s]", moving.getName(), destination.getName(), player.getName()));
        }

        //set the model of the slider
        sliderModel = new DefaultBoundedRangeModel(minimumMove, 1, minimumMove, initialUnits1);

        //initialize other fields
        initialUnits1 = moving.getUnits();
        initialUnits2 = destination.getUnits();
        territoryUnits1 = new JLabel(String.valueOf(initialUnits1 - minimumMove), SwingConstants.CENTER);
        territoryUnits2 = new JLabel(String.valueOf(initialUnits2), SwingConstants.CENTER);

        //initialize slider model
        sliderModel = new DefaultBoundedRangeModel(minimumMove, 1, minimumMove, initialUnits1);
        sliderModel.addChangeListener(this);

        //initialize and set preferences for slider
        JSlider unitsSlider = new JSlider(sliderModel);
        unitsSlider.setMajorTickSpacing(findMajorTick(minimumMove, initialUnits1));
        unitsSlider.setPaintTicks(true);
        unitsSlider.setPaintLabels(true);

        //initialize shortcut text field
        shortcutUnits = new JTextField(minimumMove);
        shortcutUnits.setToolTipText("input number of units to move");
        shortcutUnits.addActionListener(this);

        /*
        Middle panel of the border layout.
        This panel contains:
            1) a label at the top "moving:"
            2) a JSlider concerning the amount of units to move
            3) a button at the bottom "Move Units"
         */
        JPanel middlePanel = new JPanel(new BorderLayout());
        JButton move = new JButton("Move Units");
        move.addActionListener(this);
        JButton cancel = new JButton("Cancel");
        cancel.setEnabled(canCancel);
        cancel.addActionListener(this);

        JPanel bottomMiddlePanel = new JPanel(new GridLayout(1, 2));
        bottomMiddlePanel.add(move);
        bottomMiddlePanel.add(cancel);

        middlePanel.add(BorderLayout.NORTH, new JLabel("moving:", SwingConstants.CENTER));
        middlePanel.add(BorderLayout.CENTER, unitsSlider);
        middlePanel.add(BorderLayout.SOUTH, bottomMiddlePanel);

        /*
        Left panel of the border layout.
        This panel contains:
            1) a label containing the name of the first territory
            2) a label containing how many units were in the territory before this move
            3) a label containing the amount of units will be left after the move
         */
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(BorderLayout.NORTH, new JLabel(moving.getName() + "'s Units: "));
        JPanel leftSubPanel = new JPanel(new GridLayout(4, 1));
        leftSubPanel.add(new JLabel("    before move: "));
        leftSubPanel.add(new JLabel(String.valueOf(initialUnits1), SwingConstants.CENTER));
        leftSubPanel.add(new JLabel("    after move: "));
        leftSubPanel.add(territoryUnits1);
        leftPanel.add(BorderLayout.CENTER, leftSubPanel);
        leftPanel.add(BorderLayout.SOUTH, shortcutUnits);

        /*
        Right panel of the border layout.
        This panel contains:
            1) a label containing the name of the second territory
            2) a label containing how many units were in the territory before this move
            3) a label containing the amount of units will be left after the move
         */
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(BorderLayout.NORTH, new JLabel(destination.getName() + "'s Units: "));
        JPanel rightSubPanel = new JPanel(new GridLayout(4, 1));
        rightSubPanel.add(new JLabel("    before move: "));
        rightSubPanel.add(new JLabel(String.valueOf(initialUnits2), SwingConstants.CENTER));
        rightSubPanel.add(new JLabel("    after move: "));
        rightSubPanel.add(territoryUnits2);
        rightPanel.add(BorderLayout.CENTER, rightSubPanel);

        //border for customization
        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        leftPanel.setBorder(raisedEtched);
        rightPanel.setBorder(raisedEtched);

        //add items to content pane
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(BorderLayout.WEST, leftPanel);
        content.add(BorderLayout.CENTER, middlePanel);
        content.add(BorderLayout.EAST, rightPanel);

        //dialog preferences
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Calculates the space that should be in between each major tick.
     *
     * @param start  The start of the tick range
     * @param finish The end of the tick range
     * @return The spacing of each major tick from the start
     */
    private int findMajorTick(int start, int finish) {
        int t = finish - start;
        for (int i = t - 1; i >= 2; i--) {
            if (t % i == 0) return i;
        }
        return t / 2;
    }

    /**
     * Shows the composed dialog.
     * Retrieves the user inputs.
     *
     * @return An array of ints (size=2) in the form [selected closing operation, selected amount of units]
     */
    public int showInputDialog() {
        composeDialog();
        setVisible(true);
        return selectedValue;
    }

    /**
     * StateChangeListener implementation (only used for slider).
     * Sets the text of units in each territory according to the current slider value;
     *
     * @param e The event that was triggered
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        DefaultBoundedRangeModel source = (DefaultBoundedRangeModel) e.getSource();
        int value = sliderModel.getValue();
        territoryUnits1.setText(String.valueOf(initialUnits1 - value));
        territoryUnits2.setText(String.valueOf(initialUnits2 + value));
    }

    /**
     * General action listener for the shortcut text field and ok/cancel buttons
     *
     * @param e The event that was triggered
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JTextField) {
            try {
                int num = Integer.parseInt(shortcutUnits.getText());
                if (num < minimumMove) {
                    sliderModel.setValue(minimumMove);
                } else if (num > initialUnits1) {
                    sliderModel.setValue(initialUnits1);
                } else {
                    sliderModel.setValue(num);
                }
            } catch (NumberFormatException n) {
            }
        } else {
            JButton source = (JButton) e.getSource();
            if (source.getText().equals("Move Units")) {
                selectedValue = sliderModel.getValue();
            }
            close();
        }
    }

    /**
     * Disposes of the dialog.
     */
    private void close() {
        this.dispose();
    }
}
