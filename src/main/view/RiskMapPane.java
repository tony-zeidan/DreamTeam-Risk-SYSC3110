package main.view;

import main.core.Player;
import main.core.RiskColour;
import main.core.Territory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RiskMapPane extends JPanel implements RiskGameView {
    /**
     * Stores the points that will be painted on the map.
     * It is altered constantly depending on user inputs.
     */
    private Map<Territory,Point> pointsToPaint;
    private double scalingX;
    private double scalingY;
    private Image finalMapImage;
    private Dimension originalDim;
    boolean firstTimeLoaded;
    public RiskMapPane(RiskController rc)
    {
        this.addMouseListener(rc);
        this.setLayout(null);
        pointsToPaint = null;
        scalingX=1;
        scalingY=1;
        //attempt to read the map file
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(getClass().getResource("/resources/map_packages/main_package/map.png"));
        } catch (IOException ioException) {
            System.out.println("RISK Board Load Failed");
            ioException.printStackTrace();
        }
        finalMapImage=mapImage;
        firstTimeLoaded = true;
    }
    protected void paintComponent(Graphics g)
    {
        if(firstTimeLoaded)
        {
            originalDim = getSize();
            firstTimeLoaded = false;
        }
        super.paintComponent(g);
        this.removeAll();  //clears the labels off of the board
        Dimension current = getSize();
        scalingX = current.getWidth()/originalDim.getWidth();
        scalingY = current.getHeight()/originalDim.getHeight();
        //draws the scaled version of the map image
        g.drawImage(finalMapImage.getScaledInstance(getWidth(),getHeight(),
                Image.SCALE_SMOOTH), 0, 0, null);
        paintPoints(g);     //paint points representing territories
        placePointLabels();     //paint the labels to go with the points
    }
    //TODO
    /**
     *
     * @param
     */
    private void paintPoints(Graphics g) {
        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            g.setColor(Color.BLACK);
            int x = (int) (p.getX() * scalingX);
            int y = (int) (p.getY() * scalingY);
            g.fillOval(x-2,y-2,16,16);
            Player player = t.getOwner();
            g.setColor(player.getColour().getValue());
            g.fillOval(x,  y, 12, 12);
        }
        System.out.println("1");
    }
    public void placePointLabels() {
        if (pointsToPaint ==null) return;

        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);
            int x = (int) (p.getX() * scalingX);
            int y = (int) (p.getY() * scalingY);
            JLabel lbl = new JLabel(t.getName());
            JLabel lbl2 = new JLabel(String.valueOf(t.getUnits()));

            lbl.setFont(new Font("Segoe UI",Font.BOLD,9));
            lbl2.setFont(new Font("Segoe UI",Font.BOLD,11));
            Insets insets = this.getInsets();
            Dimension lblSize = lbl.getPreferredSize();
            Dimension lblSize2 = lbl2.getPreferredSize();
            lbl.setBounds(25 + insets.left, 5 + insets.top, lblSize.width, lblSize.height);
            lbl2.setBounds(30 + insets.left, 5 + insets.top, lblSize2.width, lblSize2.height);

            Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

            lbl.setLocation(x-(lbl.getWidth()/2)+2,y-15);
            lbl2.setLocation(x+15,y);
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
        System.out.println("a");
    }
    public void setPointsToPaint(HashMap<Territory,Point> mapping)
    {
        pointsToPaint = mapping;
    }
    public Map<Territory,Point> getPointsToPaint()
    {
        return pointsToPaint;
    }
    public double getScalingX()
    {
        return scalingX;
    }
    public double getScalingY()
    {
        return scalingY;
    }

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
                setPointsToPaint((HashMap<Territory, Point>) info[0]);
                repaint();
                revalidate();
                break;
        }
    }
}
