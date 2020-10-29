import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class represents a GUI for the game risk, including the feature of
 *  an interactive map.
 *
 * @author Tony Zeidan
 */
public class RiskFrame extends JFrame {

    /**
     * A container for the points that will be displayed on the map.
     */
    private List<Point> territoryPoints;

    /**
     * Constructor for instances of RiskFrame, constructs a new GUI.
     *
     * NEEDS ALTERING FOR COMMUNICATION WITH MODEL AND
     * CONTROLLER
     */
    public RiskFrame() {
        super("RISK");
        setLayout(new BorderLayout());

        //change to filepath of map image
        String filepath = "C:\\Users\\Tony Zeidan\\Pictures\\RiskBoard.png";

        //attempt to read the map file
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(new File(filepath));
        } catch (IOException ioException) {
            System.out.println("RISK Board Load Failed");
            ioException.printStackTrace();
        }

        Image finalMapImage=mapImage;
        JPanel board = new JPanel() {

            /*
            Paint the image on our canvas.
            1) every time paintComponent() is called we must resize the image. (could also be done through a window resize listener)
            2) we do this through the use of getScaledInstance()
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                //draw the scaled instance of the image
                boolean b = g.drawImage(finalMapImage.getScaledInstance(getWidth(),getHeight(),Image.SCALE_SMOOTH), 0, 0, null);
            }
        };

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        //create a massive seperator in the menu bar
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(new JLabel("Player N's Turn "));    //we must update this with the players turn

        setJMenuBar(menuBar);

        /*
        This panel contains the buttons for the players turn.
            - we must enable them and disable them accordingly
         */
        JPanel buttonPane = new JPanel(new GridLayout(3,1));
        buttonPane.add(new JButton("Attack"));
        buttonPane.add(new JButton("World State"));
        buttonPane.add(new JButton("End Turn"));

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
        1) Owner: Player/String
        2) Color: String
        2) Units : int/String
         */
        JTable infoTable = new JTable(); //TODO: add model to update table
        JScrollPane gameInfoScroller = new JScrollPane(infoTable);
        gameInfoScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameInfoScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //combo box for selecting territory
        topSubEventPane.add(BorderLayout.NORTH,new JLabel("Select Territory"));
        JComboBox<String> topSubEventCombo = new JComboBox<>();
        topSubEventCombo.addItem("GRINEER");    //testing items
        topSubEventCombo.addItem("Eurasia");        //testing items
        topSubSubEventPane.add(BorderLayout.NORTH,topSubEventCombo);

        //add table to top sub pane
        topSubSubEventPane.add(BorderLayout.CENTER,gameInfoScroller);
        topSubEventPane.add(BorderLayout.CENTER,topSubSubEventPane);

        //the following is the list of in game events that occurred
        JList eventList = new JList(); //TODO: add model to update list
        JScrollPane gameEventScroller = new JScrollPane(eventList);
        gameEventScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameEventScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //add list to the bottom sub pane
        bottomSubEventPane.add(BorderLayout.NORTH,new JLabel("Game Events"));
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
        setResizable(false);
        pack();
        setVisible(true);
    }

    /**
     * We need some sort of updating methods that will do the following.
     *
     * 1) update the lists when a territory is selected
     */

    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }
}
