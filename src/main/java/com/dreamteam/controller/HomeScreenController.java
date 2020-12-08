package com.dreamteam.controller;

import com.dreamteam.core.AIPlayer;
import com.dreamteam.core.AudioPlayer;
import com.dreamteam.core.GameSingleton;
import com.dreamteam.core.Player;
import com.dreamteam.view.HomeScreenFrame;
import com.dreamteam.view.JRiskOptionPane;
import com.dreamteam.view.RiskFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public class HomeScreenController implements ActionListener {

    private HomeScreenFrame homeView;

    public HomeScreenController(HomeScreenFrame homeView) {
        this.homeView = homeView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        AudioPlayer.playSound("btnPress");
        if (o instanceof JButton) {
            JButton btn = (JButton) o;
            switch (btn.getActionCommand()) {
                case "N":
                    //TODO: somehow create a new game here
                    //TODO: migrate the adding of players to this screen
                    File selected = openFile(homeView,"./worlds/world_maps");
                    if (selected!=null && selected.getName().endsWith(".world")) {
                        try {
                            constructNewGame(new ZipFile(selected));
                        } catch (Exception ioException) {
                            ioException.printStackTrace();
                        }
                    } else {
                        System.out.println("Incorrect file type selected (select .world files)");
                    }
                    break;
                case "L":
                    selected = openFile(homeView,"./worlds/saved_games");
                    if (selected!=null && selected.getName().endsWith(".save")) {
                        try {
                            loadGame(new ZipFile(selected));
                        } catch (Exception ioException) {
                            ioException.printStackTrace();
                        }
                    } else {
                        System.out.println("Incorrect file type selected (select .save files)");
                    }
                    break;
                case "E":
                    System.exit(0);
                    break;
            }
        }
    }

    public static File saveFile(JFrame parent, String path) {
        //TODO: use methods to read in the game that the user wants
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(path));
        chooser.setDialogTitle("Choose a Saved Game");
        chooser.setMultiSelectionEnabled(false);

        int result = chooser.showSaveDialog(parent);
        AudioPlayer.playSound("btnPress");

        if (result == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile());

            if (file.getName().endsWith(".save")||file.getName().endsWith(".world")) {
                return chooser.getSelectedFile();
            }
        }
        return null;
    }


    public static File openFile(JFrame parent, String path) {
        //TODO: use methods to read in the game that the user wants
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(path));
        chooser.setDialogTitle("Choose a Saved Game");
        chooser.setMultiSelectionEnabled(false);

        int result = chooser.showSaveDialog(parent);
        AudioPlayer.playSound("btnPress");

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile());

            if (file.getName().endsWith(".save")||file.getName().endsWith(".world")) {
                return chooser.getSelectedFile();
            }
        }
        AudioPlayer.playSound("btnPress");
        return null;
    }

    private void constructNewGame(ZipFile file) throws Exception {
        System.out.println(file);
        int numPlayers = getNumOfPlayers();
        List<Player> players = getPlayers(numPlayers);
        if (numPlayers <= 5) {
            addAIsToList(numOfAIs(numPlayers), players);
        }
        GameSingleton gs = GameSingleton.getGameInstance();
        gs.clean();
        gs.setPlayers(players);
        RiskFrame rf = new RiskFrame(gs,file);
        gs.newGame(file);
        homeView.dispose();
    }

    private void loadGame(ZipFile file) throws Exception {
        GameSingleton gs = GameSingleton.getGameInstance();
        gs.clean();
        RiskFrame rf = new RiskFrame(gs,file);
        gs.importGame(file);
        homeView.dispose();
    }

    /**
     * Creates a Window to pop up asking how many players will be playing this game
     * only allows the user to pick between 2 - 6
     *
     * @return The number of players
     */
    private int getNumOfPlayers() {
        String input;
        Object[] options = {"1", "2", "3", "4", "5", "6"};
        input = (String) JOptionPane.showInputDialog(homeView, "How many human players?", "Number of Human Players",
                JOptionPane.QUESTION_MESSAGE, null, options, "2");
        //User pressed close or cancel
        if (input == null) {
            System.exit(0);
        }
        AudioPlayer.playSound("btnPress");
        return Integer.parseInt(input);
    }

    /**
     * Gets a list of all the players currently playing the game
     *
     * @param numPlayers The number of players playing the game
     * @return List of all the players in the game
     */
    private List<Player> getPlayers(int numPlayers) {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            String input = null;
            while (input == null || input.length() == 0) {
                input = JRiskOptionPane.showPlayerNameDialog(homeView, i + 1, "Player");
            }
            players.add(new Player(input));
            AudioPlayer.playSound("btnPress");
        }
        return players;
    }

    /**
     * gets the number of AIs playing the game
     *
     * @param numSpotsTaken, int determining the max of AIs to be allowed
     * @return the number of AIs chosen
     */
    private int numOfAIs(int numSpotsTaken) {
        String[] options;
        if (numSpotsTaken == 1) {
            options = new String[]{"1", "2", "3", "4", "5"};
        } else {
            options = new String[7 - numSpotsTaken];
            for (int i = 0; i < 7 - numSpotsTaken; i++) {
                options[i] = Integer.toString((i));
            }
        }
        String input = (String) JOptionPane.showInputDialog(homeView, "How many AI Players?", "Number of AI Players",
                JOptionPane.QUESTION_MESSAGE, null, options, "2");
        //User pressed close or cancel
        if (input == null) {
            System.exit(0);
        }
        AudioPlayer.playSound("btnPress");
        return Integer.parseInt(input);
    }

    /**
     * Adds a number of AI to the players list
     *
     * @param numAIs  number of AIs to add
     * @param players the list of players AIs are added to
     */
    private void addAIsToList(int numAIs, List<Player> players) {
        for (int i = 0; i < numAIs; i++) {
            String input = null;
            while (input == null || input.length() == 0) {
                input = JRiskOptionPane.showPlayerNameDialog(homeView, i + 1, "AI");
            }
            players.add(new AIPlayer(input));
        }
    }
}
