package com.dreamteam.view;

import com.dreamteam.controller.HomeScreenController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents the GUI frame of the menu for the game risk.
 *
 * @author Tony Zeidan
 * @author Anthony Dooley
 * @author Kyler Verge
 * @author Ethan Chase
 */
public class HomeScreenFrame extends JFrame {

    /**
     * Button to create a new game.
     */
    private JButton newGame;

    /**
     * Button to load up a previously saved game
     */
    private JButton loadGame;

    /**
     * Button to exit the menu frame of the game
     */
    private JButton exitGame;

    private final ClassLoader LOADER = getClass().getClassLoader();

    /**
     * Constructor for instances of HomeScreenFrame, constructs a new GUI
     */
    public HomeScreenFrame() {
        super("Dream Team RISK!");

        composeFrame();
    }

    /**
     * Create the frame for the Home Screen with buttons added and image icon.
     */
    private void composeFrame() {
        setLayout(new BorderLayout());

        InputStream is = LOADER.getResourceAsStream("home_screen/DreamTeam.png");
        if (is!=null) {
            try {
                ImageIcon icon = new ImageIcon(ImageIO.read(is));
                JLabel label = new JLabel(icon);
                label.setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
                add(BorderLayout.CENTER,label);
                System.out.println("Home screen image displayed.");
            } catch (IOException e) {
                System.out.println("Home screen image could not be read.");
            }

        }

        HomeScreenController controller = new HomeScreenController(this);

        JPanel buttonPane = new JPanel(new GridLayout(3,1));

        newGame = new JButton("New Game");
        newGame.setActionCommand("N");
        newGame.addActionListener(controller);
        loadGame = new JButton("Load Game");
        loadGame.setActionCommand("L");
        loadGame.addActionListener(controller);
        exitGame = new JButton("Exit");
        exitGame.setActionCommand("E");
        exitGame.addActionListener(controller);

        buttonPane.add(newGame);
        buttonPane.add(loadGame);
        buttonPane.add(exitGame);

        add(BorderLayout.SOUTH,buttonPane);

        setPreferredSize(new Dimension(600,600));
    }

    /**
     * Reveal the Home Screen to the user.
     */
    public void showFrame() {
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        HomeScreenFrame hs = new HomeScreenFrame();
        hs.showFrame();
    }
}
