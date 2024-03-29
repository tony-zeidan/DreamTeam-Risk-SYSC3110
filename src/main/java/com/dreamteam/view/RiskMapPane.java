package com.dreamteam.view;

import com.dreamteam.controller.RiskController;
import com.dreamteam.core.Player;
import com.dreamteam.core.RiskColour;
import com.dreamteam.core.Territory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a part of RiskFrame and contains the Map with its points and labels. RiskMapPane is also a com.dreamteam.view
 * and handles the update of the Map GUI when certain events in the model happen.
 *
 * @author Kyler Verge
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class RiskMapPane extends JPanel implements RiskGameHandler {
    /**
     * Stores the points that will be painted on the map.
     * It is altered constantly depending on user inputs.
     */
    private Map<Territory, Point> pointsToPaint;
    /**
     * The stretching of the JPane in the X direction
     */
    private double scalingX;
    /**
     * The stretching of the JPane in the Y direction
     */
    private double scalingY;
    /**
     * Image of the Map
     */
    private Image finalMapImage;
    /**
     * The Original Dimensions without stretching in x and y
     */
    private Dimension originalDim;
    /**
     * Checks to see if it has been loaded before to set up variables
     */
    boolean firstTimeLoaded;
    /**
     * Represents the diameter of the innermost circle when painting.
     */
    public static final int INNER_POINT_DIAMETER = 12;
    /**
     * Represents the radius of the innermost circle when painting.
     */
    public static final int INNER_POINT_RADIUS = INNER_POINT_DIAMETER / 2;
    /**
     * Represents the diameter of the second outermost circle when painting.
     */
    public static final int OUTER_POINT_DIAMETER = 16;
    /**
     * Represents the radius of the second outermost circle when painting.
     */
    public static final int OUTER_POINT_RADIUS = OUTER_POINT_DIAMETER / 2;
    /**
     * Represents the diameter of the outermost circle when painting.
     */
    public static final int HIT_POINT_DIAMETER = 40;
    /**
     * Represents the radius of the outermost circle when painting.
     */
    public static final int HIT_POINT_RADIUS = HIT_POINT_DIAMETER / 2;

    /**
     * Constructor for instances of RiskMapPane.
     * Creates a new map pane (meant to be embedded within RiskFrame)
     * that uses the same RiskController.
     *
     * @param mapImage Image of the current map
     * @param rc The risk controller that this pane listens to
     */
    public RiskMapPane(Image mapImage, RiskController rc) {
        this.addMouseListener(rc);
        this.setLayout(null);
        pointsToPaint = null;
        scalingX = 1;
        scalingY = 1;
        //attempt to read the map file
        //BufferedImage mapImage = null;
        //InputStream is = null;
        /*
        try {
            is = new FileInputStream(mapFile);
            mapImage = ImageIO.read(is);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find map file");
            return;
        } catch (IOException e) {
            System.out.println("There was an error while reading the map image");
            return;
        }*/
        finalMapImage = mapImage;
        firstTimeLoaded = true;
    }

    /**
     * Overwritten method.
     * Paints the board, with the scaled map and points.
     *
     * @param g The graphics object for this component
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (firstTimeLoaded) {
            originalDim = getSize();
            firstTimeLoaded = false;
        }
        super.paintComponent(g);
        this.removeAll();  //clears the labels off of the board
        Dimension current = getSize();
        scalingX = current.getWidth() / originalDim.getWidth();
        scalingY = current.getHeight() / originalDim.getHeight();
        //draws the scaled version of the map image
        g.drawImage(finalMapImage.getScaledInstance(getWidth(), getHeight(),
                Image.SCALE_SMOOTH), 0, 0, null);
        paintPoints(g);     //paint points representing territories
        placePointLabels();     //paint the labels to go with the points
    }


    /**
     * @param g Graphics draws the points of the territories with their colour
     */
    private void paintPoints(Graphics g) {
        if (pointsToPaint == null) return;

        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            g.setColor(Color.BLACK);
            int x = (int) (p.getX() * scalingX);
            int y = (int) (p.getY() * scalingY);

            int diff = OUTER_POINT_RADIUS - INNER_POINT_RADIUS;

            g.fillOval(x - diff, y - diff, OUTER_POINT_DIAMETER, OUTER_POINT_DIAMETER);

            Player player = t.getOwner();
            g.setColor(player.getColour().getValue());

            g.fillOval(x, y, INNER_POINT_DIAMETER, INNER_POINT_DIAMETER);
            g.setColor(Color.BLACK);

            diff = HIT_POINT_RADIUS - INNER_POINT_RADIUS;

            g.drawOval(x - diff, y - diff, HIT_POINT_DIAMETER, HIT_POINT_DIAMETER);
        }
    }

    /**
     * draws the labels one with the name of the territory and a label with
     * the number of units that are on that territory
     */
    public void placePointLabels() {
        if (pointsToPaint == null) return;

        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            int x = (int) (p.getX() * scalingX);
            int y = (int) (p.getY() * scalingY);
            JLabel lbl = new JLabel(t.getName());
            JLabel lbl2 = new JLabel(String.valueOf(t.getUnits()));

            lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lbl2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            Insets insets = this.getInsets();
            Dimension lblSize = lbl.getPreferredSize();
            Dimension lblSize2 = lbl2.getPreferredSize();
            lbl.setBounds(25 + insets.left, 5 + insets.top, lblSize.width, lblSize.height);
            lbl2.setBounds(30 + insets.left, 5 + insets.top, lblSize2.width, lblSize2.height);

            Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
            setBorder(raisedEtched);

            lbl.setLocation(x - (lbl.getWidth() / 2) + 2, y - 15);
            lbl2.setLocation(x + 15, y);
            lbl.setForeground(Color.BLACK);
            RiskColour playerColour = t.getOwner().getColour();
            lbl2.setForeground(playerColour.getValue());
            lbl.setBackground(Color.WHITE);
            lbl2.setBackground(Color.WHITE);
            //lbl.setBorder(raisedEtched);
            //lbl2.setBorder(raisedEtched);
            lbl.setOpaque(true);
            lbl2.setOpaque(true);
            this.add(lbl);
            this.add(lbl2);
        }
    }

    /**
     * sets the mapping of the territory to the point that should be painted, when repaint called.
     *
     * @param mapping is the mapping of the territory to the coordinate it should be at
     */
    public void setPointsToPaint(Map<Territory, Point> mapping) {
        pointsToPaint = mapping;
    }

    /**
     * returns the mapping of the territory to its location/point.
     *
     * @return Map of the territory and where its is located.
     */
    public Map<Territory, Point> getPointsToPaint() {
        return pointsToPaint;
    }

    /**
     * gets the current scaling that the points are being painted by on the JPanel
     *
     * @return double, the scaling of x that the points are being painted by
     */
    public double getScalingX() {
        return scalingX;
    }

    /**
     * gets the current scaling that the points are being painted by on the JPanel
     *
     * @return double, the scaling of y that the points are being painted by
     */
    public double getScalingY() {
        return scalingY;
    }

    /**
     * handles the event that the model provides, from actions done to the risk model.
     *
     * @param e the RiskEvent that provides the type of event it is and information where applicable
     */
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        RiskEventType eventType = e.getType();
        Object[] info = e.getEventInfo();
        //if (eventDescriptions.getSize()==25) eventDescriptions.clear();

        //System.out.println(eventType);
        switch (eventType) {

            case UPDATE_MAP:
                //for selecting on our map we need a reference
                setPointsToPaint((HashMap<Territory, Point>) info[0]);
                repaint();
                revalidate();
                break;
        }
    }

    /**
     * Retrieves the image of the current map of this game.
     *
     * @return The image of the current map
     */
    public Image getImage() {
        return finalMapImage;
    }
}
