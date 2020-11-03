package main.view;


import main.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class represents a GUI for the game risk, including the feature of
 *  an interactive map.
 *
 * @author Tony Zeidan
 */
public class RiskFrame extends JFrame implements RiskGameView {

    private Game riskModel;
    private JLabel playerTurnLbl;
    private DefaultListModel<String> eventDescriptions;

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
     * Stores the points that will be painted on the map.
     * It is altered constantly depending on user inputs.
     */
    private Map<Territory,Point> pointsToPaint;

    /**
     * This is the model for the table that contains the information
     * of the users selected territory.
     */
    private DefaultTableModel infoModel;

    /**
     * The text area containing instructions for the user.
     */
    private JTextArea instructionsText;

    /**
     * JPanel containing the game board (the map).
     */
    private JPanel board;

    /**
     * The button for attacking. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton attack;

    /**
     * Constructor for instances of main.view.RiskFrame, constructs a new GUI.
     *
     * NEEDS ALTERING FOR COMMUNICATION WITH MODEL AND
     * CONTROLLER
     */
    public RiskFrame() {
        super("RISK");

        int numPlayers= getNumOfPlayers();
        ArrayList<String> playerName = getPlayerNames(numPlayers);
        riskModel = new Game(numPlayers, playerName);
        riskModel.makeView(this);
        pointsToPaint = riskModel.getAllCoordinates();

        board=null;
        eventDescriptions = new DefaultListModel<>();
        infoModel = new DefaultTableModel();
        infoModel.addColumn("Type");
        infoModel.addColumn("Value");
        setLayout(new BorderLayout());
        selectedAction = -1;
        composeFrame();
        showFrame();
    }

    /**
     * Generates and places all components on the frame, this should
     * generally only be called once per frame.
     */
    private void composeFrame() {

        RiskController rc = new RiskController(riskModel,this);

        //attempt to read the map file
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(getClass().getResource("/resources/RiskBoard.png"));
        } catch (IOException ioException) {
            System.out.println("RISK Board Load Failed");
            ioException.printStackTrace();
        }

        Image finalMapImage=mapImage;
        board = new JPanel() {

            /**
             * Paints the JPanel component with the given graphics.
             * It also uses the given graphics instance to draw a scaled version of
             * the image on the panel, along with the points representing territories and
             * the labels that go with them.
             *
             * @param g The dedicated graphics for this panel
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                board.removeAll();  //clears the labels off of the board

                //draws the scaled version of the map image
                g.drawImage(finalMapImage.getScaledInstance(getWidth(),getHeight(),
                        Image.SCALE_SMOOTH), 0, 0, null);

                paintPoints(g);     //paint points representing territories
                placePointLabels();     //paint the labels to go with the points
            }
        };
        board.addMouseListener(rc);
        board.setLayout(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        //create a massive seperator in the menu bar
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
        JButton endTurn = new JButton("End Turn");
        attack.addActionListener(rc);
        endTurn.addActionListener(rc);
        buttonPane.add(attack);
        buttonPane.add(endTurn);

        /*
        This panel (eventPane) is responsible for showing the events that occurred
        during the game and individual territory information.
        It contains two main sub panels:
            1) topSubEventPane is responsible for displaying individual territory information
                either when the player clicks on a territory on the map, or when they select one
                from the drop down list.
             2) middleSubEventPane is responsible for showing a list of the events that have
                occurred during the game, perhaps through the use of a RiskGameEvent.toString()
             3) bottomSubEventPane is responsible for showing instructions to the user.
         */
        JPanel eventPane = new JPanel(new GridLayout(3,1));
        JPanel topSubEventPane = new JPanel(new BorderLayout());
        JPanel middleSubEventPane = new JPanel(new BorderLayout());
        JPanel bottomSubEventPane = new JPanel(new BorderLayout());

        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

        /*
        The following is the scrolling table for territory information
        The table is only going to have three rows:
        1) Owner: main.core.Player/String
        2) Color: String
        2) Units : int/String
         */
        JTable infoTable = new JTable(infoModel); //TODO: add model to update table

        //combo box for selecting territory
        topSubEventPane.add(BorderLayout.NORTH,new JLabel("Selected Territory Information"));

        //add table to top sub pane
        topSubEventPane.add(BorderLayout.CENTER,infoTable);

        //the following is the list of in game events that occurred
        JList eventList = new JList(eventDescriptions);
        JScrollPane gameEventScroller = new JScrollPane(eventList);
        gameEventScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameEventScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //add list to the bottom sub pane
        middleSubEventPane.add(BorderLayout.NORTH,new JLabel("Game Events"));
        middleSubEventPane.add(BorderLayout.CENTER,gameEventScroller);
        bottomSubEventPane.add(BorderLayout.NORTH,new JLabel("Game Instructor"));
        instructionsText = new JTextArea();
        instructionsText.setEditable(false);
        instructionsText.setLineWrap(true);
        instructionsText.setWrapStyleWord(true);
        bottomSubEventPane.add(BorderLayout.CENTER,instructionsText);

        //set borders for both sub panes (to look seperate)
        topSubEventPane.setBorder(raisedEtched);
        middleSubEventPane.setBorder(raisedEtched);
        bottomSubEventPane.setBorder(raisedEtched);

        //add both sub panes to main event pane
        eventPane.add(BorderLayout.NORTH,topSubEventPane);
        eventPane.add(BorderLayout.CENTER,middleSubEventPane);
        eventPane.add(BorderLayout.SOUTH,bottomSubEventPane);

        //set event pane size
        eventPane.setPreferredSize(new Dimension(200,800));

        //add everything to the main content pane
        getContentPane().add(BorderLayout.CENTER,board);
        getContentPane().add(BorderLayout.SOUTH,buttonPane);
        getContentPane().add(BorderLayout.WEST,eventPane);

        //make the program terminate when frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //just to make sure everything has been reset for the start of the game
        restoreGUI();

        //set size of frame
        setPreferredSize(new Dimension(1200,800));

        //prepare
        pack();
    }

