package main.view;

import main.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

/**
 * This class operates as the Controller for the game of RISK.
 * The controller registers events in the GUI and then takes input from the user,
 * and then uses that input to update the Model.
 *
 * @see Game
 *
 * The thought process behind its implementation is that two types GUI events
 * (i.e. buttons being pressed and specific points on the map board being clicked)
 * We override the method in MouseAdapter (in order to only listen to mouse "clicks".
 *
 * @see RiskFrame
 */
public class RiskController extends MouseAdapter implements ActionListener {

    private GameSingleton riskModel;
    private RiskFrame riskView;

    /**
     * Constructor for instances of the RiskController.
     * Creates a new controller that listen for certain inputs in the view, and
     * update the model with more prompted user inputs.
     *
     * @param riskModel The model to get data from and update
     * @param riskView The view to obtain
     */
    public RiskController(GameSingleton riskModel, RiskFrame riskView) {
        this.riskView=riskView;
        this.riskModel=riskModel;
    }

    public boolean inputBattle(Territory attacking, Territory defending, int attackDie, int defendDie) {
        return riskModel.battle(attacking,defending, attackDie, defendDie);
    }
    public void inputEndTurn() {

    }

    /**
     * Action listener implementation.
     * This will be added mainly to JButtons in the view.
     * Checks if the buttons are being clicked and then sets the selected option
     * field and the components in the view accordingly.
     *
     * @param e The event that was triggered
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JButton) {
            JButton jb = (JButton) o;
            if (jb.getText().equals("Attack")) {
                Territory selected = riskView.getSelectedTerritory();
                int action = riskView.getSelectedAction();
                if (selected!=null) {
                    riskView.setPointsToPaint(riskModel.getValidAttackNeighboursOwned(
                            riskModel.getCurrentPlayer(),selected));
                    jb.setText("Cancel");
                }
                riskView.setSelectedAction(1);
            } else if (jb.getText().equals("Cancel")) {
                riskView.restoreGUI();
            } else if (jb.getText().equals("End Turn")) {
                riskModel.nextPlayer();
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
        Point clicked = new Point(e.getX(),e.getY());
        Player currentPlayer = riskModel.getCurrentPlayer();

        //compare the point with others on the map to see if the user selected a territory
        Territory clickedTerritory = checkClickedTerritory(clicked);

        Territory previousTerritory = riskView.getSelectedTerritory();
        int selectedAction = riskView.getSelectedAction();

        //debug printing
        System.out.println(String.format("\nCLICK REGISTERED:\nCoordinates: (%s,%s)\nSelected Action: %s\nCurrent Territory Selected: %s\nPrevious Territory Selected: %s\n",
                clicked.x,clicked.y,selectedAction,clickedTerritory,previousTerritory));

        if (clickedTerritory==null) {
            riskView.restoreGUI();
            return;
        }

        if (clickedTerritory!=null) {
            riskView.setInfoDisplay(clickedTerritory);

            Map<Territory,Point> validAttackers = riskModel.getValidAttackNeighboursOwned(
                    currentPlayer, clickedTerritory
            );
            riskView.setAttackable(validAttackers!=null);
        }

        if (selectedAction==1) {
            if (previousTerritory!=null) {
                if (clickedTerritory!=null) {
                    //Attack was pressed
                    //Attacker Set Up
                    //Get Max Attack Die
                    int maxAttack = riskModel.getMaxBattleDie(clickedTerritory.getUnits(), true);
                    int amountOfAttackDie = JRiskOptionPane.showDieCountDialog(riskView, currentPlayer, 1, maxAttack);

                    //Defender Set Up
                    Player defendingPlayer = riskModel.getTerritoryOwner(previousTerritory);
                    //Get Max Defend Die
                    int maxDefend = riskModel.getMaxBattleDie(previousTerritory.getUnits(), false);
                    int amountOfDefendDie = JRiskOptionPane.showDieCountDialog(riskView, defendingPlayer, 1, maxDefend);

                    System.out.println(amountOfAttackDie + ":" + amountOfDefendDie);
                    boolean won = inputBattle(clickedTerritory, previousTerritory, amountOfAttackDie, amountOfDefendDie);
                    riskView.setSelectedTerritory(null);

                    riskView.setPointsToPaint(riskModel.getAllCoordinates());
                    riskView.setSelectedAction(-1);
                    riskView.setInfoDisplay(clickedTerritory);

                    //If Defending Territory Has Been Wiped Out, Start Fortifying Process
                    if (won) {
                        //Get the Number of Units the Victor wishes to move to their newly claimed territory
                        int fortifyUnits = JRiskOptionPane.showFortifyInputDialog(riskView,currentPlayer,clickedTerritory,
                                previousTerritory,amountOfAttackDie);
                        //Move chosen number of units from the attacking territory to the claimed territory and gives rightful ownership
                        riskModel.fortifyPosition(clickedTerritory,previousTerritory,fortifyUnits);
                    }
                }
            }
            riskView.restoreGUI();
        } else {
            riskView.setSelectedAction(-1);
            riskView.setSelectedTerritory(clickedTerritory);
        }

        /*
        //check if the user selected attack and has previously selected a territory
        if (selectedAction==1 && previousTerritory!=null && clickedTerritory!=null) {

            //TODO: add battling inputs here
            //Attack was pressed
                //Attacker Set Up
                //Get Max Attack Die
                int maxAttack = riskModel.getMaxBattleDie(clickedTerritory.getUnits(), true);
                int amountOfAttackDie = JRiskOptionPane.showDieCountDialog(riskView, currentPlayer, 1, maxAttack);

                //Defender Set Up
                Player defendingPlayer = riskModel.getTerritoryOwner(previousTerritory);
                //Get Max Defend Die
                int maxDefend = riskModel.getMaxBattleDie(previousTerritory.getUnits(), false);
                int amountOfDefendDie = JRiskOptionPane.showDieCountDialog(riskView, defendingPlayer, 1, maxDefend);

                boolean won = inputBattle(clickedTerritory, previousTerritory, amountOfAttackDie, amountOfDefendDie);
            riskView.setSelectedTerritory(null);

            riskView.setPointsToPaint(riskModel.getAllCoordinates());
            riskView.setSelectedAction(-1);
            riskView.setInfoDisplay(clickedTerritory);
                if (won) {
                    //TODO: add fortify logic (JRiskOptionPane.showFortifyInputDialog)
                }



        } else if (selectedAction!=-1) {
            riskView.setSelectedAction(-1);

        } else if (clickedTerritory!=null) {
            riskView.setSelectedTerritory(clickedTerritory);
        }
        */
    }

    private Territory checkClickedTerritory(Point clicked) {
        Map<Territory,Point> coords = riskView.getPointsToPaint();
        for (Territory t : coords.keySet()) {
            Point p = coords.get(t);
            if (p.distance(clicked)<20) {
                return t;
            }
        }
        return null;
    }

}
