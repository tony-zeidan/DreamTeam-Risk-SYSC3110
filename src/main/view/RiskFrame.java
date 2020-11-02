package main.view;


import main.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a GUI for the game risk, including the feature of
 *  an interactive map.
 *
 * @author Tony Zeidan
 */
public class RiskFrame extends JFrame implements RiskGameListener {

    private Game riskModel;

    private DefaultListModel<String> eventDescriptions;

    /**
     *
     */
    private Territory selectedTerritory;

    private int selectedAction;

    private Map<Territory,Point> pointsToPaint;

    private DefaultTableModel infoModel;

    /**
     * JPanel containing the game board;
     */
    private JPanel board;

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

            /*
            Paint the image on our canvas.
            1) every time paintComponent() is called we must resize the image. (could also be done through a window resize listener)
            2) we do this through the use of getScaledInstance()
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                //draw the scaled instance of the image
                board.removeAll();
                boolean b = g.drawImage(finalMapImage.getScaledInstance(getWidth(),getHeight(),Image.SCALE_SMOOTH), 0, 0, null);
                paintPoints(g);
                placePointLabels();
            }
        };
        board.addMouseListener(rc);
        board.setLayout(null);


        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        //create a massive seperator in the menu bar
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(new JLabel("main.core.Player N's Turn "));    //we must update this with the players turn

        setJMenuBar(menuBar);

        /*
        This panel contains the buttons for the players turn.
            - we must enable them and disable them accordingly
         */
        JPanel buttonPane = new JPanel(new GridLayout(3,1));
        JButton attack = new JButton("Attack");
        JButton worldState = new JButton("World State");
        JButton endTurn = new JButton("End Turn");
        attack.addActionListener(rc);
        worldState.addActionListener(rc);
        endTurn.addActionListener(rc);
        buttonPane.add(attack);
        buttonPane.add(worldState);
        buttonPane.add(endTurn);

        /*
        This panel (eventPane) is responsible for showing the events that occurred
        during the game and individual territory information.
        It contains two main sub panels:
            1) topSubEventPane is responsible for displaying individual territory information
                either when the player clicks on a territory on the map, or when they select one
                from the drop down list.
             2) bottomSubEventPane is responsible for showing a list of the events that have
                occurred during the game, perhaps through the use of a RiskGameEvent.toString()
         */
        JPanel eventPane = new JPanel(new GridLayout(2,1));
        JPanel topSubEventPane = new JPanel(new BorderLayout());
        JPanel topSubSubEventPane = new JPanel(new BorderLayout());

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
        JScrollPane gameInfoScroller = new JScrollPane(infoTable);
        gameInfoScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameInfoScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //combo box for selecting territory
        topSubEventPane.add(BorderLayout.NORTH,new JLabel("Select main.core.Territory"));
        JComboBox<String> topSubEventCombo = new JComboBox<>();
        topSubEventCombo.addItem("GRINEER");    //testing items
        topSubEventCombo.addItem("Eurasia");        //testing items
        topSubSubEventPane.add(BorderLayout.NORTH,topSubEventCombo);

        //add table to top sub pane
        topSubSubEventPane.add(BorderLayout.CENTER,gameInfoScroller);
        topSubEventPane.add(BorderLayout.CENTER,topSubSubEventPane);

        //the following is the list of in game events that occurred
        JList eventList = new JList(eventDescriptions);
        JScrollPane gameEventScroller = new JScrollPane(eventList);
        gameEventScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameEventScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //add list to the bottom sub pane
        bottomSubEventPane.add(BorderLayout.NORTH,new JLabel("main.core.Game Events"));
        bottomSubEventPane.add(BorderLayout.CENTER,gameEventScroller); //TODO: switch this to a scroller JList

        //set borders for both sub panes (to look seperate)
        topSubEventPane.setBorder(raisedEtched);
        bottomSubEventPane.setBorder(raisedEtched);

        //add both sub panes to main event pane
        eventPane.add(BorderLayout.NORTH,topSubEventPane);
        eventPane.add(BorderLayout.CENTER,bottomSubEventPane);

        //set event pane size
        eventPane.setPreferredSize(new Dimension(200,800));

        //add everything to the main content pane
        getContentPane().add(BorderLayout.CENTER,board);
        getContentPane().add(BorderLayout.SOUTH,buttonPane);
        getContentPane().add(BorderLayout.WEST,eventPane);

        //make the program terminate when frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

    public Map<Territory,Point> getPointsToPaint() {
        return pointsToPaint;
    }

    public void setPointsToPaint(Map<Territory,Point> pointsToPaint) {
        this.pointsToPaint=pointsToPaint;
        board.repaint();
        board.revalidate();
    }

    private void paintPoints(Graphics g) {
        /*for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            Map<Territory,Point> neighbourNodes = riskModel.getNeighbouringNodes(t);
            for (Point p2 : neighbourNodes.values()) {
                g.drawLine(p2.x+6,p2.y+6,p.x+6,p.y+6);
            }
        }*/

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
            lbl2.setBounds(25 + insets.left, 5 + insets.top,
                    lblSize2.width, lblSize2.height);
            board.add(lbl);
            board.add(lbl2);

            //Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

            lbl.setLocation(p.x,p.y-15);
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
        boolean validNumEntered = false;
        while (!validNumEntered) {
            input  = JOptionPane.showInputDialog(this,"Please enter the number of players");
            //attempt to parse an integer value from the user's input
            try {
                numOfPlayers = Integer.parseInt(input);

                //check if the number parsed is invalid
                if (numOfPlayers < 7 && numOfPlayers > 1) {
                    validNumEntered = true;
                }
                //catch the exception (most commonly thrown when an integer can't be parsed)
            } catch (NumberFormatException e) {
                validNumEntered = false;
            }
        }
        return numOfPlayers;
    }
    private ArrayList<String> getPlayerNames(int numPlayers)
    {
        ArrayList<String> names = new ArrayList<>();
        for(int i= 0; i<numPlayers;i++)
        {
            names.add(JOptionPane.showInputDialog(this,String.format("Player %s Name: ", i + 1)));
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

    @Override
    public void handleRiskUpdate(RiskEvent e) {
        Game riskModel = (Game) e.getSource();
        String description = e.getDescription();
        eventDescriptions.addElement(description);
        //board.repaint();
        board.revalidate();
    }

    public void clearInfoDiaplay() {
        if (infoModel.getRowCount() > 0) {
            for (int i = infoModel.getRowCount() - 1; i > -1; i--) {
                infoModel.removeRow(i);
            }
        }
    }

    public void setInfoDisplay(Territory territory) {
        Player p = riskModel.getTerritoryOwner(territory);
        clearInfoDiaplay();
        infoModel.addRow(new String[]{"Name", territory.getName()});
        infoModel.addRow(new String[]{"Owner", p.getName()});
        infoModel.addRow(new String[]{"Colour", p.getColour().toString()});
        infoModel.addRow(new String[]{"Units", String.valueOf(territory.getUnits())});
    }

    public void setSelectedTerritory(Territory selectedTerritory) {
        this.selectedTerritory = selectedTerritory;
    }
}
