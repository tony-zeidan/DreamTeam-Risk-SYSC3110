package com.dreamteam.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreenFrame extends JFrame implements ActionListener {

    private JButton newGame;

    private JButton loadGame;

    public HomeScreenFrame() {
        super("Dream Team RISK!");

        composeFrame();

        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
    }

    private void composeFrame() {
        setLayout(new BorderLayout());

        JPanel buttonPane = new JPanel(new GridLayout(2,1));

        newGame = new JButton("New Game");
        newGame.setActionCommand("N");
        loadGame = new JButton("Load Game");
        loadGame.setActionCommand("L");

        buttonPane.add(newGame);
        buttonPane.add(loadGame);

        add(BorderLayout.SOUTH,buttonPane);

        setPreferredSize(new Dimension(600,600));
    }

    private void showFrame() {
        pack();
        setResizable(false);
        setVisible(true);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        HomeScreenFrame hs = new HomeScreenFrame();
        hs.showFrame();
    }
}
