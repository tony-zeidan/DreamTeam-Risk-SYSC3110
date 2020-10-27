import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class RiskFrame2 extends JFrame {

    public RiskFrame2() {
        super("RISK");
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JRadioButtonMenuItem fullscreen = new JRadioButtonMenuItem("Fullscreen");

        menu.add(fullscreen);
        menuBar.add(menu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(new JLabel("Player N's Turn "));

        setJMenuBar(menuBar);

        JPanel buttonPane = new JPanel(new GridLayout(3,1));
        buttonPane.add(new JButton("Attack"));
        buttonPane.add(new JButton("World State"));
        buttonPane.add(new JButton("End Turn"));

        JPanel gamePane = new JPanel(new BorderLayout());
        gamePane.add(BorderLayout.NORTH,new JLabel("Game Information\t"));
        gamePane.add(BorderLayout.CENTER,new JTextArea());
        gamePane.add(BorderLayout.SOUTH,new JTextField("Whatever"));

        JPanel eventPane = new JPanel(new BorderLayout());
        JPanel topSubEventPane = new JPanel(new BorderLayout());
        topSubEventPane.add(BorderLayout.NORTH,new JLabel("Select Territory"));
        JComboBox<String> topSubEventCombo = new JComboBox<>();
        topSubEventCombo.addItem("GRINEER");
        topSubEventCombo.addItem("Eurasia");
        topSubEventPane.add(BorderLayout.CENTER,topSubEventCombo);
        eventPane.add(BorderLayout.NORTH,topSubEventPane);
        eventPane.add(BorderLayout.CENTER,new JTextArea("Territory Information "));

        Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        eventPane.setBorder(raisedEtched);
        gamePane.setBorder(raisedEtched);

        //Sizing
        eventPane.setPreferredSize(new Dimension(200,500));
        gamePane.setPreferredSize(new Dimension(450,500));

        getContentPane().add(BorderLayout.SOUTH,buttonPane);
        getContentPane().add(BorderLayout.CENTER,gamePane);
        getContentPane().add(BorderLayout.WEST,eventPane);

        setPreferredSize(new Dimension(650,500));
        //setResizable(false);
        //setUndecorated(true);
        pack();
        setVisible(true);
    }


    public static void main(String[] args) {
        RiskFrame2 rf = new RiskFrame2();
    }
}
