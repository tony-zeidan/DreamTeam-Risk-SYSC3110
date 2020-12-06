package com.dreamteam.controller;

import com.dreamteam.view.HomeScreenFrame;
import com.dreamteam.view.RiskFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
                    File selected = chooseFile(homeView,"./worlds/world_maps");
                    runGame(selected);
                    break;
                case "L":
                    selected = chooseFile(homeView,"./worlds/saved_games");
                    runGame(selected);
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


    private void runGame(File file) {
        if (file!=null && file.getName().endsWith(".save")) {

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ZipFile zf = new ZipFile(file);
                ZipEntry imageEntry = zf.getEntry("map.png");
                InputStream in = zf.getInputStream(imageEntry);
                BufferedImage image = ImageIO.read(in);
                in.close();
                zf.close();
                out.close();

                ZipEntry jsonEntry = zf.getEntry("map.json");
                InputStream is = zf.getInputStream(jsonEntry);
                System.out.println(is);
                RiskFrame rf = new RiskFrame(is,image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //the path given should not refer to a file, but instead a map folder
        homeView.dispose();

        //TODO: make a new Game instance and ask it to read itself from a file.
    }
}
