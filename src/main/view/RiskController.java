package main.view;

import main.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class RiskController extends MouseAdapter implements ActionListener {

    private Game riskModel;
    private RiskFrame riskView;

    public RiskController(Game riskModel, RiskFrame riskView) {
        this.riskView=riskView;
        this.riskModel=riskModel;
    }

    public void inputBattle(Territory attacking, Territory defending, int attackDie, int defendDie) {
        riskModel.battle(attacking,defending, attackDie, defendDie);
    }
    public void inputEndTurn() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JButton) {
            JButton jb = (JButton) o;
            if (jb.getText().equals("Attack")) {
                Territory selected = riskView.getSelectedTerritory();
                if (selected!=null) {
                    riskView.setPointsToPaint(riskModel.getValidAttackNeighboursOwned(
                            riskModel.getCurrentPlayer(),selected));
                    jb.setText("Cancel");
                }
                riskView.setSelectedAction(1);
            } else if (jb.getText().equals("Cancel")) {
                riskView.setPointsToPaint(riskModel.getAllCoordinates());
                riskView.setSelectedAction(-1);
                jb.setText("Attack");
            } else if (jb.getText().equals("End Turn")) {
                riskModel.nextPlayer();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clicked = new Point(e.getX(),e.getY());
        Player currentPlayer = riskModel.getCurrentPlayer();

        //System.out.println(clicked.getX()+":"+clicked.getY());
        Territory clickedTerritory = checkClickedTerritory(clicked);
        Territory previousTerritory = riskView.getSelectedTerritory();
        int selectedAction = riskView.getSelectedAction();

        System.out.println(String.format("\nCLICK REGISTERED:\nCoordinates: (%s,%s)\nSelected Action: %s\nCurrent Territory Selected: %s\nPrevious Territory Selected: %s\n",
                clicked.x,clicked.y,selectedAction,clickedTerritory,previousTerritory));

        if (clickedTerritory!=null) {
            riskView.setInfoDisplay(clickedTerritory);

            Map<Territory,Point> validAttackers = riskModel.getValidAttackNeighboursOwned(
                    currentPlayer, clickedTerritory
            );
            riskView.setAttackable(validAttackers!=null);
        }

        //check if the user selected attack and has previously selected a territory
        if (selectedAction==1 && previousTerritory!=null && clickedTerritory!=null) {

            //TODO: add battling inputs here
            Object[] beforeBattleOptions = {"March Forward", "Retreat"};
            int beforeBattleChoice = JOptionPane.showOptionDialog(riskView,"Are you going to attack? or retreat?", "Before Battle",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,beforeBattleOptions,"1");
            //Attack was pressed
            if(beforeBattleChoice == JOptionPane.YES_OPTION){
                //Attacker Set Up
                //Get Max Attack Die
                int maxAttack = riskModel.getMaxBattleDie(clickedTerritory.getUnits(), true);
                int amountOfAttackDie = JRiskOptionPane.showDieCountDialog(riskView, currentPlayer, 1, maxAttack);

                //Defender Set Up
                Player defendingPlayer = riskModel.getTerritoryOwner(previousTerritory);
                //Get Max Defend Die
                int maxDefend = riskModel.getMaxBattleDie(previousTerritory.getUnits(), false);
                int amountOfDefendDie = JRiskOptionPane.showDieCountDialog(riskView, defendingPlayer, 1, maxDefend);

                inputBattle(clickedTerritory, previousTerritory, amountOfAttackDie, amountOfDefendDie);
                riskView.setSelectedTerritory(null);
            }

            riskView.setPointsToPaint(riskModel.getAllCoordinates());
            riskView.setSelectedAction(-1);
            riskView.setInfoDisplay(clickedTerritory);
        } else if (selectedAction!=-1) {
            riskView.setSelectedAction(-1);

        } else if (clickedTerritory!=null) {
            riskView.setSelectedTerritory(clickedTerritory);
        }
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
