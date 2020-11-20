package main.view;
import main.core.*;

import javax.swing.*;
import java.awt.*;
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
public class RiskFrame extends JFrame implements RiskGameHandler {

    /**
     * The model for this view.
     */
    private GameSingleton riskModel;
    /**
     * Stores whose turn it is on the panel.
     */
    private JLabel playerTurnLbl;

    private JLabel playerBonusUnitsLbl;

    private JLabel gamePhaseLbl;

    /**
     * JPanel containing the game board (the map).
     */
    private RiskMapPane mapPane;
    /**
     * JPanel containing the in-game event, and selected territory display.
     */
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

    private JButton moveUnitsBtn;

    private GamePhase gamePhase;

    /**
     * Constructor for instances of RiskFrame, constructs a new GUI.
     */
    public RiskFrame() {
        super("RISK");
        riskModel = GameSingleton.getGameInstance(getPlayers(getNumOfPlayers()));
        setLayout(new BorderLayout());
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
        fs.addActionListener(rc);
        menu.add(fs);

        //create a massive separator in the menu bar
        gamePhaseLbl = new JLabel();
        gamePhaseLbl.setOpaque(true);
        menuBar.add(gamePhaseLbl);
        menuBar.add(Box.createHorizontalGlue());
        playerBonusUnitsLbl = new JLabel();
        playerBonusUnitsLbl.setOpaque(true);
        menuBar.add(playerBonusUnitsLbl);
        menuBar.add(Box.createHorizontalGlue());
        playerTurnLbl = new JLabel();
        playerTurnLbl.setOpaque(true);
        menuBar.add(playerTurnLbl);    //we must update this with the players turn
        setJMenuBar(menuBar);

        /*
        This panel contains the buttons for the players turn.
            - we must enable them and disable them accordingly
         */
        JPanel buttonPane = new JPanel(new GridLayout(3, 1));
        attackBtn = new JButton("Attack");
        moveUnitsBtn = new JButton("Move Units");
        endTurnBtn = new JButton("End Turn");
        attackBtn.addActionListener(rc);
        moveUnitsBtn.addActionListener(rc);
        endTurnBtn.addActionListener(rc);
        attackBtn.setActionCommand("A");
        moveUnitsBtn.setActionCommand("MU");
        endTurnBtn.setActionCommand("E");
        buttonPane.add(attackBtn);
        buttonPane.add(moveUnitsBtn);
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
        //restoreGUI();
        //prepare
        //pack();
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

    public int getBonusUnits() {
        try {
            return Integer.parseInt(playerBonusUnitsLbl.getName());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setBonusUnits(int units) {
        playerBonusUnitsLbl.setText("Bonus Units: " + units);
        playerBonusUnitsLbl.setName(units+"");
    }

    /**
     * Restores the GUI to its default state
     */
    public void restoreGUI() {
        switch (gamePhase) {
            case BONUS_TROUPE:
                restoreBonusTroupe();
                break;
            case ATTACK:
                restoreAttack();
                break;
            case MOVE_UNITS:
                restoreMoveUnits();
                break;
        }
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.setCurrentInstruction(RiskEventPane.DEFAULT_INSTRUCTION);
    }

    public GamePhase getPhase() {
        return gamePhase;
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

        System.out.println(eventType);


        switch (eventType) {
            case GAME_OVER:
                JOptionPane alert = new JOptionPane();
                alert.showMessageDialog(this, "GAME OVER " + info[0] + " has won!!!");
                setAttackable(false);
                setEndable(false);
                break;
            case TURN_BEGAN:
                restoreGUI();
                Player beganPlayer = (Player) info[0];
                int bonusUnits = (int) info[1];

                Color playerColour = beganPlayer.getColour().getValue();
                setBonusUnits(bonusUnits);
                playerBonusUnitsLbl.setBackground(playerColour);
                playerBonusUnitsLbl.setForeground(getContrastColor(playerColour));
                playerTurnLbl.setText("it is : " + beganPlayer.getName() + "'s turn.        ");
                playerTurnLbl.setBackground(playerColour);
                playerTurnLbl.setForeground(getContrastColor(playerColour));
                break;
            case UPDATE_ATTACKABLE:
                if (gamePhase==GamePhase.ATTACK) {
                    setAttackable((boolean) info[0]);
                } else if (gamePhase==GamePhase.MOVE_UNITS) {
                    moveUnitsBtn.setEnabled((boolean) info[0]);
                }
                break;
            case PHASE_CHANGE:
                this.gamePhase = (GamePhase) info[0];
                //TODO: reset bonus units here
                if (gamePhase==GamePhase.BONUS_TROUPE) {

                }
                restoreGUI();
                System.out.println("Current Phase: " + gamePhase);
                break;
        }
    }

    private void restoreAttack() {
        attackBtn.setEnabled(false);
        attackBtn.setActionCommand("A");
        attackBtn.setText("Attack");
        moveUnitsBtn.setEnabled(false);
        moveUnitsBtn.setActionCommand("NULL");
        moveUnitsBtn.setText("Move Units");
        endTurnBtn.setEnabled(true);
        endTurnBtn.setActionCommand("E");
        endTurnBtn.setText("End Turn");
        setBonusUnits(-1);
        playerBonusUnitsLbl.setVisible(false);
        gamePhaseLbl.setBackground(Color.RED);
        gamePhaseLbl.setForeground(getContrastColor(Color.RED));
    }

    private void restoreMoveUnits() {
        attackBtn.setEnabled(false);
        attackBtn.setActionCommand("NULL");
        attackBtn.setText("Attack");
        moveUnitsBtn.setEnabled(false);
        moveUnitsBtn.setActionCommand("M");
        endTurnBtn.setEnabled(true);
        endTurnBtn.setActionCommand("S");
        endTurnBtn.setText("Skip");
        setBonusUnits(-1);
        playerBonusUnitsLbl.setVisible(false);
        gamePhaseLbl.setBackground(Color.BLUE);
        gamePhaseLbl.setForeground(getContrastColor(Color.BLUE));
    }

    private void restoreBonusTroupe() {
        attackBtn.setEnabled(false);
        attackBtn.setActionCommand("NULL");
        attackBtn.setText("Attack");
        moveUnitsBtn.setEnabled(false);
        moveUnitsBtn.setActionCommand("NULL");
        endTurnBtn.setEnabled(false);
        endTurnBtn.setActionCommand("NULL");
        endTurnBtn.setText("End Turn");
        playerBonusUnitsLbl.setVisible(true);
        gamePhaseLbl.setBackground(Color.GREEN);
        gamePhaseLbl.setForeground(getContrastColor(Color.GREEN));
    }



    /**
     * Main method for this view. (Testing)
     * @param args n/a
     */
    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }

}