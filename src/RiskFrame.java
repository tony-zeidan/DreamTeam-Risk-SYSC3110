import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;

public class RiskFrame extends JFrame {

    public RiskFrame() {
        super("RISK");
        setLayout(new BorderLayout());

        String filepath = "C:\\Users\\Tony Zeidan\\Pictures\\RiskBoard.png";

        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(new File(filepath));
        } catch (IOException ioException) {
            System.out.println("RISK Board Load Failed");
            ioException.printStackTrace();
        }

        Image finalMapImage=mapImage;
        JPanel board = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                //draw the scaled instance of the image
                boolean b = g.drawImage(finalMapImage.getScaledInstance(getWidth(),getHeight(),Image.SCALE_SMOOTH), 0, 0, null);
            }
        };

        JPanel buttonPane = new JPanel(new GridLayout(3,1));
        buttonPane.add(new JButton("Attack"));
        buttonPane.add(new JButton("World State"));
        buttonPane.add(new JButton("End Turn"));

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(BorderLayout.NORTH,new JLabel("Game Information"));
        infoPane.add(BorderLayout.CENTER,new JTextArea());
        infoPane.add(BorderLayout.SOUTH,new JTextField("Whatever"));


        getContentPane().add(BorderLayout.CENTER,board);
        getContentPane().add(BorderLayout.EAST,buttonPane);
        getContentPane().add(BorderLayout.WEST,infoPane);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }
}
