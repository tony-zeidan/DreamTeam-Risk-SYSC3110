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
 * @see GameSingleton
 * <p>
 * The thought process behind its implementation is that two types GUI events
 * (i.e. buttons being pressed and specific points on the map board being clicked)
 * We override the method in MouseAdapter (in order to only listen to mouse "clicks".
 * @see RiskFrame
 *
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 * @author Tony Zeidan
 */
public class RiskController extends MouseAdapter implements ActionListener {

    /**
     * GameSingleton is the model of the MVC pattern
     */
    private GameSingleton riskModel;
    /**
     * RiskFrame is the view of the MVC pattern
     */
    private RiskFrame riskView;
    /**
     * CLICK_DISTANCE is the minimum distance away from point
     * to register a click
     */
    private static final int CLICK_DISTANCE = 20;

    /**
     * Constructor for instances of the RiskController.
     * Creates a new controller that listen for certain inputs in the view, and
     * update the model with more prompted user inputs.
     *
     * @param riskModel The model to get data from and update
     * @param riskView  The view to obtain
     */
    public RiskController(GameSingleton riskModel, RiskFrame riskView) {
        this.riskView = riskView;
        this.riskModel = riskModel;
    }

    /**
     * The current player has clicked attacked and all the circumstances are
     * met to attack
     *
     * @param attacking The territory that is attacking the defending
     * @param defending The territory that is defending from the attacking
     * @param attackDie The amount of die the attacking territory can use
     * @param defendDie The amount of die the defending territory can use
     * @return If the attacker won the battle
     */
    public boolean inputBattle(Territory attacking, Territory defending, int attackDie, int defendDie) {
        return riskModel.battle(attacking, defending, attackDie, defendDie);
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
                riskView.setCurrentInstruction("Select a territory to attack");
                Territory selected = riskView.getSelectedTerritory();
                if (selected != null) {
                    riskModel.notifyMapUpdateAttackingNeighbourCoordinates(selected);
                    jb.setText("Cancel");
                    riskView.setEndable(false);
                }
                riskView.setSelectedAction(1);
            } else if (jb.getText().equals("Cancel")) {
                riskView.restoreGUI();
                riskModel.notifyMapUpdateAllCoordinates();
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
        Point clicked = new Point(e.getX(), e.getY());
        Player currentPlayer = riskModel.getCurrentPlayer();

        //compare the point with others on the map to see if the user selected a territory
        Territory clickedTerritory = checkClickedTerritory(clicked);

        Territory previousTerritory = riskView.getSelectedTerritory();
        int selectedAction = riskView.getSelectedAction();

        //debug printing
        System.out.println(String.format("\nCLICK REGISTERED:\nCoordinates: (%s,%s)\nSelected Action: %s\nCurrent Territory Selected: %s\nPrevious Territory Selected: %s\n",
                clicked.x, clicked.y, selectedAction, clickedTerritory, previousTerritory));

        if (clickedTerritory == null) {
            riskView.restoreGUI();
            riskModel.notifyMapUpdateAllCoordinates();
            return;
        }

        if (clickedTerritory != null) {
            riskView.setInfoDisplay(clickedTerritory);
            /*
            We access model data here, only to prevent the user from clicking anything that
            they shouldn't be. This could be substituted for for a user dialog that lets the user know
            they can not perform that action. We went with this approach as it provides smoother gameplay.
             */
            riskModel.getValidAttackNeighboursOwned(currentPlayer, clickedTerritory);
        }

        if (selectedAction == 1) {
            if (previousTerritory != null) {
                if (clickedTerritory != null) {
                    attacking(clickedTerritory,previousTerritory);
                }
            }
            riskView.restoreGUI();
            riskModel.notifyMapUpdateAllCoordinates();
        } else {
            riskView.setSelectedAction(-1);
            riskView.setSelectedTerritory(clickedTerritory);
        }
    }

    /**
     * Checks to see if the player clicked a territory on the map
     *
     * @param clicked The x, y coordinates on the map the player clicked
     * @return Territory that was clicked
     */
    private Territory checkClickedTerritory(Point clicked) {
        double x = riskView.getScalingX();
        double y = riskView.getScalingY();
        Point newpoint = new Point((int) (clicked.getX() / x), (int) (clicked.getY() / y));
        Map<Territory, Point> cords = riskView.getPointsToPaint();
        for (Territory t : cords.keySet()) {
            Point p = cords.get(t);
            if (p.distance(newpoint) < CLICK_DISTANCE) {
                return t;
            }
        }
        return null;
    }
    public void attacking(Territory attacking,Territory defending)
    {
        //Attack was pressed
        //Attacker Set Up
        //Get Max Attack Die
        Player currentPlayer=attacking.getOwner();
        int amountOfAttackDie;
        int attackingNumUnits =attacking.getUnits();
        if(currentPlayer instanceof AIPlayer)
        {
            amountOfAttackDie = (attackingNumUnits>4)?3:attackingNumUnits-1;
        }
        else
        {
            int maxAttack = riskModel.getMaxBattleDie(attacking.getUnits(), true);
            amountOfAttackDie = JRiskOptionPane.showDieCountDialog(riskView, currentPlayer, 1, maxAttack,
                    "You are attacking "+ defending.getName()+" from "+attacking.getName()+". ");
        }

        //Defender Set Up
        Player defendingPlayer = defending.getOwner();
        int amountOfDefendDie;
        if (defendingPlayer instanceof AIPlayer)
        {
            amountOfDefendDie = (defending.getUnits()==1)?1:2;
        }
        else
        {
            //Get Max Defend Die
            int maxDefend = riskModel.getMaxBattleDie(defending.getUnits(), false);
            amountOfDefendDie = JRiskOptionPane.showDieCountDialog(riskView, defendingPlayer, 1, maxDefend,
                    "You are Defending "+ defending.getName()+" from "+attacking.getName()+". ");
        }

        boolean won = inputBattle(attacking, defending, amountOfAttackDie, amountOfDefendDie);
        riskView.setSelectedTerritory(null);
        // riskModel.updateViewAllPoints();

        riskView.setSelectedAction(-1);
        riskView.setInfoDisplay(attacking);

        //If Defending Territory Has Been Wiped Out, Start Fortifying Process
        if (won) {
            int fortifyUnits;
            if (currentPlayer instanceof AIPlayer)
            {
                //Get the Number of Units the Victor wishes to move to their newly claimed territory
                fortifyUnits =attackingNumUnits -1;
            }
            else
            {
                //Get the Number of Units the Victor wishes to move to their newly claimed territory
                fortifyUnits = JRiskOptionPane.showFortifyInputDialog(riskView, currentPlayer, attacking,
                        defending, amountOfAttackDie, false);
                //Move chosen number of units from the attacking territory to the claimed territory and gives rightful ownership
            }
            //Move chosen number of units from the attacking territory to the claimed territory and gives rightful ownership
            riskModel.fortifyPosition(attacking, defending, fortifyUnits);
        }
    }
}
