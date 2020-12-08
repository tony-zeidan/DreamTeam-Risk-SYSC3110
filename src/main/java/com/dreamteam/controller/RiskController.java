package com.dreamteam.controller;

import com.dreamteam.core.*;
import com.dreamteam.view.HomeScreenFrame;
import com.dreamteam.view.JRiskOptionPane;
import com.dreamteam.view.RiskFrame;
import com.dreamteam.view.RiskMapPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * This class operates as the Controller for the game of RISK.
 * The controller registers events in the GUI and then takes input from the user,
 * and then uses that input to update the Model.
 *
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 * @author Tony Zeidan
 * @see GameSingleton
 * <p>
 * The thought process behind its implementation is that two types GUI events
 * (i.e. buttons being pressed and specific points on the map board being clicked)
 * We override the method in MouseAdapter (in order to only listen to mouse "clicks".
 * @see RiskFrame
 */
public class RiskController extends MouseAdapter implements ActionListener {

    /**
     * GameSingleton is the model of the MVC pattern
     */
    private GameSingleton riskModel;
    /**
     * RiskFrame is the com.dreamteam.view of the MVC pattern
     */
    private RiskFrame riskView;
    /**
     * Provides the previously selected action of the user.
     */
    private String selectedAction;
    /**
     * Provides the previously selected territory of the user.
     */
    private Territory selectedTerritory;

    /**
     * Constructor for instances of the RiskController.
     * Creates a new controller that listen for certain inputs in the com.dreamteam.view, and
     * update the model with more prompted user inputs.
     *
     * @param riskModel The model to get data from and update
     * @param riskView  The com.dreamteam.view to obtain
     */
    public RiskController(GameSingleton riskModel, RiskFrame riskView) {
        this.riskView = riskView;
        this.riskModel = riskModel;
        selectedAction = null;
        selectedTerritory = null;
    }

    /**
     * The current player has clicked attacked and all the circumstances are
     * met to attack
     *
     * @param attacking The territory that is attacking the defending
     * @param defending The territory that is defending from the attacking
     * @return If the attacker won the battle
     */
    public boolean inputBattle(Territory attacking, Territory defending) {
        return riskModel.performBattle(attacking, defending);
    }

    /**
     * Action listener implementation.
     * This will be added mainly to JButtons in the com.dreamteam.view.
     * Checks if the buttons are being clicked and then sets the selected option
     * field and the components in the com.dreamteam.view accordingly.
     *
     * @param e The event that was triggered
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        AudioPlayer.playSound("btnPress");
        Object o = e.getSource();
        if (o instanceof JButton) {
            JButton jb = (JButton) o;
            selectedAction = jb.getActionCommand();
            GamePhase phase = riskView.getPhase();
            switch (phase) {
                case ATTACK:
                    switch (selectedAction) {
                        case "A":
                            riskView.setCurrentInstruction("Select a territory to attack");
                            if (selectedTerritory != null) {
                                System.out.println("Worked");
                                riskModel.notifyMapUpdateAttackingNeighbourCoordinates(selectedTerritory);
                                jb.setText("Cancel");
                                jb.setActionCommand("C");
                                riskView.setEndable(false);
                            }
                            break;
                        case "C":
                            selectedTerritory = null;
                            riskView.restoreGUI();
                            riskModel.notifyMapUpdateAllCoordinates();
                            break;
                        case "E":
                            selectedTerritory = null;
                            riskModel.nextPhase();
                            break;
                    }
                    break;
                case MOVE_UNITS:
                    switch (selectedAction) {
                        case "M":
                            if (selectedTerritory != null) {
                                riskModel.notifyMapUpdateTroupeMoveCoordinate(selectedTerritory);
                                jb.setText("Cancel");
                                jb.setActionCommand("C");
                                //riskView.setEndable(false);
                            }
                            break;
                        case "C":
                            selectedTerritory = null;
                            riskView.restoreGUI();
                            riskModel.notifyMapUpdateOwnedCoordinates();
                            break;
                        case "S":
                            selectedTerritory = null;
                            riskModel.nextPlayer();
                            break;
                    }
                    break;
            }
        } else if (o instanceof JRadioButtonMenuItem) {
            JRadioButtonMenuItem fs = (JRadioButtonMenuItem) e.getSource();
            riskView.dispose();
            if (fs.isSelected()) {
                riskView.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                riskView.setPreferredSize(new Dimension(1200, 800));
            }
            riskView.setUndecorated(fs.isSelected());
            riskView.setVisible(true);
        } else if (o instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem) o;
            switch (mi.getActionCommand()) {
                case "S":
                    riskModel.export(HomeScreenController.saveFile(riskView,"./worlds/saved_games"),riskView.getMapImage(),riskView.getBonusUnits());
                    break;
                case "E":
                    //bug double frame.
                    riskModel.clean();
                    riskView.dispose();
                    HomeScreenFrame hs = new HomeScreenFrame();
                    hs.showFrame();
            }
        }
    }

    /**
     * Mouse listener (adapter) implementation.
     * This will trigger only when a mouse click is registered on the board
     * JPanel.
     * This method is used to determine what should be done after specific clicks.
     *
     * @param e The mouse event that was registered
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        //Make a point right where the user clicked
        Point clicked = new Point(e.getX(), e.getY());

        GamePhase phase = riskView.getPhase();

        //compare the point with others on the map to see if the user selected a territory
        Territory clickedTerritory = checkClickedTerritory(clicked);

        //debug printing
        System.out.println(String.format("\nCLICK REGISTERED:\nCoordinates: (%s,%s)\nSelected Action: %s\nCurrent Territory Selected: %s\nPrevious Territory Selected: %s\n",
                clicked.x, clicked.y, selectedAction, clickedTerritory, selectedTerritory));

        if(clickedTerritory != null){
            AudioPlayer.playSound("terrPress");
        }

        switch (phase) {
            case BONUS_TROUPE:
                int bonusUnits = riskView.getBonusUnits();

                /*
                Note that we could have triggered an event in the model, but didn't think
                it was worth the struggle doing too much error checking.
                 */
                if (clickedTerritory != null && clickedTerritory.getOwner() == riskModel.getCurrentPlayer()) {
                    bonusUnits -= 1;
                    riskView.setBonusUnits(bonusUnits);
                    riskModel.moveBonus(clickedTerritory);
                    if (bonusUnits == 0) {
                        riskModel.nextPhase();
                    }
                }
                break;
            case ATTACK:
                Player currentPlayer = riskModel.getCurrentPlayer();

                if (clickedTerritory != null) {
                    riskView.setInfoDisplay(clickedTerritory);
                }

                if (selectedAction != null && selectedAction.equals("A")) {
                    if (selectedTerritory != null) {
                        if (clickedTerritory != null) {

                            boolean won = inputBattle(clickedTerritory, selectedTerritory);

                            int amountOfAttackDie = currentPlayer.getDiceRoll();

                            //If Defending Territory Has Been Wiped Out, Start Fortifying Process
                            if (won) {
                                //Get the Number of Units the Victor wishes to move to their newly claimed territory
                                int fortifyUnits = JRiskOptionPane.showFortifyInputDialog(riskView, currentPlayer, clickedTerritory,
                                        selectedTerritory, amountOfAttackDie, false);
                                //Move chosen number of units from the attacking territory to the claimed territory and gives rightful ownership
                                riskModel.moveUnits(clickedTerritory, selectedTerritory, fortifyUnits);
                            }

                            //At the end of a successfully executed attack, restore the GUI defaults.
                            selectedTerritory = null;
                            selectedAction = null;
                            riskView.restoreGUI();
                        }
                        return;
                    }
                }

                if (clickedTerritory == null) {
                    selectedAction = null;
                    selectedTerritory = null;
                    riskView.restoreGUI();
                } else {
                    selectedTerritory = clickedTerritory;
                    riskModel.getValidAttackNeighboursOwned(currentPlayer, clickedTerritory);
                }
                break;
            case MOVE_UNITS:
                currentPlayer = riskModel.getCurrentPlayer();

                if (clickedTerritory != null) {
                    riskView.setInfoDisplay(clickedTerritory);
                }

                if (selectedAction != null && selectedAction.equals("M")) {
                    if (selectedTerritory != null) {
                        if (clickedTerritory != null) {
                            //Get the Number of Units the Victor wishes to move to their newly claimed territory
                            int fortifyUnits = JRiskOptionPane.showFortifyInputDialog(riskView, currentPlayer, clickedTerritory,
                                    selectedTerritory, 1, true);

                            if (fortifyUnits != -1) {

                                riskModel.moveUnits(clickedTerritory, selectedTerritory, fortifyUnits);

                                //reset GUI after moving
                                selectedTerritory = null;
                                selectedAction = null;
                                System.out.println("Worked!!");
                                riskModel.nextPlayer();
                                riskView.restoreGUI();
                                return;
                            }

                            //if an invalid territory was selected, reset the GUI to display
                            //only owned territories as we are in the movement phase
                            riskModel.notifyMapUpdateOwnedCoordinates();
                            riskView.restoreGUI();
                        }
                        return;
                    }
                }

                if (clickedTerritory == null) {
                    selectedAction = null;
                    selectedTerritory = null;
                    riskView.restoreGUI();
                } else {
                    selectedTerritory = clickedTerritory;
                    riskModel.getValidTroupeMovementTerritories(clickedTerritory);
                }
                break;
        }
        selectedTerritory = clickedTerritory;
    }



    /**
     * Checks to see if the player clicked a territory on the map
     *
     * @param clicked The x, y coordinates on the map the player clicked
     * @return Territory that was clicked
     */
    private Territory checkClickedTerritory(Point clicked) {

        //we need to downscale the point that was clicked on to the original set of points
        double x = riskView.getScalingX();
        double y = riskView.getScalingY();
        Point newpoint = new Point((int) ((clicked.getX()) / x), (int) ((clicked.getY()) / y));

        Map<Territory, Point> cords = riskView.getPointsToPaint();
        for (Territory t : cords.keySet()) {
            Point p = cords.get(t);

            Point translatedCheck = new Point(p.x + RiskMapPane.INNER_POINT_RADIUS,
                    p.y + RiskMapPane.INNER_POINT_RADIUS);
            if (translatedCheck.distance(newpoint) <= RiskMapPane.HIT_POINT_RADIUS) {
                return t;
            }
        }
        return null;
    }
}
