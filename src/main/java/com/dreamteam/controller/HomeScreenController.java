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

/**
 * This class represents the controller for the Home Screen frame.
 *
 * @author Tony Zeidan
 */
public class HomeScreenController implements ActionListener {

    /**
     * The current view representing the home screen frame
     */
    private HomeScreenFrame homeView;

    /**
     * Constructor for the HomeScreenController
     *
     * @param homeView The Home Screen GUI frame
     */
    public HomeScreenController(HomeScreenFrame homeView) {
        this.homeView = homeView;
    }

    /**
     * ActionPerformed for HomeScreenController.
     * Supports all buttons on the frame.
     *
     * @param e The event that was triggered
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        AudioPlayer.playSound("btnPress");
        if (o instanceof JButton) {
            JButton btn = (JButton) o;
            switch (btn.getActionCommand()) {
                case "N":
                    File selected = openFile(homeView,"./worlds/world_maps",true);
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
                    selected = openFile(homeView,"./worlds/saved_games",false);
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

    /**
     * This dialog represents one that a user may see when saving a file.
     * May not be necessary to have a dedicated method for saving and opening.
     *
     * @param parent The frame that this dialog is linked to
     * @param path The path The path of where the dialog should begin in
     * @return The file the user selected or null
     */
    public static File saveFile(JFrame parent, String path) {
        //TODO: use methods to read in the game that the user wants
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(path));
        chooser.setDialogTitle("Choose a name for this save game (.save at the end)");
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


    /**
     * This method returns the input file of a dialog that asks the user
     * where they wish to open a saved game or new game.
     *
     * @param parent The parent frame
     * @param path The path of the file to be opened
     * @param newLoad whether this dialog represents a new game or a load game
     * @return The file that the user selected
     */
    public static File openFile(JFrame parent, String path, boolean newLoad) {
        //TODO: use methods to read in the game that the user wants
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(path));
        String str = (newLoad)?"Choose a world to play on (.world)":"Choose a saved game to resume (.save)";
        chooser.setDialogTitle(str);
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

    /**
     * Constructs a new game by invoking methods in a new GameSingleton object.
     * It then links this instance to a RiskFrame GUI.
     *
     * @param file The zipfile containing the information for this new game (.world)
     * @throws Exception When the
     */
    private void constructNewGame(ZipFile file) {
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
