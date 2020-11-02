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

    public void inputBattle(Territory attacking, Territory defending) {
        riskModel.battle(attacking,defending);
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
                    riskView.setPointsToPaint(riskModel.getNeighbouringNodes(selected));
                }
                riskView.setSelectedAction(1);
            } else if (jb.getText().equals("World State")) {
                riskView.setSelectedAction(2);
            } else if (jb.getText().equals("End Turn")) {
                riskModel.nextPlayer();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clicked = new Point(e.getX(),e.getY());

        //System.out.println(clicked.getX()+":"+clicked.getY());
        Territory clickedTerritory = checkClickedTerritory(clicked);
        int selectedAction = riskView.getSelectedAction();

        System.out.println(clickedTerritory);
        System.out.println(riskView.getSelectedAction());

        if (clickedTerritory!=null) {
            riskView.setInfoDisplay(clickedTerritory);
        }

        //check if the user selected attack and has previously selected a territory
        if (selectedAction==1 && riskView.getSelectedTerritory()!=null && clickedTerritory!=null) {

            //TODO: add battling inputs here
            Object[] beforeBattleOptions = {"March Forward", "Retreat"};
            int beforeBattleChoice = JOptionPane.showOptionDialog(riskView,"Are you going to attack? or retreat?", "Before Battle",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,beforeBattleOptions,"1");
            //Attack was pressed
            if(beforeBattleChoice == JOptionPane.YES_OPTION){
                Player playerCurrent = riskModel.getCurrentPlayer();
                int amountOfAttackDie = JRiskOptionPane.showDieCountDialog(riskView, playerCurrent, 1,
                        riskModel.getMaximumDieCount(clickedTerritory.getUnits(),true));
                int amountOfDefendDie = JRiskOptionPane.showDieCountDialog(riskView, defendingPlayer, 1,
                        riskModel.getMaximumDieCount(riskView.getSelectedTerritory().getUnits(),false));
            }
            //Retreat was pressed
            else{

            }
            inputBattle(clickedTerritory,riskView.getSelectedTerritory());

            riskView.setPointsToPaint(riskModel.getAllCoordinates());

            riskView.setSelectedAction(-1);
            riskView.setSelectedTerritory(null);
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
