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
                riskView.setSelectedAction(1);
            } else if (jb.getText().equals("World State")) {
                riskView.setSelectedAction(2);
            } else if (jb.getText().equals("End Turn")) {
                riskView.setSelectedAction(3);
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

        }
        //check if the user selected attack and has previously selected a territory
        if (selectedAction==1 && riskView.getSelectedTerritory()!=null) {
            int riskAction = riskView.getSelectedAction();
            if (riskAction==1) {
                //TODO: add battling inputs here
                inputBattle(clickedTerritory,riskView.getSelectedTerritory());
            }
            riskView.setSelectedAction(-1);
            riskView.setSelectedTerritory(null);
        } else if (selectedAction!=-1) {
            riskView.setSelectedAction(-1);

        } else if (clickedTerritory!=null) {
            riskView.setSelectedTerritory(clickedTerritory);
        }
    }

    private Territory checkClickedTerritory(Point clicked) {
        Map<Territory,Point> coords = riskModel.getAllCoordinates();
        for (Territory t : coords.keySet()) {
            Point p = coords.get(t);
            if (p.distance(clicked)<20) {
                return t;
            }
        }
        return null;
    }

}
