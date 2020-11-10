package main.view;


import main.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class represents a GUI for the game risk, including the feature of
 * an interactive map.
 *
 * @author Tony Zeidan
 * @author Anthony Dooley
 * @author Kyler Verge
 * @author Ethan Chase
 */
public class RiskFrame extends JFrame implements RiskGameHandler, ActionListener {

    /**
     * The model for this view.
     */
    private GameSingleton riskModel;

    /**
     * Stores whose turn it is on the panel.
     */
    private JLabel playerTurnLbl;

    /**
     * Stores the territory clicked on by the user.
     *
     * @see RiskController
     */
    private Territory selectedTerritory;

    /**
     * Stores the action selected by the user.
     *
     * @see RiskController
     */
    private int selectedAction;

    /**
     * JPanel containing the game board (the map).
     */
    private RiskMapPane mapPane;

    private RiskEventPane eventPane;

    /**
     * The button for attacking. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton attackBtn;

    /**
     * The button for ending turn. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton endTurnBtn;

    /**
     * Constructor for instances of main.view.RiskFrame, constructs a new GUI.
     *
     * NEEDS ALTERING FOR COMMUNICATION WITH MODEL AND
     * CONTROLLER
     */
    public RiskFrame() {
        super("RISK");
        riskModel = GameSingleton.getGameInstance(getPlayers(getNumOfPlayers()));
        setLayout(new BorderLayout());
        selectedAction = -1;
        composeFrame();
        riskModel.setUpGame();
        showFrame();
    }

    /**
     * Generates and places all components on the frame, this should
     * generally only be called once per frame.
     */
    private void composeFrame() {

        RiskController rc = new RiskController(riskModel, this);
        riskModel.addHandler(this);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        JRadioButtonMenuItem fs = new JRadioButtonMenuItem("Fullscreen");
        fs.addActionListener(this);
        menu.add(fs);

        //create a massive separator in the menu bar
        menuBar.add(Box.createHorizontalGlue());
        playerTurnLbl = new JLabel();
        playerTurnLbl.setOpaque(true);
        menuBar.add(playerTurnLbl);    //we must update this with the players turn
        setJMenuBar(menuBar);

        /*
        This panel contains the buttons for the players turn.
            - we must enable them and disable them accordingly
         */
        JPanel buttonPane = new JPanel(new GridLayout(2, 1));
        attackBtn = new JButton("Attack");
        endTurnBtn = new JButton("End Turn");
        attackBtn.addActionListener(rc);
        endTurnBtn.addActionListener(rc);
        buttonPane.add(attackBtn);
        buttonPane.add(endTurnBtn);

        mapPane = new RiskMapPane(rc);
        eventPane = new RiskEventPane();
        riskModel.addHandler(mapPane);
        riskModel.addHandler(eventPane);

        //add everything to the main content pane
        getContentPane().add(BorderLayout.CENTER, mapPane);
        getContentPane().add(BorderLayout.SOUTH, buttonPane);
        getContentPane().add(BorderLayout.WEST, eventPane);

        //make the program terminate when frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set size of frame
        setSize(new Dimension(1200, 800));

        //just to make sure everything has been reset for the start of the game
        restoreGUI();
        //prepare
        //pack();
    }

    /**
     * Gets the selected territory
     *
     * @return The territory that was selected
     */
    public Territory getSelectedTerritory() {
        return selectedTerritory;
    }

    /**
     * Gets the action that was selected by the current player
     *
     * @return The number corresponding to the action
     */
    public int getSelectedAction() {
        return selectedAction;
    }

    /**
     * Sets the selected action
     *
     * @param selectedAction the number corresponding to the action
     */
    public void setSelectedAction(int selectedAction) {
        this.selectedAction = selectedAction;
    }

    /**
     * Enables or disables the Attack Button
     *
     * @param enabled Determines if the attack button will be enabled
     */
    public void setAttackable(boolean enabled) {
        attackBtn.setEnabled(enabled);
    }

    /**
     * Enables or disables the End Turn Button
     *
     * @param enabled Determines if the End Turn button is enabled
     */
    public void setEndable(boolean enabled) {
        endTurnBtn.setEnabled(enabled);
    }

    /**
     * Get the points on the map to update the colour
     * of their points
     *
     * @return The map with the updated points
     */
    public Map<Territory, Point> getPointsToPaint() {
        return mapPane.getPointsToPaint();
    }

