package com.dreamteam.controller;

import com.dreamteam.view.HomeScreenFrame;
import com.dreamteam.view.RiskFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HomeScreenController implements ActionListener {

    private HomeScreenFrame homeView;

    public HomeScreenController(HomeScreenFrame homeView) {
        this.homeView = homeView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JButton) {
            JButton btn = (JButton) o;
            switch (btn.getActionCommand()) {
                case "N":
                    //TODO: somehow create a new game here
                    //TODO: migrate the adding of players to this screen
                    File selected = chooseDir(homeView,"./worlds/world_maps");
                    try {
                        runGame(selected);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case "L":
                    selected = chooseFile(homeView,"./worlds/saved_games");
                    try {
                        runGame(selected);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case "E":
                    System.exit(0);
                    break;
            }
        }
    }

    public static File chooseDir(JFrame parent, String path) {
        //TODO: use methods to read in the game that the user wants
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(path));
        chooser.setDialogTitle("Choose a Saved Game");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {

            System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile());
            return chooser.getSelectedFile();
        }
        return null;
    }


    public static File chooseFile(JFrame parent, String path) {
        //TODO: use methods to read in the game that the user wants
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(path));
        chooser.setDialogTitle("Choose a Saved Game");
        chooser.setMultiSelectionEnabled(false);
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileFilter(new FileNameExtensionFilter("Files ending in .save", "foo"));
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {

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


    private void runGame(File file) throws Exception {
        if (file.isDirectory()) {
            Map<String,File> contained = new HashMap<>();
            for (File f : file.listFiles()) {
                contained.put(f.getName(),f);
            }
            RiskFrame rf = new RiskFrame(contained.get("country.json"),contained.get("map.png"));
        } else {
            System.out.println("File did not denote a properly formatted directory or IO error handled.");
        }
        //the path given should not refer to a file, but instead a map folder
        homeView.dispose();

        //TODO: make a new Game instance and ask it to read itself from a file.
    }
}
