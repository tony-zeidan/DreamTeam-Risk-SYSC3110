package main.view;


import main.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
/**
 * This class represents a GUI for the game risk, including the feature of
 *  an interactive map.
 *
 * @author Tony Zeidan
 */
public class RiskFrame extends JFrame implements RiskGameView,ActionListener {
    private GameSingleton riskModel;
    private JLabel playerTurnLbl;
    private RiskEventPane eventPane;
    /**
     * Stores the territory clicked on by the user.
     * @see RiskController
     */
    private Territory selectedTerritory;

    /**
     * Stores the action selected by the user.
     * @see RiskController
     */
    private int selectedAction;

    /**
     * JPanel containing the game board (the map).
     */
    private RiskMapPane board;

    /**
     * The button for attacking. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton attack;

    /**
     * The button for ending turn. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton endTurn;

    /**
     * Constructor for instances of main.view.RiskFrame, constructs a new GUI.
     *
     * NEEDS ALTERING FOR COMMUNICATION WITH MODEL AND
     * CONTROLLER
     */
    public RiskFrame() {
        super("RISK");
        //TODO: Call GameSingleton.getGameInstance() instead
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

        RiskController rc = new RiskController(riskModel,this);
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
        JPanel buttonPane = new JPanel(new GridLayout(2,1));
        attack = new JButton("Attack");
        endTurn = new JButton("End Turn");
        attack.addActionListener(rc);
        endTurn.addActionListener(rc);
        buttonPane.add(attack);
        buttonPane.add(endTurn);

        board = new RiskMapPane(rc);
        eventPane = new RiskEventPane();
        riskModel.addHandler(eventPane);

        //add everything to the main content pane
        getContentPane().add(BorderLayout.CENTER,board);
        getContentPane().add(BorderLayout.SOUTH,buttonPane);
        getContentPane().add(BorderLayout.WEST,eventPane);

        //make the program terminate when frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set size of frame
        setSize(new Dimension(1200,800));

        //just to make sure everything has been reset for the start of the game
        restoreGUI();
        //prepare
        //pack();
    }

    //TODO
    /**
     *
     * @return
     */
    public Territory getSelectedTerritory() {
        return selectedTerritory;
    }

    //TODO
    /**
     *
     * @return
     */
    public int getSelectedAction() {
        return selectedAction;
    }

    //TODO
    /**
     *
     * @param selectedAction
     */
    public void setSelectedAction(int selectedAction) {
        this.selectedAction = selectedAction;
    }

    /**
     * Enables or disables the Attack Button
     * @param enabled
     */
    public void setAttackable(boolean enabled) {
        attack.setEnabled(enabled);
    }

    /**
     * Enables or disables the End Turn Button
     * @param enabled
     */
    public void setEndable(boolean enabled) {
        endTurn.setEnabled(enabled);
    }

    //TODO
    /**
     *
     * @return
     */
    public Map<Territory,Point> getPointsToPaint() {
        return board.getPointsToPaint();
    }

    //TODO
    /**
     *
     * @param color
     * @return
     */
    private static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    public void showFrame() {
        setResizable(true);
        setVisible(true);
    }

    //TODO
    /**
     *
     * @return
     */
    private int getNumOfPlayers()
    {
        String input;
        int numOfPlayers = 0;
        Object[] options = {"2", "3", "4", "5", "6"};
        input  = (String) JOptionPane.showInputDialog(this,"How many players?","Number of Players",
                JOptionPane.QUESTION_MESSAGE,null,options,"2");
        //User pressed close or cancel
        if(input == null){
            System.exit(0);
        }
        return numOfPlayers = Integer.parseInt(input);
    }

    //TODO
    /**
     *
     * @param numPlayers
     * @return
     */
    private List<Player> getPlayers(int numPlayers)
    {
        ArrayList<Player> players = new ArrayList<>();
        for(int i= 0; i<numPlayers;i++) {
            String input = null;
            while(input == null || input.length() == 0){
                input = JRiskOptionPane.showPlayerNameDialog(this,i+1);
            }
            players.add(new Player(input));
        }
        return players;
    }

    /**
     * We need some sort of updating methods that will do the following.
     *
     * 1) update the lists when a territory is selected
     */

    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }

    //TODO
    /**
     *
     * @param info
     */
    public void setCurrentInstruction(String info) {
        eventPane.setCurrentInstruction(info);
    }

    //TODO
    /**
     *
     * @param territory
     */
    public void setInfoDisplay(Territory territory) {
        Player p = territory.getOwner();
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.setInfoDisplay(p,territory);
    }
    public double getScalingX()
    {
        return board.getScalingX();
    }
    public double getScalingY()
    {
        return board.getScalingY();
    }
    //TODO
    /**
     *
     */
    public void restoreGUI() {
        selectedAction = -1;
        selectedTerritory = null;
        attack.setText("Attack");
        attack.setEnabled(false);
        endTurn.setEnabled(true);
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.setCurrentInstruction(RiskEventPane.DEFAULT_INSTRUCTION);
        //TODO: add restore seperate panels
    }

    //TODO
    /**
     *
     * @param selectedTerritory
     */
    public void setSelectedTerritory(Territory selectedTerritory) {
        this.selectedTerritory = selectedTerritory;
    }

    //TODO
    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JRadioButtonMenuItem fs = (JRadioButtonMenuItem) e.getSource();
        dispose();
        if (fs.isSelected()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setPreferredSize(new Dimension(1200,800));
        }
        setUndecorated(fs.isSelected());
        setVisible(true);
    }

    //TODO
    /**
     *
     * @param e
     */
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        RiskEventType eventType = e.getType();
        Object[] info = e.getEventInfo();
        //if (eventDescriptions.getSize()==25) eventDescriptions.clear();

        System.out.println(eventType);
        //TODO: only tell game board to repaint when necessary
        switch (eventType) {

            case UPDATE_MAP:
                //for selecting on our map we need a reference
                board.setPointsToPaint((HashMap<Territory,Point>)info[0]);
                board.repaint();
                board.revalidate();
                break;
            case GAME_OVER:
                break;
            case TURN_BEGAN:
                Player beganPlayer = (Player) info[0];
                Color playerColour = beganPlayer.getColour().getValue();

                playerTurnLbl.setText("it is : "+beganPlayer.getName()+"'s turn.        ");
                playerTurnLbl.setBackground(playerColour);
                playerTurnLbl.setForeground(getContrastColor(playerColour));
                break;
            default:
                return;
        }
    }
}