package com.dreamteam.view;

import com.dreamteam.controller.RiskController;
import com.dreamteam.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
     * Stores whose turn it is on the panel.
     */
    private JLabel playerTurnLbl;
    /**
     * Contains the string representation of the players bonus units.
     */
    private JLabel playerBonusUnitsLbl;
    /**
     * Contains the string representation of the current game phase.
     */
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
    /**
     * Jbutton for end of turn movement.
     */
    private JButton moveUnitsBtn;
    /**
     * the current phase that the game is in.
     */
    private GamePhase gamePhase;
    /**
     * A single risk controller only designed for this frame.
     */
    private RiskController rc;

    /**
     * Constructor for instances of RiskFrame, constructs a new GUI.
     *
     * @param gs The model that the sub components of this frame are listening to
     * @param gameData The zipfile containing the map image (.save or .world)
     */
    public RiskFrame(GameSingleton gs,ZipFile gameData) {
        super("Dream Team RISK!");

        //TODO: player selection should be in the home screen (maybe)
        setLayout(new BorderLayout());
        rc = new RiskController(gs,this);

        importFrame(gs,gameData);

        gs.addHandler(this);
    }

    /**
     * Read the name map image and send it to the compose frame method.
     * @param gs The model that the sub components of this frame are listening to
     * @param zf The zipfile containing the map image (.save or .world)
     */
    private void importFrame(GameSingleton gs, ZipFile zf) {
        if (zf!=null) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ZipEntry imageEntry = zf.getEntry("map.png");
                InputStream in = zf.getInputStream(imageEntry);
                BufferedImage image = ImageIO.read(in);

                composeFrame(gs,image);

                in.close();
                out.close();

            } catch (IOException e) {
                System.out.println("There was an error while parsing.");
            }
        }
    }

    /**
     * Generates and places all components on the frame, this should
     * generally only be called once per frame.
     */
    private void composeFrame(GameSingleton gs, Image mapImage) {

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        JRadioButtonMenuItem fs = new JRadioButtonMenuItem("Fullscreen");
        fs.addActionListener(rc);
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.setActionCommand("S");
        saveGameItem.addActionListener(rc);
        JMenuItem exitGameItem = new JMenuItem("Main Menu");
        exitGameItem.setActionCommand("E");
        exitGameItem.addActionListener(rc);
        menu.add(fs);
        menu.add(saveGameItem);
        menu.add(exitGameItem);

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
        moveUnitsBtn.setActionCommand("M");
        endTurnBtn.setActionCommand("E");
        buttonPane.add(attackBtn);
        buttonPane.add(moveUnitsBtn);
        buttonPane.add(endTurnBtn);

        mapPane = new RiskMapPane(mapImage,rc);
        eventPane = new RiskEventPane();
        gs.addHandler(mapPane);
        gs.addHandler(eventPane);

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
        Object[] options = {"1", "2", "3", "4", "5", "6"};
        input = (String) JOptionPane.showInputDialog(this, "How many human players?", "Number of Human Players",
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
                input = JRiskOptionPane.showPlayerNameDialog(this, i + 1, "Player");
            }
            players.add(new Player(input));
        }
        return players;
    }
    //refactor

    /**
     * gets the number of AIs playing the game
     *
     * @param numSpotsTaken, int determining the max of AIs to be allowed
     * @return the number of AIs chosen
     */
    private int numOfAIs(int numSpotsTaken) {
        String[] options;
        if (numSpotsTaken == 1) {
            options = new String[]{"1", "2", "3", "4", "5"};
        } else {
            options = new String[7 - numSpotsTaken];
            for (int i = 0; i < 7 - numSpotsTaken; i++) {
                options[i] = Integer.toString((i));
            }
        }
        String input = (String) JOptionPane.showInputDialog(this, "How many AI Players?", "Number of AI Players",
                JOptionPane.QUESTION_MESSAGE, null, options, "2");
        //User pressed close or cancel
        if (input == null) {
            System.exit(0);
        }
        return Integer.parseInt(input);
    }

    /**
     * Adds a number of AI to the players list
     *
     * @param numAIs  number of AIs to add
     * @param players the list of players AIs are added to
     */
    private void addAIsToList(int numAIs, List<Player> players) {
        for (int i = 0; i < numAIs; i++) {
            String input = null;
            while (input == null || input.length() == 0) {
                input = JRiskOptionPane.showPlayerNameDialog(this, i + 1, "AI");
            }
            players.add(new AIPlayer(input));
        }
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
     *
     * @return double
     */
    public double getScalingX() {
        return mapPane.getScalingX();
    }

    /**
     * getter for the scaling in the y-direction of the RiskMapPane JPanel
     *
     * @return double
     */
    public double getScalingY() {
        return mapPane.getScalingY();
    }

    /**
     * Retrieves the image of the current map in this frame.
     *
     * @return The image of the current map in the frame.
     */
    public Image getMapImage() {
        return mapPane.getImage();
    }

    /**
     * gets the number of units to be added
     *
     * @return int the number of units that need to be added
     */
    public int getBonusUnits() {
        try {
            return Integer.parseInt(playerBonusUnitsLbl.getName());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * sets the lbl to the number of bonus troops that need to be added
     *
     * @param units amount of troops
     */
    public void setBonusUnits(int units) {
        playerBonusUnitsLbl.setText("Bonus Units: " + units);
        playerBonusUnitsLbl.setName(units + "");
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

    /**
     * Retrieves the current phase the gui is in right now.
     *
     * @return The current gui phase
     */
    public GamePhase getPhase() {
        return gamePhase;
    }

    /**
     * Handles any type of update made to the game.
     * Through either the game being over, a players turn
     * beginning, or updating what territory is attakable
     *
     * @param e The Risk Event that was triggered
     */
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        RiskEventType eventType = e.getType();
        Object[] info = e.getEventInfo();

        switch (eventType) {
            case GAME_OVER:
                JOptionPane alert = new JOptionPane();
                alert.showMessageDialog(this, "GAME OVER " + info[0] + " has won!!!");
                setAttackable(false);
                setEndable(false);
                break;
            case GAME_BEGAN:
                this.gamePhase = GamePhase.START_GAME;
                showFrame();
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
                playerTurnLbl.setIcon(beganPlayer.getAvatar());
                break;
            case UPDATE_ATTACKABLE:
                if (gamePhase == GamePhase.ATTACK) {
                    setAttackable((boolean) info[0]);
                } else if (gamePhase == GamePhase.MOVE_UNITS) {
                    moveUnitsBtn.setEnabled((boolean) info[0]);
                }
                break;
            case PHASE_CHANGE:
                this.gamePhase = (GamePhase) info[0];
                gamePhaseLbl.setText(gamePhase.toString());
                gamePhaseLbl.setBackground(gamePhase.getColour());
                gamePhaseLbl.setForeground(getContrastColor(gamePhase.getColour()));
                restoreGUI();
                break;
            case SELECT_ATTACK_DIE:
                Territory attacking = (Territory) info[0];
                Player currentPlayer = attacking.getOwner();
                Territory defending = (Territory) info[1];
                int maxAttack = (Integer) info[2];
                int diceAmount = -1;
                while (diceAmount<=0) {
                    diceAmount = JRiskOptionPane.showDieCountDialog(this, currentPlayer,
                            1, maxAttack, "You are attacking " + defending.getName() + " from " + attacking.getName() + ".");
                }
                currentPlayer.setDiceRoll(diceAmount);
                AudioPlayer.playSound("btnPress");
                break;
            case SELECT_DEFEND_DIE:
                attacking = (Territory) info[0];
                defending = (Territory) info[1];
                currentPlayer = defending.getOwner();
                int maxDice = (Integer) info[2];
                diceAmount = -1;
                while (diceAmount<=0) {
                    diceAmount = JRiskOptionPane.showDieCountDialog(this, currentPlayer, 1,
                            maxDice, "You are defending " + defending.getName() + " from " + attacking.getName() + ".");
                }
                currentPlayer.setDiceRoll(diceAmount);
                AudioPlayer.playSound("btnPress");
                break;
            case INVALID_MAP_LOAD:
                //popup dialog and send back to home screen
                JOptionPane.showMessageDialog(this,"You loaded an invalid map!","INVALID MAP LOAD",JOptionPane.ERROR_MESSAGE);
                System.exit(0); //simply discontinue the program
        }
    }

    /**
     * restores button to default of attacking
     */
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
    }

    /**
     * restores default buttons in end of turn movement
     */
    private void restoreMoveUnits() {
        attackBtn.setEnabled(false);
        attackBtn.setActionCommand("NULL");
        attackBtn.setText("Attack");
        moveUnitsBtn.setEnabled(false);
        moveUnitsBtn.setActionCommand("M");
        moveUnitsBtn.setText("Move Units");
        endTurnBtn.setEnabled(true);
        endTurnBtn.setActionCommand("S");
        endTurnBtn.setText("Skip");
        setBonusUnits(-1);
        playerBonusUnitsLbl.setVisible(false);
    }

    /**
     * restores button default in troop placement
     */
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
    }


    /**
     * Main method for this com.dreamteam.view. (Testing)
     *
     * @param args n/a
     */
    public static void main(String[] args) {
        //RiskFrame rf = new RiskFrame();
    }

}