    /**
     * Gets a contrasting color to be used for the colour display
     * for whose turn it is
     *
     * @param color The colour of the player
     * @return A contrasting colour
     */
    private static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    /**
     * Makes the games window visible and eligible to resize
     */
    public void showFrame() {
        setResizable(true);
        setVisible(true);
    }

    /**
     * Creates a Window to pop up asking how many players will be playing this game
     * only allows the user to pick between 2 - 6
     *
     * @return The number of players
     */
    private int getNumOfPlayers() {
        String input;
        Object[] options = {"2", "3", "4", "5", "6"};
        input = (String) JOptionPane.showInputDialog(this, "How many players?", "Number of Players",
                JOptionPane.QUESTION_MESSAGE, null, options, "2");
        //User pressed close or cancel
        if (input == null) {
            System.exit(0);
        }
        return Integer.parseInt(input);
    }

    /**
     * Gets a list of all the players currently playing the game
     *
     * @param numPlayers The number of players playing the game
     * @return List of all the players in the game
     */
    private List<Player> getPlayers(int numPlayers) {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            String input = null;
            while (input == null || input.length() == 0) {
                input = JRiskOptionPane.showPlayerNameDialog(this, i + 1);
            }
            players.add(new Player(input));
        }
        return players;
    }

    /**
     * We need some sort of updating methods that will do the following.
     * <p>
     * 1) update the lists when a territory is selected
     */


    /**
     * Updates the event pane to instruct the player of their choices
     *
     * @param info Message of the the players current instruction
     */
    public void setCurrentInstruction(String info) {
        eventPane.setCurrentInstruction(info);
    }

    /**
     * Displays the selected territories information in an event pane
     *
     * @param territory The selected territory
     */
    public void setInfoDisplay(Territory territory) {
        Player p = territory.getOwner();
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.setInfoDisplay(p, territory);
    }

    /**
     * getter for the scaling in the x-direction of the RiskMapPane JPanel
     * @return double
     */
    public double getScalingX() {
        return mapPane.getScalingX();
    }

    /**
     * getter for the scaling in the y-direction of the RiskMapPane JPanel
     * @return double
     */
    public double getScalingY() {
        return mapPane.getScalingY();
    }

    /**
     * Restores the GUI to its default state
     */
    public void restoreGUI() {
        selectedAction = -1;
        selectedTerritory = null;
        attackBtn.setText("Attack");
        attackBtn.setEnabled(false);
        endTurnBtn.setEnabled(true);
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.setCurrentInstruction(RiskEventPane.DEFAULT_INSTRUCTION);
        //TODO: add restore seperate panels
    }

    /**
     * Sets the selected territory to the most current selected territory
     *
     * @param selectedTerritory The territory that was currently selected
     */
    public void setSelectedTerritory(Territory selectedTerritory) {
        this.selectedTerritory = selectedTerritory;
    }

    /**
     * Maximizes and minimizes the
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JRadioButtonMenuItem fs = (JRadioButtonMenuItem) e.getSource();
        dispose();
        if (fs.isSelected()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setPreferredSize(new Dimension(1200, 800));
        }
        setUndecorated(fs.isSelected());
        setVisible(true);
    }

    /**
     * Handles any type of update made to the game.
     * Through either the game being over, a players turn
     * beginning, or updating what territory is attakable
     *
     * @param e The Risk Event
     */
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        RiskEventType eventType = e.getType();
        Object[] info = e.getEventInfo();
        //if (eventDescriptions.getSize()==25) eventDescriptions.clear();

        System.out.println(eventType);
        //TODO: only tell game board to repaint when necessary
        switch (eventType) {
            case GAME_OVER:
                JOptionPane alert = new JOptionPane();
                alert.showMessageDialog(this, "GAME OVER " + info[0] + " has won!!!");
                setAttackable(false);
                setEndable(false);
                break;
            case TURN_BEGAN:
                Player beganPlayer = (Player) info[0];
                Color playerColour = beganPlayer.getColour().getValue();

                playerTurnLbl.setText("it is : " + beganPlayer.getName() + "'s turn.        ");
                playerTurnLbl.setBackground(playerColour);
                playerTurnLbl.setForeground(getContrastColor(playerColour));
                break;
            case RESTORE_GUI:
                restoreGUI();
                break;
            case UPDATE_ATTACKABLE:
                setAttackable((boolean) info[0]);
            default:
                return;
        }
    }

    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }

}