    public Territory getSelectedTerritory() {
        return selectedTerritory;
    }


    public int getSelectedAction() {
        return selectedAction;
    }
    public void setSelectedAction(int selectedAction) {
        this.selectedAction = selectedAction;
    }

    public void setAttackable(boolean enabled) {
        attack.setEnabled(enabled);
    }

    public Map<Territory,Point> getPointsToPaint() {
        return pointsToPaint;
    }

    public void setPointsToPaint(Map<Territory,Point> pointsToPaint) {
        this.pointsToPaint=pointsToPaint;
        board.repaint();
        board.revalidate();
    }

    private static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    private void paintPoints(Graphics g) {
        if (pointsToPaint==null) return;
        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            Map<Territory,Point> neighbourNodes = riskModel.getNeighbouringNodes(t);
            for (Point p2 : neighbourNodes.values()) {
                g.drawLine(p2.x+6,p2.y+6,p.x+6,p.y+6);
            }
        }

        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            g.setColor(Color.BLACK);

            int x = (int) (p.getX());
            int y = (int) (p.getY());
            g.fillOval(x-2,y-2,16,16);
            g.setColor(riskModel.getTerritoryOwner(t).getColour());
            g.fillOval(x,  y, 12, 12);
            //g.setFont(new Font("Segoe UI",Font.PLAIN,10));
            //g.setColor(Color.WHITE);
            //g.drawString(t.getName(),x-25,y-10);
        }
    }

    public void placePointLabels() {
        if (pointsToPaint==null) return;
        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            int x = (int) (p.getX());
            int y = (int) (p.getY());
            JLabel lbl = new JLabel(t.getName());
            JLabel lbl2 = new JLabel(" " + String.valueOf(t.getUnits())+ " ");

            lbl.setFont(new Font("Segoe UI",Font.BOLD,9));
            lbl2.setFont(new Font("Segoe UI",Font.BOLD,11));

            Insets insets = board.getInsets();
            Dimension lblSize = lbl.getPreferredSize();
            Dimension lblSize2 = lbl2.getPreferredSize();
            lbl.setBounds(25 + insets.left, 5 + insets.top,
                    lblSize.width, lblSize.height);
            board.add(lbl);
            lbl2.setBounds(30 + insets.left, 5 + insets.top,
                    lblSize2.width, lblSize2.height);
            board.add(lbl);
            board.add(lbl2);

            //Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

            lbl.setLocation(p.x-(lbl.getWidth()/2)+2,p.y-15);
            lbl2.setLocation(p.x+15,p.y);
            lbl.setForeground(Color.BLACK);
            lbl2.setForeground(riskModel.getTerritoryOwner(t).getColour());
            lbl.setBackground(Color.WHITE);
            lbl2.setBackground(Color.WHITE);
            //lbl.setBorder(raisedEtched);
            //lbl2.setBorder(raisedEtched);
            lbl.setOpaque(true);
            lbl2.setOpaque(true);
        }
    }

    public void showFrame() {
        setResizable(false);
        setVisible(true);
    }

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

    private ArrayList<String> getPlayerNames(int numPlayers)
    {
        ArrayList<String> names = new ArrayList<>();
        for(int i= 0; i<numPlayers;i++)
        {
            String input = null;
            while(input == null || input.length() == 0){
                input = JRiskOptionPane.showPlayerNameDialog(this,i+1);
            }
            names.add(input);
        }
        return names;
    }
    /**
     * We need some sort of updating methods that will do the following.
     *
     * 1) update the lists when a territory is selected
     */

    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }

    public void setCurrentInstruction(String info) {
        instructionsText.setText(info);
    }

    @Override
    public void handleRiskUpdate(RiskEvent e) {
        Game riskModel = (Game) e.getSource();
        RiskEventType eventType = e.getType();
        Object trigger = e.getTrigger();
        if (eventDescriptions.getSize()==25) eventDescriptions.clear();

        //TODO: only tell game board to repaint when necessary
        switch (eventType) {
            case GAME_STARTED:
                //TODO: add handling for game started
            case TURN_BEGAN:
                //TODO: add handling for turn began
            case TURN_ENDED:
                //TODO: trigger this event when the next turn method is called
                //TODO: but before the player is actually switched
                Player currentPlayer = riskModel.getCurrentPlayer();
                Player nextPlayer = (Player) trigger;
                playerTurnLbl.setBackground(nextPlayer.getColour());
                playerTurnLbl.setForeground(getContrastColor(nextPlayer.getColour()));
                playerTurnLbl.setText("it is : "+nextPlayer.getName()+"'s turn.        ");
                eventDescriptions.addElement(String.format("%s's turn had ended, it is now %s's turn.",
                        currentPlayer.getName(),nextPlayer.getName()));
            case ATTACK_COMMENCED:
                //TODO: add handling for attack started
            case ATTACK_COMPLETED:
                //TODO: add handling for attack completed
            case TERRITORY_DOMINATION:
                //TODO: add handling for territory takeover
                board.revalidate();
            case UNITS_MOVED:
                //TODO: add handling for units being moved
            case GAME_OVER:
                //TODO: add handling for the end of the game
        }
    }

    public void clearInfoDisplay() {
        if (infoModel.getRowCount() > 0) {
            for (int i = infoModel.getRowCount() - 1; i > -1; i--) {
                infoModel.removeRow(i);
            }
        }
    }

    public void setInfoDisplay(Territory territory) {
        Player p = riskModel.getTerritoryOwner(territory);
        clearInfoDisplay();
        infoModel.addRow(new String[]{"Name", territory.getName()});
        infoModel.addRow(new String[]{"Owner", p.getName()});
        infoModel.addRow(new String[]{"Colour", p.getColour().toString()});
        infoModel.addRow(new String[]{"Units", String.valueOf(territory.getUnits())});
    }

    public void restoreGUI() {
        selectedAction = -1;
        selectedTerritory = null;
        pointsToPaint = riskModel.getAllCoordinates();
        attack.setText("Attack");
        attack.setEnabled(false);
        clearInfoDisplay();
        instructionsText.setText(riskModel.getCurrentPlayer().getName()+
                ", please select a territory or end your turn.");
    }

    public void setSelectedTerritory(Territory selectedTerritory) {
        this.selectedTerritory = selectedTerritory;
    }
}